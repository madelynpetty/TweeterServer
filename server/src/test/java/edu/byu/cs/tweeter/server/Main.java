package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.server.lambda.GetFollowerHandler;

public class Main {
    public static void main(String[] args) {

        GetFollowerHandler handler = new GetFollowerHandler();
        AuthToken authToken = new AuthToken();
        User follower = new User("maddie", "petty", "@mp", "");
        FollowerRequest request = new FollowerRequest(authToken, follower, 10, null);
        handler.handleRequest(request, new FakeContext());
    }
}
