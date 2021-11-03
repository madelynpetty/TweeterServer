package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

public class UserService {

    public LoginResponse login(LoginRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        FakeData fakeData = new FakeData();
        User user = fakeData.getFirstUser();
        AuthToken authToken = fakeData.getAuthToken();
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        FakeData fakeData = new FakeData();
        User user = fakeData.getFirstUser();
        AuthToken authToken = fakeData.getAuthToken();
        return new RegisterResponse(user, authToken);
    }
}
