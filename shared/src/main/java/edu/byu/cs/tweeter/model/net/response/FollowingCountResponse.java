package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response {
    private int count;

    public FollowingCountResponse(String message) {
        super(false, message);
    }

    public FollowingCountResponse(int count) {
        super(true, null);
        this.count = count;
    }
}
