package edu.byu.cs.tweeter.client.model.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class GetStoryTest {
    //using this in m4

    private StoryRequest validRequest;
    private StoryResponse successResponse;
    private StoryServiceObserver observer;
    private CountDownLatch countDownLatch;
    private User currentUser = new User("FirstName", "LastName", null);

    @Before
    public void setup() {
        String dateTime = LocalDate.now().toString();

        List<String> urls = new ArrayList<>();
        urls.add("https://google.com");
        urls.add("https://byu.com");

        List<String> mentions = new ArrayList<>();
        mentions.add("@bob");
        mentions.add("@susie");
        mentions.add("@allen");

        Status currentStatus = new Status("Hello", currentUser, dateTime, urls, mentions);

        Status resultStatus1 = new Status("Hello1", currentUser, dateTime, urls, mentions);
        Status resultStatus2 = new Status("Hello2", currentUser, dateTime, urls, mentions);
        Status resultStatus3 = new Status("Hello3", currentUser, dateTime, urls, mentions);

        // Setup valid and invalid requests to be used in the tests
        validRequest = new StoryRequest(new AuthToken(), currentUser.getAlias(), 3, null);

        // Setup success and failure responses to be used in the tests
        List<Status> success_stories = Arrays.asList(resultStatus1, resultStatus2, resultStatus3);
        successResponse = new StoryResponse(success_stories, false);

        // Setup an observer for the StoryService
        observer = new StoryServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class StoryServiceObserver implements StatusService.StoryObserver {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void statusSucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
            this.success = true;
            this.message = null;
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailed(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    private StatusService setupStatusServiceSpy(StoryResponse serverFacadeResponse) {
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        try {
            Mockito.when(mockServerFacade.getStory(Mockito.any(), Mockito.any())).thenReturn(serverFacadeResponse);
        } catch (Exception e) {
            // We won't actually get an exception while setting up the mock
        }

        StatusService statusService = new StatusService();
        StatusService statusServiceSpy = Mockito.spy(statusService);
        Mockito.when(statusServiceSpy.getServerFacade()).thenReturn(mockServerFacade);

        return statusServiceSpy;
    }

    private static void assertEquals(StoryResponse response, StoryServiceObserver observer) {
        Assert.assertEquals(response.isSuccess(), observer.isSuccess());
        Assert.assertEquals(response.getMessage(), observer.getMessage());
        Assert.assertEquals(response.getStatuses(), observer.getStatuses());
        Assert.assertEquals(response.getHasMorePages(), observer.getHasMorePages());
        Assert.assertNull(observer.getException());
    }

    private static void assertEquals(Exception exception, StoryServiceObserver observer) {
        Assert.assertFalse(observer.isSuccess());
        Assert.assertNull(observer.getMessage());
        Assert.assertNull(observer.getStatuses());
        Assert.assertFalse(observer.getHasMorePages());
        Assert.assertEquals(exception, observer.getException());
    }

    @Test
    public void testGetStatuses_validRequest_correctResponse() throws InterruptedException {
        StatusService statusServiceSpy = setupStatusServiceSpy(successResponse);

        statusServiceSpy.getStory(observer, currentUser, validRequest.getLastStatus());
        awaitCountDownLatch();

        assertEquals(successResponse, observer);
    }
}