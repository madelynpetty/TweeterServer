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
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthTokenDAO {
    private static DynamoDB dynamoDB = UserDAO.getDynamoDB();
    private static final String tableName = "authTokenTable";
    private final String indexName = "authTokenTimeStamp-authToken-index";
    private static Table authTokenTable = dynamoDB.getTable(tableName);
    private static final String partitionKey = "authToken";
    private static final String sortKey = "authTokenTimeStamp";

    public AuthToken getNewAuthToken(String userAlias) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        AuthToken authToken = new AuthToken();

        Item item = new Item()
                .withPrimaryKey(partitionKey, authToken.getIdentifier())
                .withString("userAlias", userAlias)
                .withString(sortKey, dtf.format(now));

        authTokenTable.putItem(item);
        return authToken;
    }

    public void checkValidAuthTokens() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime cutOff = LocalDateTime.now().minusMinutes(30);
            // although this is 30 minutes ago, this actually is a day and a
            // half ago due to aws not allowing me to change my timezone.
/*
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(sortKey + " < :timeVal")
                .withValueMap(new ValueMap().withString(":timeVal", dtf.format(cutOff)));

        try {
            Index index = authTokenTable.getIndex(indexName);
            ItemCollection<QueryOutcome> items = index.query(querySpec);

            for (Item item : items) {
                String timeVal = item.getString(partitionKey);
                if (timeVal != null) {
                    String authTokenValue = item.getString(partitionKey);
                    String date = item.getString(sortKey);

                    DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                            .withPrimaryKey(new PrimaryKey(partitionKey, authTokenValue, sortKey, date));
                    authTokenTable.deleteItem(deleteItemSpec);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan the authToken table: " + e.getMessage());
        }
 */

        ScanSpec scanSpec = new ScanSpec()
                .withProjectionExpression("#authTokenVal, " + sortKey + ", userAlias")
                .withFilterExpression("#authTokenVal < :cutOffVal")
                .withNameMap(new NameMap().with("#authTokenVal", partitionKey))
                .withValueMap(new ValueMap().withString(":cutOffVal", dtf.format(cutOff)));

        try {
            ItemCollection<ScanOutcome> items = authTokenTable.scan(scanSpec);

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                String authTokenValue = item.getString(partitionKey);
                String date = item.getString(sortKey);

                DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                        .withPrimaryKey(new PrimaryKey(partitionKey, authTokenValue, sortKey, date));
                authTokenTable.deleteItem(deleteItemSpec);
            }

        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan the table: " + e.getMessage());
        }
    }

    public static void removeAuthToken(String authTokenIdentifier) {

//        Item item = new Item()
//                .withPrimaryKey(partitionKey, authTokenIdentifier)
//                .withString("authToken", userAlias)
//                .withString(sortKey, authTokenIdentifier);
//
//        authTokenTable.deleteItem(item);
    }
}
