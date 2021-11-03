package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    private static final String LOG_TAG = "GetFollowersTask";
    private static final String URL_PATH = "/getfollowers";

    private FollowerRequest followerRequest;
    private PagedResponse followerResponse;

    public GetFollowersTask(FollowerRequest followerRequest, User targetUser, User lastFollower,
                            Handler messageHandler) {
        super(followerRequest.getAuthToken(), targetUser, followerRequest.getLimit(), lastFollower, messageHandler);
        this.followerRequest = followerRequest;
    }

    @Override
    protected List<User> getItems() {
        return getFakeData().getPageOfUsersItem(lastItem, limit, targetUser);
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
}
