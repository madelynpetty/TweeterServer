package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a login request.
 */
public class LogoutRequest {

    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LogoutRequest() {}

    /**
     * Creates an instance.
     */
    public LogoutRequest(AuthToken authToken) {}

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
