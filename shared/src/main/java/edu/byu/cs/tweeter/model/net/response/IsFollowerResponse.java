package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class IsFollowerResponse extends AuthenticatedResponse {

    private boolean isFollower;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public IsFollowerResponse(String message) {
        super(message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super();
        this.isFollower = isFollower;
    }

    public IsFollowerResponse() {
        super();
    }

    /**
     * Returns whether the user is a follower or not.
     *
     * @return whether the user is a follower or not.
     */
    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean isFollower) {
        this.isFollower = isFollower;
    }
}
