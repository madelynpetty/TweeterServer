//package edu.byu.cs.tweeter.client.model.service;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import edu.byu.cs.tweeter.client.model.net.ServerFacade;
//import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
//import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
//
//public class GetFollowersTest {
//
//    private FollowerRequest validRequest;
//    private FollowerRequest invalidRequest;
//
//    private FollowerResponse successResponse;
//    private FollowerResponse failureResponse;
//
//    private FollowServiceObserver observer;
//
//    private CountDownLatch countDownLatch;
//
//    /**
//     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
//     * requests.
//     */
//    @Before
//    public void setup() {
//        User currentUser = new User("FirstName", "LastName", null);
//
//        User resultUser1 = new User("FirstName1", "LastName1",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
//        User resultUser2 = new User("FirstName2", "LastName2",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//        User resultUser3 = new User("FirstName3", "LastName3",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//
//        // Setup valid and invalid requests to be used in the tests
//        validRequest = new FollowerRequest(new AuthToken(), currentUser, 3, null);
//        invalidRequest = new FollowerRequest(null, null, 0, null);
//
//        // Setup success and failure responses to be used in the tests
//        List<User> success_followees = Arrays.asList(resultUser1, resultUser2, resultUser3);
//        successResponse = new FollowerResponse(success_followees, false);
//
//        failureResponse = new FollowerResponse("An exception occurred");
//
//        // Setup an observer for the FollowService
//        observer = new FollowServiceObserver();
//
//        // Prepare the countdown latch
//        resetCountDownLatch();
//    }
//
//    private void resetCountDownLatch() {
//        countDownLatch = new CountDownLatch(1);
//    }
//
//    private void awaitCountDownLatch() throws InterruptedException {
//        countDownLatch.await();
//        resetCountDownLatch();
//    }
//
//    /**
//     * A {@link FollowService.GetFollowersObserver} implementation that can be used to get the values
//     * eventually returned by an asynchronous call on the {@link FollowService}. Counts down
//     * on the countDownLatch so tests can wait for the background thread to call a method on the
//     * observer.
//     */
//    private class FollowServiceObserver implements FollowService.GetFollowersObserver {
//
//        private boolean success;
//        private String message;
//        private List<User> followers;
//        private boolean hasMorePages;
//        private Exception exception;
//
//        @Override
//        public void followSucceeded(List<User> followers, boolean hasMorePages, User lastFollower) {
//            this.success = true;
//            this.message = null;
//            this.followers = followers;
//            this.hasMorePages = hasMorePages;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleFailed(String message) {
//            this.success = false;
//            this.message = message;
//            this.followers = null;
//            this.hasMorePages = false;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            this.success = false;
//            this.message = null;
//            this.followers = null;
//            this.hasMorePages = false;
//            this.exception = exception;
//
//            countDownLatch.countDown();
//        }
//
//        public boolean isSuccess() {
//            return success;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public List<User> getFollowers() {
//            return followers;
//        }
//
//        public boolean getHasMorePages() {
//            return hasMorePages;
//        }
//
//        public Exception getException() {
//            return exception;
//        }
//    }
//
//    private FollowService setupFollowerServiceSpy(FollowerResponse serverFacadeResponse) {
//        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
//        try {
//            Mockito.when(mockServerFacade.getFollowers(Mockito.any(), Mockito.any())).thenReturn(serverFacadeResponse);
//        } catch (Exception e) {
//            // We won't actually get an exception while setting up the mock
//        }
//
//        FollowService followService = new FollowService();
//        FollowService followServiceSpy = Mockito.spy(followService);
//        Mockito.when(followServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
//
//        return followServiceSpy;
//    }
//
//    private static void assertEquals(FollowerResponse response, FollowServiceObserver observer) {
//        Assert.assertEquals(response.isSuccess(), observer.isSuccess());
//        Assert.assertEquals(response.getMessage(), observer.getMessage());
//        Assert.assertEquals(response.getFollowers(), observer.getFollowers());
//        Assert.assertEquals(response.getHasMorePages(), observer.getHasMorePages());
//        Assert.assertNull(observer.getException());
//    }
//
//    private static void assertEquals(Exception exception, FollowServiceObserver observer) {
//        Assert.assertFalse(observer.isSuccess());
//        Assert.assertNull(observer.getMessage());
//        Assert.assertNull(observer.getFollowers());
//        Assert.assertFalse(observer.getHasMorePages());
//        Assert.assertEquals(exception, observer.getException());
//    }
//
//    /**
//     * Verify that for successful requests, the {@link FollowService#getFollowers}
//     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
//     */
//    @Test
//    public void testGetFollowees_validRequest_correctResponse() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(successResponse);
//
//        followServiceSpy.getFollowers(observer, validRequest.getFollower(), validRequest.getLastFollower());
//        awaitCountDownLatch();
//
//        assertEquals(successResponse, observer);
//    }
//
//    /**
//     * Verify that for successful requests, the the {@link FollowService#getFollowers}
//     * method loads the profile image of each user included in the result.
//     */
//    @Test
//    public void testGetFollowees_validRequest_loadsProfileImages() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(successResponse);
//
//        followServiceSpy.getFollowers(observer, validRequest.getFollower(), validRequest.getLastFollower());
//        awaitCountDownLatch();
//
//        List<User> followees = observer.getFollowers();
//        Assert.assertTrue(followees.size() > 0);
//
//        for(User user : followees) {
//            Assert.assertNotNull(user.getImageBytes());
//        }
//    }
//
//    /**
//     * Verify that for unsuccessful requests, the the {@link FollowService#getFollowers}
//     * method returns the same failure response as the server facade.
//     */
//    @Test
//    public void testGetFollowees_invalidRequest_returnsNoFollowees() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(failureResponse);
//
//        followServiceSpy.getFollowers(observer, invalidRequest.getFollower(), invalidRequest.getLastFollower());
//        awaitCountDownLatch();
//
//        assertEquals(failureResponse, observer);
//    }
//
//    /**
//     * Verify that when an IOException occurs while loading an image, the {@link FollowService.GetFollowersObserver#handleException(Exception)}
//     * method of the Service's observer is called and the exception is passed to it.
//     */
//    @Test
//    public void testGetFollowees_exceptionThrownLoadingImages_observerHandleExceptionMethodCalled() throws IOException, InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(successResponse);
//
//        // Create a task spy for the FollowService that throws an exception when it's loadImages method is called
//        GetFollowersTask getFollowerTask = followServiceSpy.getGetFollowerTask(validRequest, validRequest.getFollower(),
//                validRequest.getLastFollower(), observer);
//        GetFollowersTask getFollowerTaskSpy = Mockito.spy(getFollowerTask);
//
//        IOException exception = new IOException();
//        Mockito.doThrow(exception).when(getFollowerTaskSpy).run();
//
//        // Make the FollowService spy use the RetrieveFollowingTask spy
//        Mockito.when(followServiceSpy.getGetFollowerTask(validRequest, validRequest.getFollower(),
//                validRequest.getLastFollower(), observer)).thenReturn(getFollowerTaskSpy);
//
//        followServiceSpy.getFollowers(observer, validRequest.getFollower(), validRequest.getLastFollower());
//        awaitCountDownLatch();
//
//        Assert.assertEquals(exception, observer.getException());
//    }
//}
