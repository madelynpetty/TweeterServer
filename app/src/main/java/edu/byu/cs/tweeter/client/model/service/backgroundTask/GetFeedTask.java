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
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetFeedTask";
    private static final String URL_PATH = "/getfeed";

    private final FeedRequest feedRequest;
    private PagedResponse<Status> feedResponse;

    public GetFeedTask(FeedRequest feedRequest, User targetUser, Status lastStatus,
                       boolean hasMorePages, Handler messageHandler) {
        super(feedRequest.getAuthToken(), targetUser, feedRequest.getLimit(), lastStatus,
                hasMorePages, messageHandler);
        this.feedRequest = feedRequest;
        this.targetUser = targetUser;
        this.lastItem = lastStatus;
        this.limit = feedRequest.getLimit();
    }

    @Override
    protected List<Status> getItems() {
        return getPageOfStatusItem(lastItem, limit, feedResponse.getItems());
    }

    @Override
    protected PagedResponse<Status> getResponse() {
        try {
            feedResponse = new FollowService().getServerFacade().getFeed(feedRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return feedResponse;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        return null;
    }
}
