package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingCountRequest extends CountRequest {
    private FollowingCountRequest() {
        super();
    }

    public FollowingCountRequest(AuthToken authToken, User targetUser) {
        super(authToken, targetUser);
    }
}
