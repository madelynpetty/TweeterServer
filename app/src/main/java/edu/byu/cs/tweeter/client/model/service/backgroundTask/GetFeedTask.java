package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetFeedTask";
    private static final String URL_PATH = "/getfeed";

    private FeedRequest feedRequest;
    private PagedResponse feedResponse;

    public GetFeedTask(FeedRequest feedRequest, User targetUser, Status lastStatus, Handler messageHandler) {
        super(feedRequest.getAuthToken(), targetUser, feedRequest.getLimit(), lastStatus, messageHandler);
        this.feedRequest = feedRequest;
    }

    @Override
    protected List<Status> getItems() {
        return getFakeData().getPageOfStatusItem(lastItem, limit);
    }

    @Override
    protected PagedResponse getResponse() {
        try {
            feedResponse = new FollowService().getServerFacade().getFeed(feedRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return feedResponse;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        return null; //TODO this may become a problem
    }
}
