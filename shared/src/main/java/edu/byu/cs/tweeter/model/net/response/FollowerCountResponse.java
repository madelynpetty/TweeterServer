package edu.byu.cs.tweeter.model.net.response;

public class FollowerCountResponse extends CountResponse {

    public FollowerCountResponse(String message) {
        super(message);
    }

    public FollowerCountResponse(int count) {
        super(count);
    }
}
