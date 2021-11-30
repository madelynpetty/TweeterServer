package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.AuthenticatedResponse;

public abstract class AuthenticatedTask extends BackgroundTask {
    private static final String LOG_TAG = "AuthenticatedTask";

    private final AuthToken authToken;

    protected AuthenticatedTask(AuthToken authToken, Handler messageHandler) { //pass in user?
        super(messageHandler);
        this.authToken = authToken;
    }

    @Override
    protected boolean runTask() throws IOException {
        AuthenticatedResponse response = runAuthenticationTask();
        return true;
    }

    protected abstract AuthenticatedResponse runAuthenticationTask();
}