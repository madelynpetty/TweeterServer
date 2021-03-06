package edu.byu.cs.tweeter.model.net.response;

public class UnfollowResponse extends AuthenticatedResponse {

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public UnfollowResponse(String message) {
        super(message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     */
    public UnfollowResponse(boolean success) {
        super(success);
    }

    public UnfollowResponse() {
        super();
    }
}
