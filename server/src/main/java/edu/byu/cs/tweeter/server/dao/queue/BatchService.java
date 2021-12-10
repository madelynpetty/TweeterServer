package edu.byu.cs.tweeter.server.dao.queue;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostUpdateFeedRequest;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDbFactory;

public class BatchService {
    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    private final FollowDAOInterface followDAOInterface = DAOFactory.getInstance().getFollowDAO();

    private static final String feedTableName = "feed2";
    private static final String feedPartitionKey = "receiverAlias";
    private static final String feedSortKey = "feedtime";

    public void postUpdateFeed(Status status) {
        List<String> currUserFolloweeList = followDAOInterface.getFollowersAliasList(status.user.getAlias());
        List<String> batch = new ArrayList<>();

        for (String follower : currUserFolloweeList) {
            batch.add(follower);

            if (batch.size() == 25) {
                PostUpdateFeedRequest request = new PostUpdateFeedRequest(batch, status);
                sqs.sendMessage(
                        "https://sqs.us-west-2.amazonaws.com/851652515100/UpdateFeedQueue",
                        (new Gson()).toJson(request));
                batch.clear();
            }
        }

        // Write any leftover items
        if (batch.size() > 0) {
            PostUpdateFeedRequest request = new PostUpdateFeedRequest(batch, status);
            sqs.sendMessage("https://sqs.us-west-2.amazonaws.com/851652515100/UpdateFeedQueue", (new Gson()).toJson(request));
            batch.clear();
        }
    }

    public void updateFeedTable(PostUpdateFeedRequest request) {
        TableWriteItems items = new TableWriteItems(feedTableName);
        List<String> users = request.getFollowers();

        for (String user : users) {
            Item item = new Item()
                    .withPrimaryKey(feedPartitionKey, user)
                    .withString("post", request.getStatus().getPost())
                    .withString("senderAlias", request.getStatus().getUser().getAlias())
                    .withString(feedSortKey, request.getStatus().getDate());

            items.addItemToPut(item);

//            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
//                loopBatchWrite(items);
//                items = new TableWriteItems(feedTableName);
//            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {
        BatchWriteItemOutcome outcome = DynamoDbFactory.getDynamoDB().batchWriteItem(items);

        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = DynamoDbFactory.getDynamoDB().batchWriteItemUnprocessed(unprocessedItems);
        }
    }
}
