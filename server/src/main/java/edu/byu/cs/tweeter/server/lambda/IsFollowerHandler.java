package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class IsFollowerHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {

    /**
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the success or failure of following user.
     */
    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest request, Context context) {
        FollowService service = new FollowService();
        return service.isFollower(request);
    }

}
