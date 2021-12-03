package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "UnfollowTask";
    private static final String URL_PATH = "/unfollow";

    private UnfollowRequest request;
    private AuthenticatedResponse response;

    public UnfollowTask(UnfollowRequest unfollowRequest, Handler messageHandler) {
        super(unfollowRequest.getAuthToken(), messageHandler);
        this.request = unfollowRequest;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        try {
            response = new FollowService().getServerFacade().unfollowUser(request, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
