package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

public abstract class GetCountTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetCountTask";

    public static final String COUNT_KEY = "count";
    protected User targetUser;
    protected int count;

    protected GetCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
    }

    protected User getTargetUser() {
        return targetUser;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }

    @Override
    protected boolean runTask() {
        CountResponse countResponse = runCountTask();
        count = countResponse.getCount();
        return true;
    }

    protected abstract CountResponse runCountTask();
}
