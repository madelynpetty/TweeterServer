package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    private static final String LOG_TAG = "LogoutTask";
    private static final String URL_PATH = "/getfollowercount";

    private FollowerCountRequest request;
    private CountResponse response;

    public GetFollowersCountTask(FollowerCountRequest request, Handler messageHandler) {
        super(request.getAuthToken(), request.getTargetUser(), messageHandler);
        this.request = request;
    }

    @Override
    protected CountResponse runCountTask() {
        try {
            response = new FollowService().getServerFacade().getFollowerCount(request, URL_PATH);
            System.out.println("hi");
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return response;
    }
}
