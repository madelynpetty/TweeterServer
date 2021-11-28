package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class UserService {

    public LoginResponse login(LoginRequest request) {
        return getUserDAO().login(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        return getUserDAO().register(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return getUserDAO().logout(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        return getUserDAO().getUser(request);
    }

    UserDAO getUserDAO() {
        return new UserDAO();
    }
}
