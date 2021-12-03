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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class UserDAO {
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard().withRegion("us-west-2").build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final String tableName = "user";
    private final String indexName = "alias-index";
    private static final Table userTable = dynamoDB.getTable(tableName);

    public static AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }

    public static DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public LoginResponse login(LoginRequest request) {
        Item item = userTable.getItem("alias", request.getAlias());
        User user = null;

        if (item == null) {
            throw new RuntimeException("Username does not exist");
        }
        else {
            String password = item.getString("password");
            if (password.equals(request.getPassword())) {
                String firstName = item.getString("firstName");
                String lastName = item.getString("lastName");
                String imageUrl = item.getString("image");
                user = new User(firstName, lastName, request.getAlias(), imageUrl);
            }
        }

        if (user == null) {
            throw new RuntimeException("Username and password combination do not match");
        }


//        getAuthTokenDAO().checkValidAuthTokens();
        return new LoginResponse(user, getAuthTokenDAO().getNewAuthToken(user.getAlias()));
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() != null && request.getAuthToken().getIdentifier() != null) {
            AuthTokenDAO.removeAuthToken(request.getAuthToken().getIdentifier());
        }
//        getAuthTokenDAO().checkValidAuthTokens();
        return new LogoutResponse(true);
    }

    public RegisterResponse register(RegisterRequest request) {
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
        }
        catch (DuplicateItemException e) {
            throw new RuntimeException("Duplicate Item Exception: " + e.getMessage());
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getAlias(), imageUrl);
        return new RegisterResponse(user, getAuthTokenDAO().getNewAuthToken(user.getAlias()));
    }

    public GetUserResponse getUser(GetUserRequest request) {
        return new GetUserResponse(true, UserDAO.getUserFromAlias(request.getAlias()));
    }

    private AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAO();
    }

    public static User getUserFromAlias(String alias) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#aliasName", "alias");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withKeyConditionExpression("#aliasName = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String firstName = item.get("firstName").getS();
                String lastName = item.get("lastName").getS();
                String imageUrl = item.get("image").getS();
                return new User(firstName, lastName, alias, imageUrl);
            }
        }
        return null;
    }
}
