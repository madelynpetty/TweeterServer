package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {
    private static final String LOG_TAG = "RegisterTask";
    private static final String URL_PATH = "/register";

    private AuthenticateRequest registerRequest;
    private AuthenticateResponse registerResponse;

    public RegisterTask(AuthenticateRequest registerRequest, Handler messageHandler) {
        super(registerRequest, messageHandler);
        this.registerRequest = registerRequest;
    }

    @Override
    protected AuthenticateResponse runAuthenticationTask() {
        try {
            registerResponse = new UserService().getServerFacade().register(registerRequest, URL_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }

        return registerResponse;
    }
}
