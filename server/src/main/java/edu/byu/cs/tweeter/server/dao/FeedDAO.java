package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DuplicateItemException;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FeedDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard().withRegion("us-west-2").build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private final String tableName = "feed";
    private final String indexName = "feed-index";
    private Table feedTable = dynamoDB.getTable(tableName);
    private static final String partitionKey = "receiverAlias";
    private static final String sortKey = "timestamp";

    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getFollowsStatuses(request);
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusesIndex = getFeedStartingIndex(request.getUserAlias(), allStatuses);

                for (int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex < allStatuses.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }

    private int getFeedStartingIndex(String lastStatus, List<Status> allStatuses) {

        int statusIndex = 0;

        if (lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if (lastStatus.equals(allStatuses.get(i).getPost())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }

        return statusIndex;
    }

    List<Status> getFollowsStatuses(FeedRequest request) {
        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#a", ":" + partitionKey);
        attrNames.put("#t", ":" + sortKey);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(request.getUserAlias()));
        attrValues.put(":" + sortKey, new AttributeValue());

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#a = :" + partitionKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        Status status = null;
        List<Status> statuses = new ArrayList<>();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                String userAlias = item.get("alias").getS();
                User user = UserDAO.getUserFromAlias(userAlias);

                String post = item.get("post").getS();
                String datetime = item.get("datetime").getS();
                List<String> urls = item.get("urls").getNS();
                List<String> mentions = item.get("mentions").getNS();

                status =  new Status(post, user, datetime, urls, mentions);
                statuses.add(status);
            }
        }
        else {
            System.out.println("There are no statuses to show in the user's feed. They may not " +
                    "be following anyone.");
        }

        return statuses;
    }
}