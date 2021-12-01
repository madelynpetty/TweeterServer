package edu.byu.cs.tweeter.model.net.response;

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

}
