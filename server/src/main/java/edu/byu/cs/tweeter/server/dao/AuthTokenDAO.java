package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.FakeData;

public class AuthTokenDAO {
    public AuthToken getAuthToken() {
        FakeData fakeData = new FakeData();
        return fakeData.getAuthToken();
    }
}
