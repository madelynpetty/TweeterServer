package edu.byu.cs.tweeter.server.dao;

public abstract class DAOFactory {
    private static DAOFactory daoFactory;

    public static DAOFactory getInstance() {
        if (daoFactory == null) {
            new DbConfig();
        }
        return daoFactory;
    }

    public static void setInstance(DAOFactory factory) {
        daoFactory = factory;
    }

    public abstract AuthTokenDAO getAuthTokenDAO();

    public abstract FeedDAOInterface getFeedDAO();

    public abstract FollowDAOInterface getFollowDAO();

    public abstract S3DAO getS3DAO();

    public abstract StoryDAOInterface getStoryDAO();

    public abstract UserDAOInterface getUserDAO();
}
