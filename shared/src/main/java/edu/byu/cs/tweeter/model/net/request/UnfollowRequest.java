package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followees for a specified follower.
 */
public class UnfollowRequest {

    private User user;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private UnfollowRequest() {}

    /**
     * Creates an instance.
     *
     * @param user the user
     */
    public UnfollowRequest(User user) {
        this.user = user;
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
}
