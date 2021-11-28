package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask {
    private static final String LOG_TAG = "AuthenticateTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private String username;
    private String password;
    protected User user;
    protected AuthToken authToken;

    protected AuthenticateTask(AuthenticateRequest request, Handler messageHandler) {
        super(messageHandler);
        this.username = request.getAlias();
        this.password = request.getPassword();
    }

    @Override
    protected final boolean runTask() throws IOException {
        AuthenticateResponse response = runAuthenticationTask();
        user = response.getUser();
        authToken = response.getAuthToken();
        BackgroundTaskUtils.loadImage(user);
        return true;
    }

    protected abstract AuthenticateResponse runAuthenticationTask();

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, this.user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}