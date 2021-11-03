package edu.byu.cs.tweeter.model.net.request;

/**
 * Contains all the information needed to make a login request.
 */
public class LoginRequest extends AuthenticateRequest {

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LoginRequest() {
        super(null, null);
    }

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public LoginRequest(String username, String password) {
        super(username, password);
    }
}
