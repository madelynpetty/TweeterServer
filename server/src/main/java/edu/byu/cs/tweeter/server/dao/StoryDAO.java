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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StoryDAO implements StoryDAOInterface {
    private final String tableName = "story";
    private final String indexName = "senderAlias-storytime-index";
    private Table storyTable = DynamoDbFactory.getDynamoDB().getTable(tableName);
    private static final String partitionKey = "senderAlias";
    private static final String sortKey = "storytime";

    public StoryDAO() {}

    @Override
    public List<Status> getStory(int limit, String userAlias) {
        assert limit > 0;
        assert userAlias != null;

        List<Status> allStatuses = getFollowsStatuses(userAlias);
        List<Status> responseStatuses = new ArrayList<>(limit);

        if(limit > 0) {
            if (allStatuses != null) {
                int statusesIndex = getStoryStartingIndex(userAlias, allStatuses);

                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < limit; statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }
            }
        }

        return responseStatuses;
    }

    private int getStoryStartingIndex(String lastStatus, List<Status> allStatuses) {

        int statusIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i).getPost())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }

        return statusIndex;
    }

    @Override
    public List<Status> getFollowsStatuses(String alias) {

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#aliasName", partitionKey);
        attrNames.put("#timeName", sortKey);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(alias));
        attrValues.put(":" + sortKey, new AttributeValue().withS(dtf.format(now)));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#aliasName = :" + partitionKey +
                        " AND #timeName < :" + sortKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        queryRequest.setScanIndexForward(true);

        QueryResult queryResult = DynamoDbFactory.getAmazonDynamoDB().query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        Status status = null;
        List<Status> statuses = new ArrayList<>();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                String userAlias = item.get(partitionKey).getS();
                User user = UserDAO.getUserFromAlias(userAlias);

                String post = item.get("post").getS();
                String datetime = item.get(sortKey).getS();

                List<String> urls = getUrlsInPost(post);
                List<String> mentions = getMentionsInPost(post);

                status =  new Status(post, user, datetime, urls, mentions);
                statuses.add(status);
            }
        }
        else {
            throw new RuntimeException("There are no statuses to show in the user's story. They may not " +
                    "be following anyone.");
        }

        return statuses;
    }

    @Override
    public boolean postStatus(String currUserAlias, String post) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            Item item = new Item()
                    .withPrimaryKey(partitionKey, currUserAlias)
                    .withString("post", post)
                    .withString(sortKey, dtf.format(now));

            storyTable.putItem(item);

        }
        catch (DuplicateItemException e) {
            System.out.println("Duplicate Item Exception: " + e.getMessage());
            throw new RuntimeException("Duplicate Item Exception:" + e.getMessage());
        }

        return true;
    }

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private List<String> getUrlsInPost(String post) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = urlPattern.matcher(post);

        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            urls.add(post.substring(matchStart, matchEnd));
        }

        System.out.println("URLS: ");
        for (String s : urls) {
            System.out.println("URL: " + s);
        }

        return urls;
    }

    private List<String> getMentionsInPost(String post) {
        List<String> mentions = new ArrayList<>();
        String[] words = post.split(" ");

        for (String s : words) {
            if (s.charAt(0) == '@') {
                mentions.add(s);
            }
        }

        System.out.println("MENTIONS: ");
        for (String s : mentions) {
            System.out.println("MENTION: " + s);
        }

        return mentions;
    }
}
