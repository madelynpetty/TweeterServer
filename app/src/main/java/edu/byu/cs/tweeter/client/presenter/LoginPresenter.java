package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.LoginObserver {
    public interface View {
        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private View view;

    public LoginPresenter(View view) {
        this.view = view;
    }

    public void login(String alias, String password) {
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateLogin(alias, password);

        if (message == null) {
            view.displayInfoMessage("Logging In...");
            new UserService().login(alias, password, this);
        }
        else {
            view.displayErrorMessage("Login failed: " + message);
        }
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    @Override
    public void loginSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage("Login failed: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayErrorMessage("Login failed because of exception: " + ex.getMessage());
    }
}
