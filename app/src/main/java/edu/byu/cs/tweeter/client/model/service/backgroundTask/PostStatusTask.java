package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {
    private static final String LOG_TAG = "PostStatusTask";
    private static final String URL_PATH = "/poststatus";

    private PostStatusRequest request;
    private AuthenticatedResponse response;

    public PostStatusTask(PostStatusRequest request, Handler messageHandler) {
        super(request.getAuthToken(), messageHandler);
        this.request = request;
    }

    @Override
    public AuthenticatedResponse runAuthenticationTask() {
        try {
            response = new FollowService().getServerFacade().postStatus(request, URL_PATH);
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
