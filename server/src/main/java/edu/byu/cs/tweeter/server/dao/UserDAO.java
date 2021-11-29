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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.util.HashMap;
import java.util.List;
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
    private static final String tableName = "user";
    private final String indexName = "alias-index";
    private static final Table userTable = dynamoDB.getTable(tableName);
    private static User loggedInUser;

    public LoginResponse login(LoginRequest request) {
//        Map<String, String> attrNames = new HashMap<String, String>();
//        attrNames.put("#a", ":alias");
////        attrNames.put("#password", ":passwordVal");
//
//        Map<String, AttributeValue> attrValues = new HashMap<>();
//        attrValues.put(":alias", new AttributeValue().withS(request.getAlias()));
////        attrValues.put(":passwordVal", new AttributeValue().withS(request.getPassword()));
//
//        QueryRequest queryRequest = new QueryRequest()
//                .withTableName(tableName)
//                .withIndexName(indexName)
//                .withKeyConditionExpression("#a = :alias")
//                .withExpressionAttributeNames(attrNames)
//                .withExpressionAttributeValues(attrValues);
//
//        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
//        List<Map<String, AttributeValue>> items = queryResult.getItems();
//
//        User user = null;
//        if (items != null) {
//            for (Map<String, AttributeValue> item : items) {
//                String password = item.get("password").getS();
//                if (password == request.getPassword()) {
//                    String firstName = item.get("firstName").getS();
//                    String lastName = item.get("lastName").getS();
//                    String imageUrl = item.get("imageUrl").getS();
//                    user =  new User(firstName, lastName, imageUrl);
//                    break;
//                }
//            }
//        }

        Item item = userTable.getItem("alias", request.getAlias());
        User user = null;

        if (item == null) {
            System.out.println("------------------");
            System.out.println("Username does not exist");
            System.out.println("------------------");
        }
        else {
            String password = item.getString("password");
            System.out.println("------------------");
            System.out.println("PASSWORD FROM DATABASE: " + password);
            System.out.println("------------------");
            if (password.equals(request.getPassword())) {
                String firstName = item.getString("firstName");
                String lastName = item.getString("lastName");
                String imageUrl = item.getString("image");
                System.out.println("IMAGEURL FROM DATABASE: " + imageUrl);
                user = new User(firstName, lastName, request.getAlias(), imageUrl);
            }
        }

        if (user == null) {
            System.out.println("------------------");
            System.out.println("Username and password combination do not match");
            System.out.println("------------------");
        }
        else {
            System.out.println("User exists.");
            System.out.println("Username: " + user.getAlias());
            System.out.println("firstName: " + user.getFirstName());
            System.out.println("lastName: " + user.getLastName());
            System.out.println("imageUrl: " + user.getImageUrl());
        }

        FakeData fakeData = new FakeData();
        AuthToken authToken = fakeData.getAuthToken();
        loggedInUser = user;

        return new LoginResponse(user, authToken);
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
        loggedInUser = user;
        FakeData fakeData = new FakeData();
        AuthToken authToken = fakeData.getAuthToken();
        return new RegisterResponse(user, authToken); //getAuthTokenDAO().getAuthToken()
    }

    public GetUserResponse getUser(GetUserRequest request) {
        //I believe this is already giving me the correct user even without using the request?
        return new GetUserResponse();
    }

    private AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAO();
    }

    public static User getUserFromAlias(String alias) {
        Item item = userTable.getItem("alias", alias);
        if (item != null) {
            String firstName = item.getString("firstName");
            String lastName = item.getString("lastName");
            String imageUrl = item.getString("image");
            return new User(firstName, lastName, alias, imageUrl);
        }
        return null;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
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
