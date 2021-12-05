package edu.byu.cs.tweeter.server.dao;

import static com.amazonaws.util.StringUtils.UTF8;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DuplicateItemException;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

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
            String storedHashedPassword = item.getString("password");
            String hashTypedInPassword;
            try {
                hashTypedInPassword = hashPassword(password);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not hash password: " + e.getMessage());
            }

//            try {
//                decryptedPassword = decrypt(password);
//            }
//            catch(Exception e) {
//                throw new RuntimeException("Exception was thrown while decrypting: " + e.getMessage());
//            }

            if (storedHashedPassword.equals(hashTypedInPassword)) {
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
        try {
            Map<String, String> attrNames = new HashMap<String, String>();
            attrNames.put("#alias", ":val");

            Map<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":val", new AttributeValue().withS(alias));

            String hashedPassword = hashPassword(password);

//            String encryptedPassword = encrypt(password);

            Item item = new Item()
                    .withPrimaryKey("alias", alias)
                    .withString("firstName", firstName)
                    .withString("lastName", lastName)
                    .withString("image", imageUrl)
                    .withString("password", hashedPassword);

            userTable.putItem(item);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception thrown: " + e.getMessage());
        }

        User user = new User(firstName, lastName, alias);
        return user;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        byte[] salt = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03,
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
        };
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        byte[] hashedPasswordBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return new String(hashedPasswordBytes, StandardCharsets.UTF_8);
    }

    /*private String encrypt(String unencryptedString) throws Exception {
        String myEncryptionScheme = "DESede";
        SecretKeyFactory skf = SecretKeyFactory.getInstance(myEncryptionScheme);
//        String myEncryptionKey = "MaddiePettyTweeterProjec";
//        byte[] arrayBytes = myEncryptionKey.getBytes(UTF8);
        byte[] arrayBytes = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
        };
        KeySpec ks = new DESedeKeySpec(arrayBytes);

        String encryptedString = null;
        try {
            Cipher cipher = Cipher.getInstance(myEncryptionScheme);
            cipher.init(Cipher.ENCRYPT_MODE, skf.generateSecret(ks));
//            byte[] plainText = unencryptedString.getBytes(UTF8);
//            byte[] encryptedText = cipher.doFinal(plainText);

            byte[] base64decodedTokenArr = Base64.decodeBase64(unencryptedString.getBytes(UTF8));
            byte[] encryptedText = cipher.doFinal(base64decodedTokenArr);

            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            throw new RuntimeException("Password could not be encrypted: " + e.getMessage());
        }
        return encryptedString;
    }

    private String decrypt(String encryptedString) throws Exception {
        String myEncryptionScheme = "DESede";
        SecretKeyFactory skf = SecretKeyFactory.getInstance(myEncryptionScheme);
//        String myEncryptionKey = "MaddiePettyTweeterProjec";
//        byte[] arrayBytes = myEncryptionKey.getBytes(UTF8);
        byte[] arrayBytes = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
        };
        KeySpec ks = new DESedeKeySpec(arrayBytes);

        String decryptedText = null;
        try {
            Cipher cipher = Cipher.getInstance(myEncryptionScheme);
            cipher.init(Cipher.DECRYPT_MODE, skf.generateSecret(ks));
//            byte[] encryptedText = Base64.decodeBase64(encryptedString);

            byte[] base64decodedTokenArr = Base64.decodeBase64(encryptedString.getBytes(UTF8));
            byte[] encryptedText = cipher.doFinal(base64decodedTokenArr);

            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            throw new RuntimeException("Could not decrypt password: " + e.getMessage());
        }
        return decryptedText;
    }*/

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
