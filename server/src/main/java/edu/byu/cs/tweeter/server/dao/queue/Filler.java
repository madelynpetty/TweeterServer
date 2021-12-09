package edu.byu.cs.tweeter.server.dao.queue;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;


public class Filler {
    private final static int NUM_USERS = 50;
    private final static String FOLLOW_TARGET = "followed";

    public static void fillDatabase() {
        UserDAOInterface userDAO = DAOFactory.getInstance().getUserDAO();
        FollowDAOInterface followDAO = DAOFactory.getInstance().getFollowDAO();

        List<User> followers = new ArrayList<>();
//        List<User> users = new ArrayList<>();

        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "Guy " + i;
            String alias = "guy" + i;
            String lastName = i + "";

            User user = new User(firstName, lastName, alias, "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40dad");
//            users.add(user);
            followers.add(user);
        }

//        if (users.size() > 0) {
//            userDAO.addUserBatch(users);
//        }
        if (followers.size() > 0) {
            followDAO.addFollowerBatch(followers);
        }
    }

    public static void main(String[] args) {
        fillDatabase();
    }
}