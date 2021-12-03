package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {
    public static final String IS_FOLLOWER_KEY = "is-follower";
    public static final String LOG_TAG = "IsFollowerTask";
    public static final String URL_PATH = "/isfollower";

    private IsFollowerRequest request;
    private AuthenticatedResponse response;

    public IsFollowerTask(IsFollowerRequest request, AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        this.request = request;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask(){
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
        try {
            response = new FollowService().getServerFacade().isFollower(request, URL_PATH);
            // for some reason the response is giving me follower of false, even though it's printing
            // as true. it should be true. Why?
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, response.getIsFollower());
    }
}
