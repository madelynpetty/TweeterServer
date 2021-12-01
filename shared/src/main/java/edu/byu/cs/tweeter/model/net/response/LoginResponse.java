package edu.byu.cs.tweeter.model.net.response;


import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class LoginResponse extends AuthenticateResponse {

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the now logged in user.
     * @param authToken the auth token representing this user's session with the server.
     */
    public LoginResponse(User user, AuthToken authToken) {
        super(user, authToken);
        System.out.println("---------------");
        System.out.println("HIT LOGIN RESPONSE");
        if (user != null) {
            System.out.println("USERNAME " + user.getAlias());
        }
        else {
            System.out.println("USER IS NULL");
        }
        System.out.println("---------------");
    }

    public LoginResponse() {
        super();
    }
}
