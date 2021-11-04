package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class FollowingCountHandler implements RequestHandler<FollowingCountRequest, FollowingCountResponse> {

    /**
     *
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the following count.
     */
    @Override
    public FollowingCountResponse handleRequest(FollowingCountRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getFollowingCount(request);
    }
}
