package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    private static final String LOG_TAG = "LogoutTask";
    private static final String URL_PATH = "/getfollowingcount";

    private FollowingCountRequest request;
    private CountResponse response;

    public GetFollowingCountTask(FollowingCountRequest request, Handler messageHandler) {
        super(request.getAuthToken(), request.getTargetUser(), messageHandler);
        this.request = request;
    }

    @Override
    protected CountResponse runCountTask() {
        try {
            response = new FollowService().getServerFacade().getFollowingCount(request, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return response;
    }
}
