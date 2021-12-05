package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.lambda.GetFollowerHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowingHandler;
import edu.byu.cs.tweeter.server.lambda.IsFollowerHandler;
import edu.byu.cs.tweeter.server.lambda.LoginHandler;
import edu.byu.cs.tweeter.server.lambda.UnfollowHandler;

public class Main {
    public static void main(String[] args) {

//        GetFollowerHandler handler = new GetFollowerHandler();
//        AuthToken authToken = new AuthToken();
//        User lsatFollower = new User("m", "m", "@m", "");
//        User follower = new User("maddie", "petty", "@mp", "");
//        FollowerRequest request = new FollowerRequest(authToken, follower.getAlias(), 10, lsatFollower.getAlias());
//        FollowerResponse response = handler.handleRequest(request, new FakeContext());
//        System.out.println(response.getLastItem());


//        GetFollowingHandler handler = new GetFollowingHandler();
//        AuthToken authToken = new AuthToken();
//        FollowingRequest request = new FollowingRequest(authToken, "@mp", 10, "@m");
//        FollowingResponse response = handler.handleRequest(request, new FakeContext());
//        System.out.println(response.getLastItem());


//        User currUser = new User("m", "p", "@mp", "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40mp");
//        User followee = new User("m", "m", "@m", "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40m");
//        AuthToken authToken = new AuthToken();
//        authToken.identifier = "70f43a00-8e56-4439-bf48-4bce52021eea";
//        IsFollowerRequest request = new IsFollowerRequest(authToken, currUser, followee);
//        IsFollowerHandler handler = new IsFollowerHandler();
//        IsFollowerResponse response = handler.handleRequest(request, new FakeContext());
//        System.out.println();


//        AuthToken authToken = new AuthToken();
//        authToken.identifier = "70f43a00-8e56-4439-bf48-4bce52021eea";
//        FollowerRequest followerRequest = new FollowerRequest(authToken, "@mp", 10, null);
//        GetFollowerHandler handler = new GetFollowerHandler();
//        FollowerResponse response = handler.handleRequest(followerRequest, new FakeContext());
//        System.out.println(response.isSuccess());


//        AuthToken authToken = new AuthToken();
//        authToken.identifier = "70f43a00-8e56-4439-bf48-4bce52021eea";
//        User currUser = new User("m", "p", "@mp", "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40mp");
//        User followee = new User("m", "m", "@m", "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40m");
//        UnfollowRequest request = new UnfollowRequest(followee, currUser, authToken);
//        UnfollowHandler handler = new UnfollowHandler();
//        UnfollowResponse response = handler.handleRequest(request, new FakeContext());
//        System.out.println(response.isSuccess());

        LoginRequest request = new LoginRequest("@mom", "mom");
        LoginHandler handler = new LoginHandler();
        LoginResponse response = handler.handleRequest(request, new FakeContext());
        System.out.println(response.isSuccess());
    }
}
