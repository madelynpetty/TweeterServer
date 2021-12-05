package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;

public class FollowService {

    FollowDAOInterface followDAOInterface = DAOFactory.getInstance().getFollowDAO();
    AuthTokenDAOInterface authTokenDAOInterface = DAOFactory.getInstance().getAuthTokenDAO();

    public FollowingResponse getFollowees(FollowingRequest request) {
        assert request.getLoggedInUserAlias() != null;
        assert request.getLastFolloweeAlias() != null;
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FollowingResponse("AuthToken is no longer valid.");
        }

        List<User> allFollowees = followDAOInterface.getFollowees(request.getLoggedInUserAlias(),
                request.getLastFolloweeAlias(), request.getLimit());
        boolean hasMorePages = request.getLimit() < allFollowees.size();
        User lastFollowee = allFollowees.get(allFollowees.size() - 1);
        request.setLastFolloweeAlias(lastFollowee.getAlias());

        return new FollowingResponse(allFollowees, lastFollowee, hasMorePages);
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        assert request.getLimit() > 0;
        assert request.getLoggedInUserAlias() != null;
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FollowerResponse("AuthToken is no longer valid.");
        }

        List<User> allFollowers = followDAOInterface.getFollowers(request.getLoggedInUserAlias(),
                request.getLastFollowerAlias(), request.getLimit());
        boolean hasMorePages = request.getLimit() <= allFollowers.size();
        User lastFollower = allFollowers.get(allFollowers.size() - 1);
        if (lastFollower != null) {
            request.setLastFollowerAlias(lastFollower.getAlias());
        }
        else {
            request.setLastFollowerAlias(null);
        }

        return new FollowerResponse(allFollowers, lastFollower, hasMorePages);
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        assert request.getAuthToken() != null;
        assert request.getTargetUser() != null;
        assert request.getTargetUser().getAlias() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FollowerCountResponse("AuthToken is no longer valid.");
        }

        int count = followDAOInterface.getFollowerCount(request.getTargetUser());
        return new FollowerCountResponse(count);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        assert request.getAuthToken() != null;
        assert request.getTargetUser() != null;
        assert request.getTargetUser().getAlias() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FollowingCountResponse("AuthToken is no longer valid.");
        }

        int count = followDAOInterface.getFollowingCount(request.getTargetUser());
        return new FollowingCountResponse(count);
    }

    public FollowResponse follow(FollowRequest request) {
        assert request.getUser() != null;
        assert request.getUser().getAlias() != null;
        assert request.getCurrUser() != null;
        assert request.getCurrUser().getAlias() != null;
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FollowResponse("AuthToken is no longer valid.");
        }

        boolean isSuccess = followDAOInterface.follow(request.getUser().getAlias(),
                request.getCurrUser().getAlias());
        return new FollowResponse(isSuccess);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            assert request.getUser() != null;
            assert request.getUser().getAlias() != null;
            assert request.getCurrUser() != null;
            assert request.getCurrUser().getAlias() != null;
            assert request.getAuthToken() != null;
            assert request.getAuthToken().getCurrUserAlias() != null;

            if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                    request.getAuthToken().getCurrUserAlias())) {
                return new UnfollowResponse("AuthToken is no longer valid.");
            }

            boolean isSuccess = followDAOInterface.unfollow(request.getUser().getAlias(),
                    request.getCurrUser().getAlias());
            return new UnfollowResponse(isSuccess);
        }
        catch (RuntimeException e) {
            return new UnfollowResponse("Unfollow failed, try again.");
        }
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        assert request.getFollowee() != null;
        assert request.getFollowee().getAlias() != null;
        assert request.getCurrUser() != null;
        assert request.getCurrUser().getAlias() != null;
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new IsFollowerResponse("AuthToken is no longer valid.");
        }

        boolean isFollower = followDAOInterface.isFollower(request.getFollowee().getAlias(),
                request.getCurrUser().getAlias());
        return new IsFollowerResponse(true, isFollower);
    }
}
