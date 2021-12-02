package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    private static final String LOG_TAG = "GetFollowersTask";
    private static final String URL_PATH = "/getfollowers";

    private FollowerRequest followerRequest;
    private PagedResponse followerResponse;

    public GetFollowersTask(FollowerRequest followerRequest, User targetUser, User lastFollower,
                            boolean hasMorePages, Handler messageHandler) {
        super(followerRequest.getAuthToken(), targetUser, followerRequest.getLimit(), lastFollower,
                hasMorePages, messageHandler);
        this.followerRequest = followerRequest;
    }

    @Override
    protected List<User> getItems() {
        return followerResponse.getItems();
    }

    @Override
    protected PagedResponse getResponse() {
        try {
            followerResponse = new FollowService().getServerFacade().getFollowers(followerRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return followerResponse;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        return null;
    }
}
