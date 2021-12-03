package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class IsFollowerResponse extends AuthenticatedResponse {
    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public IsFollowerResponse(String message) {
        super(message);
    }

    public IsFollowerResponse(boolean success, boolean isFollower) {
        super(success, isFollower);
    }

    public IsFollowerResponse() {
        super();
    }
}
