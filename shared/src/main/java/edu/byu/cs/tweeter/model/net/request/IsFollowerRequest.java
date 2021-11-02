package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a login request.
 */
public class IsFollowerRequest {

    private AuthToken authToken;
    private User follower;
    private User followee;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private IsFollowerRequest() {}

    /**
     * Creates an instance.
     *
     * @param follower the alleged follower.
     * @param followee the alleged followee.
     */
    public IsFollowerRequest(AuthToken authToken, User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }

    /**
     * Returns the authToken of the user.
     *
     * @return the authToken.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authToken.
     *
     * @param authToken the authToken.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the alleged follower in this request.
     *
     * @return the follower.
     */
    public User getFollower() {
        return follower;
    }

    /**
     * Sets the alleged follower in this request.
     *
     * @param follower the follower.
     */
    public void setFollower(User follower) {
        this.follower = follower;
    }

    /**
     * Returns the alleged followee in this request.
     *
     * @return the followee.
     */
    public User getFollowee() {
        return followee;
    }

    /**
     * Sets the alleged followee in this request.
     *
     * @param followee the followee.
     */
    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
