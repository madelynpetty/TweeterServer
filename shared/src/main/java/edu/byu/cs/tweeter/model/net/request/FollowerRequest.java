package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followers for a specified follower.
 */
public class FollowerRequest {

    private AuthToken authToken;
    private User follower;
    private int limit;
    private User lastFollower;
    private User lastItem;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowerRequest() {}

    /**
     * Creates an instance.
     *
     * @param follower the user whose followers are to be returned.
     * @param limit the maximum number of follower to return.
     * @param lastFollower the last follower that was returned in the previous request (null if
     *                     there was no previous request or if no followers were returned in the
     *                     previous request).
     */
    public FollowerRequest(AuthToken authToken, User follower, int limit, User lastFollower) {
        this.authToken = authToken;
        this.follower = follower;
        this.limit = limit;
        this.lastFollower = lastFollower;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the follower whose followers are to be returned by this request.
     *
     * @return the follower.
     */
    public User getFollower() {
        return follower;
    }

    /**
     * Sets the follower.
     *
     * @param follower the follower.
     */
    public void setFollower(User follower) {
        this.follower = follower;
    }

    /**
     * Returns the number representing the maximum number of followers to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the last follower that was returned in the previous request or null if there was no
     * previous request or if no followers were returned in the previous request.
     *
     * @return the last follower.
     */
    public User getLastFollower() {
        return lastFollower;
    }

    /**
     * Sets the last follower.
     *
     * @param lastFollower the last follower.
     */
    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    public User getLastItem() {
        return lastItem;
    }

    public void setLastItem(User lastItem) {
        this.lastItem = lastItem;
    }
}
