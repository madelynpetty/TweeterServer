package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.service.UserService;

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
            String storedPassword = item.getString("password");
            String hashedPassword = hashPassword(password);

            if (storedPassword.equals(hashedPassword)) {
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
    public User register(String alias, String firstName, String lastName, String password,
                         String imageUrl) {
        String url = null;
        try {
            Map<String, String> attrNames = new HashMap<String, String>();
            attrNames.put("#alias", ":val");

            Map<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":val", new AttributeValue().withS(alias));

            String hashedPassword = hashPassword(password);

            url = UserService.getImageUrl(alias, imageUrl);

            Item item = new Item()
                    .withPrimaryKey("alias", alias)
                    .withString("firstName", firstName)
                    .withString("lastName", lastName)
                    .withString("image", url)
                    .withString("password", hashedPassword);

            userTable.putItem(item);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception thrown: " + e.getMessage());
        }

        return new User(firstName, lastName, alias, url);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to hash password: " + e.getMessage());
        }
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
