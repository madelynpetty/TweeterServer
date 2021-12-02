package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.lambda.GetFollowerHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowingHandler;

public class Main {
    public static void main(String[] args) {

        GetFollowerHandler handler = new GetFollowerHandler();
        AuthToken authToken = new AuthToken();
        User lsatFollower = new User("m", "m", "@m", "");
        User follower = new User("maddie", "petty", "@mp", "");
        FollowerRequest request = new FollowerRequest(authToken, follower.getAlias(), 10, lsatFollower.getAlias());
        FollowerResponse response = handler.handleRequest(request, new FakeContext());
        System.out.println(response.getLastItem());


//        GetFollowingHandler handler = new GetFollowingHandler();
//        AuthToken authToken = new AuthToken();
//        FollowingRequest request = new FollowingRequest(authToken, "@mp", 10, "@m");
//        FollowingResponse response = handler.handleRequest(request, new FakeContext());
//        System.out.println(response.getLastItem());
    }
}
