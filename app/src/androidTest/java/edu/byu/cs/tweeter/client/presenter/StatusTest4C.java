//package edu.byu.cs.tweeter.client.presenter;
//
//import android.view.View;
//
//import org.mockito.Mockito;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
//import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
//
//public class StatusTest4C {
//
//    @BeforeEach
//    public void setup() {
//        LoginPresenter loginPresenter = new LoginPresenter(mockView);
//        loginPresenter.login("@mp", "hi");
//
//        Mockito.when(followServiceSpy.getFollowingDAO()).thenReturn(mockFollowDAO);
//    }
//
//    /**
//     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
//     * method returns the same result as the {@link FollowDAO} class.
//     */
//    @Test
//    public void testGetFollowees_validRequest_correctResponse() {
//        FollowingResponse response = followServiceSpy.getFollowees(request);
//        Assertions.assertEquals(expectedResponse, response);
//    }
//}
