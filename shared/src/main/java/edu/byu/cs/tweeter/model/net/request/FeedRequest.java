package edu.byu.cs.tweeter.model.net.request;

import javax.print.MultiDocPrintService;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followers for a specified follower.
 */
public class FeedRequest {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private String lastStatus;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FeedRequest() {}

    /**
     * Creates an instance.
     *
     * @param userAlias the alias of the user whose feed is to be returned.
     * @param limit the maximum number of statuses to return.
     * @param lastStatus the alias of the last status that was returned in the previous request (null if
     *                     there was no previous request or if no statuses were returned in the
     *                     previous request).
     */
    public FeedRequest(AuthToken authToken, String userAlias, int limit, String lastStatus) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastStatus = lastStatus;
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
     * Returns the user whose feed is to be returned by this request.
     *
     * @return the follower.
     */
    public String getUserAlias() {
        return userAlias;
    }

    /**
     * Sets the user.
     *
     * @param userAlias the follower.
     */
    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    /**
     * Returns the number representing the maximum number of statuses to be returned by this request.
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
     * Returns the last status that was returned in the previous request or null if there was no
     * previous request or if no statuses were returned in the previous request.
     *
     * @return the last status.
     */
    public String getLastStatus() {
        return lastStatus;
    }

    /**
     * Sets the last status.
     *
     * @param lastStatus the last status.
     */
    public void setLastFollowerAlias(String lastStatus) {
        this.lastStatus = lastStatus;
    }
}
