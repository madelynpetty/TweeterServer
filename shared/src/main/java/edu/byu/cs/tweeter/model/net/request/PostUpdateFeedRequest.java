package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostUpdateFeedRequest {
    private List<String> followers;
    private Status status;

    private PostUpdateFeedRequest() {}

    public PostUpdateFeedRequest(List<String> followers, Status status) {
        this.followers = followers;
        this.status = status;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
