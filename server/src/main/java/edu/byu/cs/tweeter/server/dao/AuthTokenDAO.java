package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAO implements AuthTokenDAOInterface {
    private static final String tableName = "authTokenTable";
    private final String indexName = "authTokenTimeStamp-authToken-index";
    private static Table authTokenTable = DynamoDbFactory.getDynamoDB().getTable(tableName);
    private static final String partitionKey = "authToken";
    private static final String sortKey = "authTokenTimeStamp";

    public AuthTokenDAO() {}

    @Override
    public AuthToken getNewAuthToken(String userAlias) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        AuthToken authToken = new AuthToken();
        Date date = new Date();
        long ttl = date.getTime() + 3600000; // 1 hour

        Item item = new Item()
                .withPrimaryKey(partitionKey, authToken.getIdentifier())
                .withString("userAlias", userAlias)
                .withString(sortKey, dtf.format(now))
                .withNumber("ttl", ttl);

        authTokenTable.putItem(item);
        return authToken;
    }

    @Override
    public void checkValidAuthTokens() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime cutOff = LocalDateTime.now().minusMinutes(30);
        String appCreationDate = "2021/11/1 10:10:10";
            // although this is 30 minutes ago, this actually is a day and a
            // half ago due to aws not allowing me to change my timezone.

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(sortKey + " BETWEEN :creationTime AND :authTimeStamp")
                .withValueMap(new ValueMap()
                        .withString(":authTimeStamp", dtf.format(cutOff))
                        .withString(":creationTime", appCreationDate));

        try {
            Index index = authTokenTable.getIndex(indexName);
            ItemCollection<QueryOutcome> items = index.query(querySpec);

            for (Item item : items) {
                String authToken = item.getString(partitionKey);
                String timestamp = item.getString(sortKey);

                DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
                        new PrimaryKey(partitionKey, authToken, sortKey, timestamp));
                authTokenTable.deleteItem(deleteItemSpec);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to scan authToken table: " + e.getMessage());
        }
    }

    @Override
    public void removeAuthToken(String authTokenIdentifier) {

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(partitionKey + " = :authToken")
                .withValueMap(new ValueMap().withString(":authToken", authTokenIdentifier));

        ItemCollection<QueryOutcome> items = authTokenTable.query(querySpec);

        if (items != null) {
            for (Item item : items) {
                String authToken = item.getString(partitionKey);
                String timestamp = item.getString(sortKey);

                DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
                        new PrimaryKey(partitionKey, authToken, sortKey, timestamp));
                authTokenTable.deleteItem(deleteItemSpec);
            }
        }
    }

    @Override
    public boolean validateUser(String authTokenIdentifier, String userAlias) {
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(partitionKey + " = :authToken")
                .withValueMap(new ValueMap().withString(":authToken", authTokenIdentifier));

        ItemCollection<QueryOutcome> items = authTokenTable.query(querySpec);

        if (items != null) {
            for (Item item : items) {
                String aliasFromDB = item.getString("userAlias");
//                String timestamp = item.getString(sortKey);

                if (userAlias.equals(aliasFromDB)) {
                    return true;
                }
            }
        }

        return false;
    }
}
