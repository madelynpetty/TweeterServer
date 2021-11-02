package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Contains all the information needed to make a login request.
 */
public class PostStatusRequest {

    private AuthToken authToken;
    private Status post;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PostStatusRequest() {}

    /**
     * Creates an instance.
     *
     * @param post the post.
     */
    public PostStatusRequest(Status post) {
        this.post = post;
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
}
