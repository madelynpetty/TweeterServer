package edu.byu.cs.tweeter.model.net.request;

import javax.swing.text.html.ImageView;

/**
 * Contains all the information needed to make a login request.
 */
public class PostStatusRequest {

    private String post;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PostStatusRequest() {}

    /**
     * Creates an instance.
     *
     * @param post the post.
     */
    public PostStatusRequest(String post) {
        this.post = post;
    }

    /**
     * Returns the post to be posted by this request.
     *
     * @return the post.
     */
    public String getPost() {
        return post;
    }

    /**
     * Sets the post.
     *
     * @param post the post.
     */
    public void setPost(String post) {
        this.post = post;
    }
}
