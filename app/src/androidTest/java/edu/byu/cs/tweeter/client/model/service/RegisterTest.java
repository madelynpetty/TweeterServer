package edu.byu.cs.tweeter.client.model.service;

import android.widget.ImageView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterTest {

    private RegisterRequest validRequest;
    private RegisterResponse successResponse;
    private UserServiceObserver observer;
    private CountDownLatch countDownLatch;
    private User currentUser = new User("FirstName", "LastName", null);

    @Before
    public void setup() {
        User user = new User("Bob", "Johnson", "image");

        validRequest = new RegisterRequest("bob", "johnson", "bobjohnson", "hi", "image");
        successResponse = new RegisterResponse(user, null);
        observer = new UserServiceObserver();

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class UserServiceObserver implements UserService.RegisterObserver {

        private boolean success;
        private String message;
        private Exception exception;

        private AuthToken authToken;
        private User user;

        @Override
        public void registerSucceeded(AuthToken authToken, User user) {
            this.success = true;
            this.message = null;
            this.exception = null;
            this.authToken = authToken;
            this.user = user;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailed(String message) {
            this.success = false;
            this.message = message;
            this.exception = null;
            this.authToken = null;
            this.user = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.exception = exception;
            this.authToken = null;
            this.user = null;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public AuthToken getAuthToken() {
            return authToken;
        }

        public User getUser() {
            return user;
        }

        public Exception getException() {
            return exception;
        }
    }

    private UserService setupUserServiceSpy(RegisterResponse serverFacadeResponse) {
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        try {
            Mockito.when(mockServerFacade.register(Mockito.any(), Mockito.any())).thenReturn(serverFacadeResponse);
        } catch (Exception e) {
            // We won't actually get an exception while setting up the mock
        }

        UserService userService = new UserService();
        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.when(userServiceSpy.getServerFacade()).thenReturn(mockServerFacade);

        return userServiceSpy;
    }

    private static void assertEquals(RegisterResponse response, UserServiceObserver observer) {
        Assert.assertEquals(response.isSuccess(), observer.isSuccess());
        Assert.assertEquals(response.getMessage(), observer.getMessage());
        Assert.assertEquals(response.getAuthToken(), observer.getAuthToken());
        Assert.assertEquals(response.getUser(), observer.getUser());
        Assert.assertNull(observer.getException());
    }

    private static void assertEquals(Exception exception, UserServiceObserver observer) {
        Assert.assertFalse(observer.isSuccess());
        Assert.assertNull(observer.getMessage());
        Assert.assertNull(observer.getAuthToken());
        Assert.assertNull(observer.getUser());
        Assert.assertEquals(exception, observer.getException());
    }

    @Test
    public void testGetStatuses_validRequest_correctResponse() throws InterruptedException {
        UserService statusServiceSpy = setupUserServiceSpy(new RegisterResponse(currentUser, null));

        ImageView image = new ImageView(null);
        //TODO doesn't work because I don't know how to create a nonnull image
        statusServiceSpy.register("bob", "johnson", "bob.johnson", "hi", image);
        awaitCountDownLatch();

        assertEquals(successResponse, observer);
    }
}