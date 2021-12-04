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
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDbFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.StoryDAOInterface;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {
    StoryDAOInterface storyDAOInterface = DAOFactory.getInstance().getStoryDAO();
    FeedDAOInterface feedDAOInterface = DAOFactory.getInstance().getFeedDAO();
    FollowDAOInterface followDAOInterface = DAOFactory.getInstance().getFollowDAO();

    public StoryResponse getStory(StoryRequest request) {
        List<Status> responseStatuses = storyDAOInterface.getStory(request.getLimit(), request.getUserAlias());
        boolean hasMorePages = request.getLimit() <= responseStatuses.size();
        return new StoryResponse(responseStatuses, request.getLastStatus(), hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        List<User> currUserFolloweeList = followDAOInterface.getFollowingList(request.getCurrUserAlias());
        feedDAOInterface.postStatus(request.getPost().getPost(), request.getCurrUserAlias(), currUserFolloweeList);
        boolean success = storyDAOInterface.postStatus(request.getCurrUserAlias(), request.getPost().getPost());
        return new PostStatusResponse(success);
    }

    public FeedResponse getFeed(FeedRequest request) {
        List<Status> allStatuses = feedDAOInterface.getFeed(request.getLimit(),
                request.getUserAlias(), request.getLastStatus());
        boolean hasMorePages = request.getLimit() <= allStatuses.size();
        return new FeedResponse(allStatuses, request.getLastStatus(), hasMorePages);
    }
}
