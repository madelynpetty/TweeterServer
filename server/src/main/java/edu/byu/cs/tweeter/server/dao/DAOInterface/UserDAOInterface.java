package edu.byu.cs.tweeter.server.dao.DAOInterface;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAOInterface {
    User login(String alias, String password);
    boolean logout(AuthToken authToken);
    User register(String alias, String firstName, String lastName, String password,
                  String imageUrl);
    User getUser(String alias);
    void addUserBatch(List<User> users);
}
