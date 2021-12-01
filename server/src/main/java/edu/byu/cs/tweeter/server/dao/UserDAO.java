package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DuplicateItemException;

import java.util.HashMap;
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


        getAuthTokenDAO().checkValidAuthTokens();
        return new LoginResponse(user, getAuthTokenDAO().getNewAuthToken(user.getAlias()));
    }

    public LogoutResponse logout(LogoutRequest request) {
        //todo validate request.authtoken

        getAuthTokenDAO().checkValidAuthTokens();
        return new LogoutResponse(true);
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
        }
        catch (DuplicateItemException e) {
            throw new RuntimeException("Duplicate Item Exception: " + e.getMessage());
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getAlias(), imageUrl);
        return new RegisterResponse(user, getAuthTokenDAO().getNewAuthToken(user.getAlias()));
    }

    public GetUserResponse getUser(GetUserRequest request) {
        return new GetUserResponse(true);
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
}
