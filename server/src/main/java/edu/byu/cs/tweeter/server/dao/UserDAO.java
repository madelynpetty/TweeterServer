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

public class UserDAO implements UserDAOInterface {
    private final String tableName = "user";
    private final Table userTable = DynamoDbFactory.getDynamoDB().getTable(tableName);

    public UserDAO() {}

    @Override
    public User login(String alias, String password) {
        Item item = userTable.getItem("alias", alias);
        User user = null;

        if (item == null) {
            throw new RuntimeException("Username does not exist");
        }
        else {
            String pass = item.getString("password");
            if (password.equals(pass)) {
                String firstName = item.getString("firstName");
                String lastName = item.getString("lastName");
                String imageUrl = item.getString("image");
                user = new User(firstName, lastName, alias, imageUrl);
            }
        }

        if (user == null) {
            throw new RuntimeException("Username and password combination do not match");
        }

        return user;
    }

    @Override
    public boolean logout(AuthToken authToken) {
        return true;
    }

    @Override
    public User register(String alias, String imageUrl, String firstName, String lastName, String password) {
        String image = null;
        
        try {
            Map<String, String> attrNames = new HashMap<String, String>();
            attrNames.put("#alias", ":val");

            Map<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":val", new AttributeValue().withS(alias));

            Item item = new Item()
                    .withPrimaryKey("alias", alias)
                    .withString("firstName", firstName)
                    .withString("lastName", lastName)
                    .withString("image", imageUrl)
                    .withString("password", password);

            userTable.putItem(item);
        }
        catch (DuplicateItemException e) {
            throw new RuntimeException("Duplicate Item Exception: " + e.getMessage());
        }

        User user = new User(firstName, lastName, alias);
        return user;
    }

    @Override
    public User getUser(String alias) {
        return getUserFromAlias(alias);
    }

    public static User getUserFromAlias(String alias) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#aliasName", "alias");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("user")
                .withKeyConditionExpression("#aliasName = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = DynamoDbFactory.getAmazonDynamoDB().query(queryRequest);
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
