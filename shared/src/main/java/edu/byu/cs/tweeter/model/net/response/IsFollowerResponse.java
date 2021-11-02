package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class IsFollowerResponse extends Response {

    private boolean isFollower;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public IsFollowerResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param isFollower whether the user is a follower or not.
     */
    public IsFollowerResponse(Boolean isFollower) {
        super(true, null);
        this.isFollower = isFollower;
    }

    /**
     * Returns whether the user is a follower or not.
     *
     * @return whether the user is a follower or not.
     */
    public boolean getIsFollower() {
        return isFollower;
    }
}
