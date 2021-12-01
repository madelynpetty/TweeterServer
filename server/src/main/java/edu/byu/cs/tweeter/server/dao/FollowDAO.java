package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard().withRegion("us-west-2").build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String tableName = "follow";
    private final String indexName = "userAlias-follower-index";
    private static Table followTable = dynamoDB.getTable(tableName);
    private static final String partitionKey = "userAlias";
    private static final String sortKey = "follower";

    public FollowResponse follow(FollowRequest request) {
        //todo add image
        Item item = new Item()
                .withPrimaryKey(partitionKey, request.getUser().getAlias())
                .withString(sortKey, request.getCurrUser().getAlias());
//                .withString("userAliasImage", request.getUser().getImageUrl())
//                .withString("followerImage", request.getCurrUser().getImageUrl());

        followTable.putItem(item);
        return new FollowResponse(true);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
//        Map<String, String> attrNames = new HashMap<String, String>();
//        attrNames.put("#aliasName", partitionKey);
//        attrNames.put("#followerName", secondColumnKey);
//
//        Map<String, AttributeValue> attrValues = new HashMap<>();
//        attrValues.put(":" + partitionKey,
//                new AttributeValue().withS(request.getUser().getAlias()));
//        attrValues.put(":" + secondColumnKey,
//                new AttributeValue().withS(request.getCurrUser().getAlias()));
//
//        QueryRequest queryRequest = new QueryRequest()
//                .withTableName(tableName)
//                .withIndexName(indexName)
//                .withKeyConditionExpression("#aliasName = :" + partitionKey +
//                        " AND #followerName = :" + secondColumnKey)
//                .withExpressionAttributeNames(attrNames)
//                .withExpressionAttributeValues(attrValues);
//
//
//        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
//        List<Map<String, AttributeValue>> items = queryResult.getItems();
//
//        for (Map<String, AttributeValue> item : items) {
//            Set<String> partitionSet = item.keySet();
//            for (String s : partitionSet) {
//                String partition = s;
//                AttributeValue secondColumn = item.get(partition);
//                followTable.deleteItem(partition, secondColumn);
//            }
//        }

        ScanSpec scanSpec = new ScanSpec()
                .withProjectionExpression("#aliasName, " + sortKey)
                .withFilterExpression("#aliasName = :aliasVal AND " + sortKey + " = :followerVal")
                .withNameMap(new NameMap().with("#aliasName", partitionKey))
                .withValueMap(new ValueMap().withString(":aliasVal",
                        request.getUser().getAlias()))
                .withValueMap(new ValueMap().withString(":followerVal",
                        request.getCurrUser().getAlias()));

        try {
            ItemCollection<ScanOutcome> items = followTable.scan(scanSpec);

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                String aliasVal = item.getString(partitionKey);
                String followerVal = item.getString(sortKey);

                DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
                        new PrimaryKey(partitionKey, aliasVal, sortKey, followerVal));
                followTable.deleteItem(deleteItemSpec);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan the follow table: " + e.getMessage());
        }
        return new UnfollowResponse(true);
    }

    public FollowerCountResponse getFollowerCount(User follower) {
        assert follower != null;
        FollowerCountResponse response = new FollowerCountResponse(
                getFollowersList(follower.getAlias()).size());
        return response;
    }

    public FollowingCountResponse getFollowingCount(User followee) {
        assert followee != null;
        FollowingCountResponse response = new FollowingCountResponse(
                getFollowingList(followee.getAlias()).size());
        return response;
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#aliasName", partitionKey);
        attrNames.put("#followerName", sortKey);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(request.getFollowee().getAlias()));
        attrValues.put(":" + sortKey, new AttributeValue().withS(request.getFollower().getAlias()));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#aliasName = :" + partitionKey +
                        "#followerName = :" + sortKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        boolean isFollower = false;

        if(!items.isEmpty()) isFollower = true;

        return new IsFollowerResponse(isFollower);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        System.out.println("FOLLOWER REQUEST: ");
        System.out.println("FOLLOWER: " + request.getFollowerAlias());
        System.out.println("LAST FOLLOWER: " + request.getLastFolloweeAlias());

        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        if (request.getLastFolloweeAlias() == null) {
            List<User> users = new ArrayList<>();
            return new FollowingResponse(users, false);
        }

        List<User> allFollowees = getFollowingList(request.getFollowerAlias());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFollowStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    /**
     * Determines the index for the first followee/follower in the specified 'allFollows' list that should
     * be returned in the current request. This will be the index of the next followee/follower after the
     * specified 'lastAlias'.
     *
     * @param lastAlias the alias of the last followee/follower that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollows the generated list of followees/followers from which we are returning paged results.
     * @return the index of the first followee/follower to be returned.
     */
    private int getFollowStartingIndex(String lastAlias, List<User> allFollows) {

        int followIndex = 0;

        if(lastAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollows.size(); i++) {
                if(lastAlias.equals(allFollows.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followIndex = i + 1;
                    break;
                }
            }
        }

        return followIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getFollowingList(String userAlias) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#aliasName", partitionKey);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(userAlias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#aliasName = :" + partitionKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        List<User> followers = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Set<String> partitionSet = item.keySet();
            for (String alias : partitionSet) {
                User user = UserDAO.getUserFromAlias(alias);

                if (user != null) {
                    System.out.println("USER IS NOT NULL: " + user.getAlias());
                    followers.add(user);
                }
                else {
                    System.out.println("USER IS NULL");
                }
            }
        }

        return followers;
    }

    List<User> getFollowersList(String userAlias) {
        //CURRENT USER IS THE FOLLOWER

        List<User> followers = new ArrayList<>();

        ScanSpec scanSpec = new ScanSpec()
                .withProjectionExpression("#aliasName, " + sortKey)
                .withFilterExpression("#aliasName = :aliasVal")
                .withNameMap(new NameMap().with("#aliasName", partitionKey))
                .withValueMap(new ValueMap().withString(":aliasVal", userAlias));

        try {
            ItemCollection<ScanOutcome> items = followTable.scan(scanSpec);

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                String aliasVal = item.getString(sortKey);
                User user = UserDAO.getUserFromAlias(aliasVal);
                if (user != null) {
                    System.out.println("USER IS NOT NULL: " + user.getAlias());
                    followers.add(user);
                }
                else {
                    System.out.println("USER IS NULL");
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan the follow table: " + e.getMessage());
        }
        return followers;
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowerResponse getFollowers(FollowerRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollower().getAlias() != null;
        if (request.getLastFollower() == null) {
            List<User> users = new ArrayList<>();
            return new FollowerResponse(users, false);
        }

        List<User> allFollowers = getFollowersList(request.getFollower().getAlias());
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followeesIndex = getFollowStartingIndex(request.getLastFollower().getAlias(), allFollowers);

                for(int limitCounter = 0; followeesIndex < allFollowers.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowers.size();
            }
        }

        return new FollowerResponse(responseFollowers, hasMorePages);
    }
}
