package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDbFactory;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FeedDAO implements FeedDAOInterface {
    private static final String tableName = "feed";
    private final String indexName = "receiverAlias-feedtime-index";
    private static Table feedTable = DynamoDbFactory.getDynamoDB().getTable(tableName);
    private static final String partitionKey = "receiverAlias";
    private static final String sortKey = "feedtime";

    public FeedDAO() {}

    @Override
    public List<Status> getFeed(int limit, String alias, Status lastStatus) {
        assert limit > 0;
        assert alias != null;

        List<Status> responseStatuses = new ArrayList<>(limit);
        List<Status> allStatuses = getFollowsStatuses(alias);

        if (limit > 0) {
            if (allStatuses != null) {
                int statusesIndex = getFeedStartingIndex(alias, allStatuses);

                for (int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < limit; statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }
            }
        }

        return responseStatuses;
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

    @Override
    public List<Status> getFollowsStatuses(String userAlias) {
        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#aliasName", partitionKey);
        attrNames.put("#timeName", sortKey);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(userAlias));
        attrValues.put(":" + sortKey, new AttributeValue().withS(dtf.format(now)));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#aliasName = :" + partitionKey +
                        " AND #timeName < :" + sortKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        queryRequest.setScanIndexForward(false);

        QueryResult queryResult = DynamoDbFactory.getAmazonDynamoDB().query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        Status status = null;
        List<Status> statuses = new ArrayList<>();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                String alias = item.get("senderAlias").getS();
                User user = UserDAO.getUserFromAlias(alias);

                String post = item.get("post").getS();
                String datetime = item.get(sortKey).getS();

                List<String> urls = getUrlsInPost(post);
                List<String> mentions = getMentionsInPost(post);

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

//    @Override
//    public void postStatus(String post, String senderAlias, List<User> currUserFolloweeList) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//
//        for (User user : currUserFolloweeList) {
//            Item item = new Item()
//                    .withPrimaryKey(partitionKey, user.getAlias())
//                    .withString("post", post)
//                    .withString("senderAlias", senderAlias)
//                    .withString(sortKey, dtf.format(now));
//            feedTable.putItem(item);
//        }
//    }

    @Override
    public boolean sendFeedMessage(String post, User currUser) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Status status = new Status(post, currUser, dtf.format(now),
                getUrlsInPost(post), getMentionsInPost(post));

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl("https://sqs.us-west-2.amazonaws.com/851652515100/PostStatusQueue")
                .withMessageBody((new Gson()).toJson(status))
                .withDelaySeconds(5);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);

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