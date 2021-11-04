package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerCountRequest extends CountRequest {
    private FollowerCountRequest() {
        super(null, null);
    }

    public FollowerCountRequest(AuthToken authToken, User targetUser) {
        super(authToken, targetUser);
    }
}
