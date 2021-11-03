package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link edu.byu.cs.tweeter.model.net.request.RegisterRequest}.
 */
public class RegisterResponse extends AuthenticateResponse {

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user      the now logged in and registered user.
     * @param authToken the auth token representing this user's session with the server.
     */
    public RegisterResponse(User user, AuthToken authToken) {
        super(user, authToken);
    }
}
