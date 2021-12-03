package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticatedResponse extends Response {
    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public AuthenticatedResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     */
    public AuthenticatedResponse() {
        super();
    }

    public AuthenticatedResponse(Boolean success) {
        super(success, null);
    }



    private User user;
    public AuthenticatedResponse(boolean success, User user) {
        super(success);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    private boolean isFollower;
    public AuthenticatedResponse(boolean success, boolean isFollower) {
        super(success);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean isFollower) {
        this.isFollower = isFollower;
    }
}
