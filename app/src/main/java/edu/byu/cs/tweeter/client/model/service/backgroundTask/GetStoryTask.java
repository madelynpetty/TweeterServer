package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetStoryTask";
    private static final String URL_PATH = "/getstory";

    private StoryRequest storyRequest;
    private PagedResponse storyResponse;

    public GetStoryTask(StoryRequest storyRequest, User targetUser, Status lastStatus, Handler messageHandler) {
        super(storyRequest.getAuthToken(), targetUser, storyRequest.getLimit(), lastStatus, messageHandler);
        this.storyRequest = storyRequest;
    }

    @Override
    protected List<Status> getItems() {
        return getFakeData().getPageOfStatusItem(lastItem, limit);
    }

    @Override
    protected PagedResponse getResponse() {
        try {
            storyResponse = new FollowService().getServerFacade().getStory(storyRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return storyResponse;
    }
}
