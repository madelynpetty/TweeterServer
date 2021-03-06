package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOInterface.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.StoryDAOInterface;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {
    StoryDAOInterface storyDAOInterface = DAOFactory.getInstance().getStoryDAO();
    FeedDAOInterface feedDAOInterface = DAOFactory.getInstance().getFeedDAO();
    UserDAOInterface userDAOInterface = DAOFactory.getInstance().getUserDAO();
    AuthTokenDAOInterface authTokenDAOInterface = DAOFactory.getInstance().getAuthTokenDAO();

    public StoryResponse getStory(StoryRequest request) {
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;
        assert request.getAuthToken().getIdentifier() != null;
        assert request.getUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new StoryResponse("AuthToken is no longer valid.");
        }

        List<Status> responseStatuses = storyDAOInterface.getStory(request.getLimit(), request.getUserAlias());
        boolean hasMorePages = request.getLimit() <= responseStatuses.size();
        return new StoryResponse(responseStatuses, request.getLastStatus(), hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        assert request != null;
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;
        assert request.getAuthToken().getIdentifier() != null;
        assert request.getCurrUserAlias() != null;
        assert request.getPost() != null;
        assert request.getPost().getPost() != null;

//        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
//                request.getAuthToken().getCurrUserAlias())) {
//            return new PostStatusResponse("AuthToken is no longer valid.");
//        }

        boolean storySuccess = storyDAOInterface.postStatus(request.getCurrUserAlias(), request.getPost().getPost());
        User currUser = userDAOInterface.getUser(request.getCurrUserAlias());
        System.out.println("Sending feed message: " + currUser.getAlias());
        boolean feedSuccess = feedDAOInterface.sendFeedMessage(request.getPost().getPost(), currUser);
        return new PostStatusResponse(storySuccess && feedSuccess);
    }

    public FeedResponse getFeed(FeedRequest request) {
        assert request.getAuthToken() != null;
        assert request.getAuthToken().getCurrUserAlias() != null;
        assert request.getAuthToken().getIdentifier() != null;
        assert request.getUserAlias() != null;

        if (!authTokenDAOInterface.validateUser(request.getAuthToken().getIdentifier(),
                request.getAuthToken().getCurrUserAlias())) {
            return new FeedResponse("AuthToken is no longer valid.");
        }

        List<Status> allStatuses = feedDAOInterface.getFeed(request.getLimit(),
                request.getUserAlias(), request.getLastStatus());
        boolean hasMorePages = request.getLimit() <= allStatuses.size();
        return new FeedResponse(allStatuses, request.getLastStatus(), hasMorePages);
    }
}
