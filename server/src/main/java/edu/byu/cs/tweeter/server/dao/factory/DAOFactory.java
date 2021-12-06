package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.S3DAO;
import edu.byu.cs.tweeter.server.dao.DAOInterface.StoryDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.UserDAOInterface;

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
