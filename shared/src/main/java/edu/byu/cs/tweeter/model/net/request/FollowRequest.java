package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followees for a specified follower.
 */
public class FollowRequest {

    private User user; //user to be followed
    private User currUser;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowRequest() {}

    /**
     * Creates an instance.
     *
     * @param user the user
     */
    public FollowRequest(User user, User currUser, AuthToken authToken) {
        this.user = user;
        this.currUser = currUser;
        this.authToken = authToken;
    }

    /**
     * Returns the user to be followed.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user.
     *
     * @return the current user.
     */
    public User getCurrUser() {
        return currUser;
    }

    /**
     * Sets the current user.
     *
     * @param currUser the current user.
     */
    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
