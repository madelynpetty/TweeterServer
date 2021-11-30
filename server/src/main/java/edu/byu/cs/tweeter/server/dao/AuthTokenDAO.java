package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard().withRegion("us-west-2").build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String tableName = "authTokenTable";
    private final String indexName = "authToken-authTokenTimeStamp-index";
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

    public void checkValidAuthTokens() { //todo call this when app starts up?
        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#authtokenName", partitionKey);
        attrNames.put("#timeName", sortKey);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(); //todo not sure how to get from last week or whatever

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":" + partitionKey, new AttributeValue().withS(null /* todo not sure what to put here */));
        attrValues.put(":" + sortKey, new AttributeValue().withS(dtf.format(now)));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName)
                .withKeyConditionExpression("#aliasName = :" + partitionKey +
                        " AND #timeName < :" + sortKey)
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        for (Map<String, AttributeValue> item : items) {
            Set<String> partitionSet = item.keySet();
            for (String s : partitionSet) {
                String partition = s;
                AttributeValue sort = item.get(partition);
                authTokenTable.deleteItem(partition, sort);
            }
        }
    }
}
