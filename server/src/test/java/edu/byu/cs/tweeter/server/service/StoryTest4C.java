package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOInterface.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOInterface.StoryDAOInterface;
import edu.byu.cs.tweeter.server.dao.StoryDAO;

class StoryTest4C {

    private AuthToken testUserAuthToken = null;
    private final User user = new User("story", "test", "@storytest",
            "https://maddiepettytweeterbucket.s3.us-west-2.amazonaws.com/%40storytest");
        //password: test

    // putting dao tests in although not required
    private AuthTokenDAOInterface authTokenDAOSpy;
    private StoryDAOInterface storyDAOSpy;

    private StatusService service;

    private Status status1 = null;
    private Status status2 = null;

    @BeforeEach
    void setup() {
        authTokenDAOSpy = Mockito.spy(new AuthTokenDAO());
        testUserAuthToken = authTokenDAOSpy.getNewAuthToken(user.getAlias());
        service = Mockito.spy(new StatusService());

        storyDAOSpy = Mockito.spy(new StoryDAO());

        List<String> urls2 = new ArrayList<>();
        urls2.add("https://google.com");

        List<String> mentions2 = new ArrayList<>();
        mentions2.add("@mp");

        status1 = new Status("hi, my name is story test", user, "2021/12/07 08:04:50", null, null);
        status2 = new Status("https://google.com is a great site @mp", user, "2021/12/07 08:08:10", urls2, mentions2);
    }

    @Test
    void testStoryPagesCorrectlyReturned() {
        List<Status> stories = Collections.emptyList();
        stories.add(status1);
        stories.add(status2);

        Mockito.when(storyDAOSpy.getStory(10, user.getAlias())).thenReturn(stories);

        StoryRequest request = new StoryRequest(testUserAuthToken, user.getAlias(), 10, null);
        StoryResponse response = service.getStory(request);

        Assertions.assertNull(response.getLastItem());
        Assertions.assertFalse(response.getHasMorePages());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(stories, response.getItems());
    }
}
