package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetUserHandler implements RequestHandler<GetUserRequest, GetUserResponse> {

    @Override
    public GetUserResponse handleRequest(GetUserRequest request, Context context) {
        StatusService service = new StatusService();
        return service.getUser(request);
    }
}
