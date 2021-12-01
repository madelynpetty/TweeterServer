package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class CountRequest {
    private AuthToken authToken;
    private User targetUser;

    public CountRequest(AuthToken authToken, User targetUser) {
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public CountRequest() {
        super();
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getTargetUser() {
        return targetUser;
    }
}
