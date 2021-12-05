package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.service.UserService;

public class UserDAO implements UserDAOInterface {
    private final String tableName = "user";
    private final Table userTable = DynamoDbFactory.getDynamoDB().getTable(tableName);
    private final byte[] SALT_ARRAY = {
        (byte) 0x52, (byte) 0x43, (byte) 0x68, (byte) 0x58,
        (byte) 0x6D, (byte) 0x75, (byte) 0x5A, (byte) 0x79,
        (byte) 0x52, (byte) 0x43, (byte) 0x68, (byte) 0x58,
        (byte) 0x6D, (byte) 0x75, (byte) 0x5A, (byte) 0x79
    };

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
//            String storedHashedPassword = item.getString("password");
//            String salt = item.getString("salt");
//
//            MessageDigest md = null;
//            String hashedPassword = null;
//            try {
//                md = MessageDigest.getInstance("SHA-512");
//                md.update(SALT_ARRAY);
//                byte[] hashedPasswordBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
//                hashedPassword = new String(hashedPasswordBytes);
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException("Exception while decrypting: " + e.getMessage());
//            }
//
//            System.out.println("Hashed passowrd: " + hashedPassword);
//            System.out.println("Salt: " + salt);

            if (storedPassword.equals(password)) {
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

//            SecureRandom random = new SecureRandom();
//            byte[] salt = new byte[16];
//            random.nextBytes(salt);
//
//            MessageDigest md = MessageDigest.getInstance("SHA-512");
//            md.update(SALT_ARRAY);
//            String saltStr = new String(SALT_ARRAY, StandardCharsets.UTF_8);
//
//            byte[] hashedPasswordBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
//            String hashedPassword = new String(hashedPasswordBytes);
//
//            System.out.println("Hashed passowrd: " + hashedPassword);
//            System.out.println("Salt: " + saltStr);

            url = UserService.getImageUrl(alias, imageUrl);

            Item item = new Item()
                    .withPrimaryKey("alias", alias)
                    .withString("firstName", firstName)
                    .withString("lastName", lastName)
                    .withString("image", url)
                    .withString("password", password);

            userTable.putItem(item);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception thrown: " + e.getMessage());
        }

        return new User(firstName, lastName, alias, url);
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
