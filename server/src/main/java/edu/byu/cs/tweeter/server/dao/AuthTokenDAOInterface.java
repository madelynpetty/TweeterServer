package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAOInterface {
     AuthToken getNewAuthToken(String userAlias);
     void checkValidAuthTokens();
     void removeAuthToken(String authTokenIdentifier);
     boolean validateUser(String authToken, String userAlias);
}
