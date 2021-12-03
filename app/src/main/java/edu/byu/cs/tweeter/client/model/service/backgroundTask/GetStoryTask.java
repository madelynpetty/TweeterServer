package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetStoryTask";
    private static final String URL_PATH = "/getstory";

    private final StoryRequest storyRequest;
    private PagedResponse<Status> storyResponse;
    private List<Status> items;

    public GetStoryTask(StoryRequest storyRequest, User targetUser, Status lastStatus,
                        boolean hasMorePages, Handler messageHandler) {
        super(storyRequest.getAuthToken(), targetUser, storyRequest.getLimit(), lastStatus,
                hasMorePages, messageHandler);
        this.storyRequest = storyRequest;
        this.targetUser = targetUser;
        this.lastItem = lastStatus;
        this.limit = storyRequest.getLimit();
    }

    @Override
    protected List<Status> getItems() {
        return getPageOfStatusItem(lastItem, limit, items);
    }

    @Override
    protected PagedResponse<Status> getResponse() {
        try {
            storyResponse = new FollowService().getServerFacade().getStory(storyRequest, URL_PATH);
            this.items = storyResponse.getItems();
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return storyResponse;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        return null;
    }
}
