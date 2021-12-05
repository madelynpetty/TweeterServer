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
//import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
//import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
//import edu.byu.cs.tweeter.model.net.response.PagedResponse;
//
//public class GetFollowingCountTest {
//
//    private FollowingCountRequest validRequest;
//    private FollowingCountRequest invalidRequest;
//
//    private FollowingCountResponse successResponse;
//    private FollowingCountResponse failureResponse;
//
//    private FollowService.FollowingCountObserver observer;
//    private FollowService.GetFollowingObserver followObserver;
//
//    private CountDownLatch countDownLatch;
//
//    private User currentUser = new User("FirstName", "LastName", null);
//
//    /**
//     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
//     * requests.
//     */
//    @Before
//    public void setup() {
//        // Setup valid and invalid requests to be used in the tests
//        validRequest = new FollowingCountRequest(new AuthToken(), currentUser);
//        invalidRequest = new FollowingCountRequest(null, null);
//
//        // Setup success and failure responses to be used in the tests
//        successResponse = new FollowingCountResponse(20);
//
//        failureResponse = new FollowingCountResponse("An exception occurred");
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
//    private class FollowServiceObserver implements FollowService.FollowingCountObserver {
//
//        private boolean success;
//        private String message;
//        private int count;
//        private Exception exception;
//
//        @Override
//        public void setFollowingCount(int count) {
//            this.success = true;
//            this.message = null;
//            this.count = count;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleFailed(String message) {
//            this.success = false;
//            this.message = message;
//            this.count = -1;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            this.success = false;
//            this.message = null;
//            this.count = -1;
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
//        public int getCount() {
//            return count;
//        }
//
//        public Exception getException() {
//            return exception;
//        }
//    }
//
//    private FollowService setupFollowerServiceSpy(FollowService.FollowingCountObserver serverFacadeResponse) {
//        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
//        try {
//            Mockito.when(mockServerFacade.getFollowers(Mockito.any(), Mockito.any())).thenReturn((PagedResponse) serverFacadeResponse);
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
//    private static void assertEquals(int num, int num2) {
//        Assert.assertEquals(num, num2);
//    }
//
//    /**
//     * Verify that for successful requests, the {@link FollowService#getFollowers}
//     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
//     */
//    @Test
//    public void testGetFollowees_validRequest_correctResponse() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(observer);
//
//        followServiceSpy.getFollowing(currentUser, 20, validRequest.getTargetUser(), followObserver);
//        awaitCountDownLatch();
//
//        assertEquals(successResponse.getCount(), 20);
//    }
//
//    /**
//     * Verify that for successful requests, the the {@link FollowService#getFollowers}
//     * method loads the profile image of each user included in the result.
//     */
//    @Test
//    public void testGetFollowees_validRequest_loadsProfileImages() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(observer);
//
//        followServiceSpy.getFollowing(currentUser, 20, validRequest.getTargetUser(), followObserver);
//        awaitCountDownLatch();
//
//        Assert.assertEquals(successResponse.getCount(), 20);
//    }
//
//    /**
//     * Verify that for unsuccessful requests, the the {@link FollowService#getFollowers}
//     * method returns the same failure response as the server facade.
//     */
//    @Test
//    public void testGetFollowees_invalidRequest_returnsNoFollowees() throws InterruptedException {
//        FollowService followServiceSpy = setupFollowerServiceSpy(observer);
//
//        followServiceSpy.getFollowing(currentUser, 20, invalidRequest.getTargetUser(), followObserver);
//        awaitCountDownLatch();
//
//        assertEquals(failureResponse.getCount(), 20);
//    }
//}
