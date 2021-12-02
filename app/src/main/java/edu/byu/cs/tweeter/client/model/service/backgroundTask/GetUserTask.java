package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetUserTask";
    public static final String USER_KEY = "user";
    private static final String URL_PATH = "/getuser";

    private GetUserRequest request;
    private AuthenticatedResponse response;

    public GetUserTask(GetUserRequest request, Handler messageHandler) {
        super(request.getAuthToken(), messageHandler);
        this.request = request;
    }

    @Override
    protected AuthenticatedResponse runAuthenticationTask() {
        try {
            response = new FollowService().getServerFacade().getUser(request, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        User user = response.getUser();
        BackgroundTaskUtils.loadImage(user);
        msgBundle.putSerializable(USER_KEY, user);
    }
}
