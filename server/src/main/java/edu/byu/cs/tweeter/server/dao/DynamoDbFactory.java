package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbFactory extends DAOFactory {
    private static AmazonDynamoDB amazonDynamoDB;
    private static DynamoDB dynamoDB;

    private AuthTokenDAO authTokenDAO;
    private FeedDAO feedDAO;
    private FollowDAO followDAO;
    private S3DAO s3DAO;
    private StoryDAO storyDAO;
    private UserDAO userDAO;

    public DynamoDbFactory() {
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        dynamoDB = new DynamoDB(amazonDynamoDB);
    }

    public static DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public static AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        if (authTokenDAO == null) {
            authTokenDAO = new AuthTokenDAO();
        }
        return authTokenDAO;
    }

    @Override
    public FeedDAO getFeedDAO() {
        if (feedDAO == null) {
            feedDAO = new FeedDAO();
        }
        return feedDAO;
    }

    @Override
    public FollowDAO getFollowDAO() {
        if (followDAO == null) {
            followDAO = new FollowDAO();
        }
        return followDAO;
    }

    @Override
    public S3DAO getS3DAO() {
        return new S3DAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        if (storyDAO == null) {
            storyDAO = new StoryDAO();
        }
        return storyDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAO();
    }
}
