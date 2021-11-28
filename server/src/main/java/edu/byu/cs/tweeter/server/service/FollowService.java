package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

public class FollowService {

    public FollowingResponse getFollowees(FollowingRequest request) {
        return getFollowingDAO().getFollowees(request);
    }

    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        return getFollowerDAO().getFollowers(request);
    }

    FollowDAO getFollowerDAO() {
        return new FollowDAO();
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        return getFollowerDAO().getFollowerCount(request.getTargetUser());
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        return getFollowerDAO().getFollowingCount(request.getTargetUser());
    }

    public FollowResponse follow(FollowRequest request) {
        return getFollowerDAO().follow(request.getUser());
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return getFollowerDAO().unfollow(request.getUser());
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        return getFollowerDAO().isFollower(request.getFollower()); //TODO may need getFollowee here
    }
}
