package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Contains all the information needed to make a login request.
 */
public class PostStatusRequest {

    private AuthToken authToken;
    private Status post;
    private String currUserAlias;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PostStatusRequest() {}

    /**
     * Creates an instance.
     *
     * @param post the post.
     */
    public PostStatusRequest(Status post, String currUserAlias, AuthToken authToken) {
        this.post = post;
        this.currUserAlias = currUserAlias;
        this.authToken = authToken;
    }

    /**
     * Returns the status to be posted by this request.
     *
     * @return the post.
     */
    public Status getPost() {
        return post;
    }

    /**
     * Sets the post.
     *
     * @param post the post.
     */
    public void setPost(Status post) {
        this.post = post;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getCurrUserAlias() {
        return currUserAlias;
    }

    public void setCurrUserAlias(String currUserAlias) {
        this.currUserAlias = currUserAlias;
    }
}
