package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {
    private static final String LOG_TAG = "GetFollowingTask";
    public static final String MORE_PAGES_KEY = "more-pages";
    private static final String URL_PATH = "/getfollowing";

    private FollowingRequest followingRequest;
    private PagedResponse followingResponse;

    public GetFollowingTask(FollowingRequest followingRequest, User follower, User lastFollowee,
                            boolean hasMorePages, Handler messageHandler) {
        super(followingRequest.getAuthToken(), follower, followingRequest.getLimit(), lastFollowee,
                hasMorePages, messageHandler);
        this.followingRequest = followingRequest;
    }

    @Override
    protected List<User> getItems() {
        return followingResponse.getItems();
    }

    @Override
    protected PagedResponse getResponse() {
        try {
            followingResponse = new FollowService().getServerFacade().getFollowing(followingRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
        return followingResponse;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        return null;
    }
}
