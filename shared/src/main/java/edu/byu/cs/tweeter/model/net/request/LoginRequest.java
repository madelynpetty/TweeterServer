package edu.byu.cs.tweeter.model.net.request;

/**
 * Contains all the information needed to make a login request.
 */
public class LoginRequest extends AuthenticateRequest {

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LoginRequest() {
        super();
    }

    /**
     * Creates an instance.
     *
     * @param alias the alias of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public LoginRequest(String alias, String password) {
        super(alias, password);
    }

//    public String getAlias() {
//        return alias;
//    }
//
//    public void setAlias(String alias) {
//         this.alias = alias;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
