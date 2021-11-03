package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

public class UserService {

    private static ServerFacade serverFacade;

    public ServerFacade getServerFacade() {
        if (serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }

    //GET USER

    public interface GetUserObserver extends ServiceObserver {
        void getUserSucceeded(User user);
    }

    public static void getUsers(AuthToken authtoken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authtoken, alias, new GetUserHandler(observer));
        new ExecuteTask<>(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends BackgroundTaskHandler {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            ((GetUserObserver)observer).getUserSucceeded(user);
        }
    }


    //REGISTER

    public interface RegisterObserver extends ServiceObserver {
        void registerSucceeded(AuthToken authToken, User user);
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload, UserService.RegisterObserver observer) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Send register request.
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, alias, password, imageBytesBase64);
        RegisterTask registerTask = new RegisterTask(registerRequest, new RegisterHandler(observer));
        new ExecuteTask<>(registerTask);
    }

    private class RegisterHandler extends BackgroundTaskHandler {
        public RegisterHandler(UserService.RegisterObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((RegisterObserver)observer).registerSucceeded(authToken, registeredUser);
        }
    }


    //LOGIN

    public interface LoginObserver extends ServiceObserver {
        void loginSucceeded(AuthToken authToken, User user);
    }

    public void login(String alias, String password, LoginObserver observer) {
        // Send the login request.
        LoginRequest loginRequest = new LoginRequest(alias, password);
        LoginTask loginTask = new LoginTask(loginRequest, new LoginHandler(observer));
        new ExecuteTask<>(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends BackgroundTaskHandler {
        public LoginHandler(LoginObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((LoginObserver)observer).loginSucceeded(authToken, loggedInUser);
        }
    }


    //LOGOUT

    public interface LogoutObserver extends ServiceObserver {
        void logoutSucceeded();
    }

    public void logout(UserService.LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new UserService.LogoutHandler(observer));
        new ExecuteTask<>(logoutTask);
    }

    private class LogoutHandler extends BackgroundTaskHandler {
        public LogoutHandler(UserService.LogoutObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            ((LogoutObserver)observer).logoutSucceeded();
        }
    }
}
