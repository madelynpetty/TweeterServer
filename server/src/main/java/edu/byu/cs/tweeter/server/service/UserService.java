package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOInterface.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOInterface.UserDAOInterface;

public class UserService {
    UserDAOInterface userDAOInterface = DAOFactory.getInstance().getUserDAO();
    AuthTokenDAOInterface authTokenDAOInterface = DAOFactory.getInstance().getAuthTokenDAO();

    public LoginResponse login(LoginRequest request) {
        try {
            User user = userDAOInterface.login(request.getAlias(), request.getPassword());
            AuthToken authToken = authTokenDAOInterface.getNewAuthToken(request.getAlias());
            return new LoginResponse(user, authToken);
        }
        catch (RuntimeException e) {
            return new LoginResponse(e.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = userDAOInterface.register(request.getAlias(), request.getFirstName(),
                request.getLastName(), request.getPassword(), request.getImageUrl());
        AuthToken authToken = authTokenDAOInterface.getNewAuthToken(request.getAlias());
        return new RegisterResponse(user, authToken);
    }

    public static String getImageUrl(String alias, String url) { //used in register
        DAOFactory.getInstance().getS3DAO().putUrl(alias, url);
        return DAOFactory.getInstance().getS3DAO().getUrl(alias);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() != null && request.getAuthToken().getIdentifier() != null) {
            authTokenDAOInterface.removeAuthToken(request.getAuthToken().getIdentifier());
        }
        boolean isSuccess = userDAOInterface.logout(request.getAuthToken());
        return new LogoutResponse(isSuccess);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        User user = userDAOInterface.getUser(request.getAlias());
        return new GetUserResponse(true, user);
    }
}
