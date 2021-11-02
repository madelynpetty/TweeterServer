package edu.byu.cs.tweeter.model.net.response;

public class GetUserResponse extends Response {
    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public GetUserResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     */
    public GetUserResponse() {
        super(true, null);
    }
}
