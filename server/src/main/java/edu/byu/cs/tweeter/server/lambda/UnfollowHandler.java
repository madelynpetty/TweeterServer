package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {

    /**
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the success or failure of following user.
     */
    @Override
    public UnfollowResponse handleRequest(UnfollowRequest request, Context context) {
        FollowService service = new FollowService();
        return service.unfollow(request);
    }
}
