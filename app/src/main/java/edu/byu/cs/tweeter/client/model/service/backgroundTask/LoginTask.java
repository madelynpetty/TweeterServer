package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    private static final String LOG_TAG = "LoginTask";
    private static final String URL_PATH = "/login";

    private AuthenticateRequest loginRequest;
    private AuthenticateResponse loginResponse;

    public LoginTask(AuthenticateRequest loginRequest, Handler messageHandler) {
        super(loginRequest, messageHandler);
        this.loginRequest = loginRequest;
    }

    @Override
    protected AuthenticateResponse runAuthenticationTask() {
        try {
            loginResponse = new UserService().getServerFacade().login(loginRequest, URL_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }

        return loginResponse;
    }

}
