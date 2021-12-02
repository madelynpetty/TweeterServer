package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.xspec.S;

import org.w3c.dom.Attr;

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
    private static AmazonDynamoDB amazonDynamoDB = UserDAO.getAmazonDynamoDB();
    private static DynamoDB dynamoDB = UserDAO.getDynamoDB();
    private static final String tableName = "follow";
    private final String indexName = "follower-userAlias-index";
    private final String indexName2 = "userAlias-follower-index";
    private static Table followTable = dynamoDB.getTable(tableName);
    private static final String partitionKey = "userAlias";
    private static final String sortKey = "follower";

    public FollowResponse follow(FollowRequest request) {
        Item item = new Item()
                .withPrimaryKey(partitionKey, request.getUser().getAlias())
                .withString(sortKey, request.getCurrUser().getAlias());

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
        //how many people are following me, the current user
        assert follower != null;
        List<User> followerList = getFollowersList(follower.getAlias());
        if (followerList != null) {
            return new FollowerCountResponse(followerList.size());
        }
        else {
            return new FollowerCountResponse(0);
        }
    }

    public FollowingCountResponse getFollowingCount(User followee) {
        // how many people the logged in user is following
        assert followee != null;
        List<User> followeeList = getFollowingList(followee.getAlias());
        if (followeeList != null) {
            return new FollowingCountResponse(followeeList.size());
        }
        else {
            return new FollowingCountResponse(0);
        }
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
        System.out.println("FOLLOWER: " + request.getLoggedInUserAlias());
        System.out.println("LAST FOLLOWER: " + request.getLastFolloweeAlias());

        assert request.getLimit() > 0;
        assert request.getLoggedInUserAlias() != null;

        // todo request.getLoggedInUser is null
        List<User> allFollowees = getFollowingList(request.getLoggedInUserAlias());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;
        User lastFollowee = null;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFollowStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
                lastFollowee = allFollowees.get(followeesIndex - 1);
                request.setLastFolloweeAlias(lastFollowee.getAlias());
            }
        }

        return new FollowingResponse(responseFollowees, lastFollowee, hasMorePages);
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

    // people that I follow
    // logged in user is in the sort key
    List<User> getFollowingList(String userAlias) {
        // todo passing in a null alias!
        System.out.println("FOLLOWING REQUEST: ");
        System.out.println("PASSED IN: " + userAlias);

        List<User> followingList = new ArrayList<>();

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(sortKey + " = :aliasVal")
                .withValueMap(new ValueMap().withString(":aliasVal", userAlias));

        try {
            Index index = followTable.getIndex(indexName);
            ItemCollection<QueryOutcome> items = index.query(querySpec);

            for (Item item : items) {
                String aliasVal = item.getString(partitionKey);
                System.out.println("Alias we got from database: " + aliasVal);
                User user = UserDAO.getUserFromAlias(aliasVal);
                if (user != null) {
                    System.out.println("USER IS NOT NULL: " + user.getAlias());
                    followingList.add(user);
                } else {
                    System.out.println("USER IS NULL");
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan the follow table: " + e.getMessage());
        }
        return followingList;
    }

    // people that are following me
    // logged in user is in the primary key
    List<User> getFollowersList(String userAlias) {
        System.out.println("Passed in alias: " + userAlias);
//        Map<String, String> attrNames = new HashMap<>();
//        attrNames.put("#aliasName", partitionKey);
//        attrNames.put("#followerName", sortKey);
//
//        Map<String, AttributeValue> attrValues = new HashMap<>();
//        attrValues.put(":" + partitionKey, new AttributeValue().withS(userAlias));
//        attrValues.put(":" + sortKey, new AttributeValue().withS());
//
//        QueryRequest queryRequest = new QueryRequest()
//                .withTableName(tableName)
//                .withIndexName(indexName)
//                .withKeyConditionExpression("#aliasName = :userAlias")
//                .withExpressionAttributeNames(attrNames)
//                .withExpressionAttributeValues(attrValues);
//
//        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
//        List<Map<String, AttributeValue>> items = queryResult.getItems();
        List<User> followerList = new ArrayList<>();
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(partitionKey + " = :aliasVal")
                .withValueMap(new ValueMap().withString(":aliasVal", userAlias));

//        Index index = followTable.getIndex(indexName2);
//        ItemCollection<QueryOutcome> items = index.query(querySpec);

        ItemCollection<QueryOutcome> items = followTable.query(querySpec);

        if (items != null) {
            for (Item item : items) {
                System.out.println("here is the alias: " + item.get(sortKey));
                User user = UserDAO.getUserFromAlias(item.get(sortKey).toString());
                if (user != null) {
                    System.out.println("USER IS NOT NULL: " + user.getAlias());
                    followerList.add(user);
                } else {
                    System.out.println("USER IS NULL");
                }
            }
        }

        return followerList;
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
        assert request.getFollowerAlias() != null;
        System.out.println("Alias sent in: " + request.getFollowerAlias());

        List<User> allFollowers = getFollowersList(request.getFollowerAlias());
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;
        User lastFollower = null;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followeesIndex = getFollowStartingIndex(request.getLastFollowerAlias(), allFollowers);

                for(int limitCounter = 0; followeesIndex < allFollowers.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowers.size();
                lastFollower = allFollowers.get(followeesIndex - 1);
                request.setLastFollowerAlias(lastFollower.getAlias());
            }
        }

        return new FollowerResponse(responseFollowers, lastFollower, hasMorePages);
    }
}
