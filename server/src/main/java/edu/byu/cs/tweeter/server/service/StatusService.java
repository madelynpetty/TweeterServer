package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {

    public StoryResponse getStories(StoryRequest request) {
        return getStoryDAO().getStories(request);
    }

    StoryDAO getStoryDAO() {
        return new StoryDAO();
    }

    public FeedResponse getFeed(FeedRequest request) {
        return getFeedDAO().getFeed(request);
    }

    FeedDAO getFeedDAO() {
        return new FeedDAO();
    }
}