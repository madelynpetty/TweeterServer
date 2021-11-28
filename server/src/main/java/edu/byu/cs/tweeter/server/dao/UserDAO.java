package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DuplicateItemException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

public class UserDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard().withRegion("us-west-2").build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private final String tableName = "user";
    private final String indexName = "alias-index";
    private Table userTable = dynamoDB.getTable(tableName);

    public LoginResponse login(LoginRequest request) {
        FakeData fakeData = new FakeData();
        User user = fakeData.getFirstUser();
        return new LoginResponse(user, getAuthTokenDAO().getAuthToken());
    }

    public LogoutResponse logout(LogoutRequest request) {
        //validate request.authtoken
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        //todo can re-register user with same already registered alias

        String imageUrl = null;
        
        try {
            Map<String, String> attrNames = new HashMap<String, String>();
            attrNames.put("#alias", ":val");

            Map<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":val", new AttributeValue().withS(request.getAlias()));

            S3DAO.putUrl(request.getAlias(), request.getImageUrl());
            imageUrl = S3DAO.getUrl(request.getAlias());

            Item item = new Item()
                    .withPrimaryKey("alias", request.getAlias())
                    .withString("firstName", request.getFirstName())
                    .withString("lastName", request.getLastName())
                    .withString("image", imageUrl)
                    .withString("password", request.getPassword());

            userTable.putItem(item);

//            userTable.putItem("alias", request.getAlias(), LocationAttr, location,
//                    "set #alias = #alias + :val", attrNames, attrValues);
        }
        catch (DuplicateItemException e) {
            System.out.println("Duplicate Item Exception: " + e.getMessage());
        }

//        Map<String, String> attrNames = new HashMap<String, String>();
//        attrNames.put("#alias", ":val");
//        attrNames.put("#firstName", "firstName");
//        attrNames.put("#lastName", "lastName");
//        attrNames.put("#imageUrl", "imageUrl");
//        attrNames.put("#password", "password");

//        Map<String, AttributeValue> attrValues = new HashMap<>();
//        attrValues.put(":val", new AttributeValue().withS(request.getAlias()));
//        attrValues.put(":firstName", new AttributeValue().withS(request.getFirstName()));
//        attrValues.put(":lastName", new AttributeValue().withS(request.getLastName()));
//        attrValues.put(":imageUrl", new AttributeValue().withS(request.getImage()));
//        attrValues.put(":password", new AttributeValue().withS(request.getPassword()));
//        QueryRequest queryRequest = new QueryRequest()
//                .withTableName(tableName)
//                .withIndexName(indexName)
//                .withKeyConditionExpression("#a = :alias")
//                .withExpressionAttributeNames(attrNames)
//                .withExpressionAttributeValues(attrValues);
//
//        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
//        List<Map<String, AttributeValue>> items = queryResult.getItems();

        User user = new User(request.getFirstName(), request.getLastName(), request.getAlias(), imageUrl);
        FakeData fakeData = new FakeData();
        AuthToken authToken = fakeData.getAuthToken();
        return new RegisterResponse(user, authToken); //getAuthTokenDAO().getAuthToken()
    }

    public GetUserResponse getUser(GetUserRequest request) {
        //TODO actually get the correct user

        return new GetUserResponse();
    }

    private AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAO();
    }

//    public void createTable() throws Exception {
//        try {
//            // Attribute definitions
//            ArrayList<AttributeDefinition> tableAttributeDefinitions = new ArrayList<>();
//
//            tableAttributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName("alias")
//                    .withAttributeType("S"));
//            tableAttributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName("firstName")
//                    .withAttributeType("S"));
//            tableAttributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName("lastName")
//                    .withAttributeType("S"));
//            tableAttributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName("imageUrl")
//                    .withAttributeType("S"));
//            tableAttributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName("password")
//                    .withAttributeType("S"));
//
//            // Table key schema
//            ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
//            tableKeySchema.add(new KeySchemaElement()
//                    .withAttributeName("alias")
//                    .withKeyType(KeyType.HASH));  //Partition key
//
//            // Index
//            GlobalSecondaryIndex index = new GlobalSecondaryIndex()
//                    .withIndexName("user-index")
//                    .withProvisionedThroughput(new ProvisionedThroughput()
//                            .withReadCapacityUnits((long) 1)
//                            .withWriteCapacityUnits((long) 1))
//                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));
//
//            ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();
//
//            indexKeySchema.add(new KeySchemaElement()
//                    .withAttributeName("alias")
//                    .withKeyType(KeyType.HASH));  //Partition key
//
//            index.setKeySchema(indexKeySchema);
//
//            CreateTableRequest createTableRequest = new CreateTableRequest()
//                    .withTableName("user")
//                    .withProvisionedThroughput(new ProvisionedThroughput()
//                            .withReadCapacityUnits((long) 1)
//                            .withWriteCapacityUnits((long) 1))
//                    .withAttributeDefinitions(tableAttributeDefinitions)
//                    .withKeySchema(tableKeySchema)
//                    .withGlobalSecondaryIndexes(index);
//
//            Table table = dynamoDB.createTable(createTableRequest);
//            table.waitForActive();
//        }
//        catch (Exception e) {
//            throw new Exception(e);
//        }
//    }
}
