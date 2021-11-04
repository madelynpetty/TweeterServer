package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {
    private static final String LOG_TAG = "LogoutTask";
    private static final String URL_PATH = "/logout";

    private LogoutRequest logoutRequest;
    private LogoutResponse logoutResponse;

    public LogoutTask(LogoutRequest logoutRequest, Handler messageHandler) {
        super(logoutRequest.getAuthToken(), messageHandler);
        this.logoutRequest = logoutRequest;
    }

    @Override
    protected boolean runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try {
            logoutResponse = new UserService().getServerFacade().logout(logoutRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return logoutResponse.isSuccess();
    }

//    @Override
//    protected void loadSuccessBundle(Bundle msgBundle) {
//        // Nothing to load
//    }
}
