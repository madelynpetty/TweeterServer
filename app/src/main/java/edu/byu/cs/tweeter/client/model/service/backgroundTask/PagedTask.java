package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public abstract class PagedTask<T> extends AuthenticatedTask {
    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    protected User targetUser;
    protected int limit;
    protected T lastItem;
    protected List<T> items;
    protected boolean hasMorePages;

    public PagedTask(AuthToken authToken, User targetUser, int limit, T lastItem,
                     boolean hasMorePages, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastItem = lastItem;
        this.hasMorePages = hasMorePages;
    }

    @Override
    protected final boolean runTask() throws IOException {
        PagedResponse response = getResponse();
        List<T> pageOfItems = getItems();
        hasMorePages = response.getHasMorePages();

        for(User user : getUsersForItems(pageOfItems)) {
            BackgroundTaskUtils.loadImage(user);
        }
        return true;
    }

    protected abstract List<T> getItems();
    protected abstract PagedResponse getResponse();

    protected abstract List<User> getUsersForItems(List<T> items);

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) this.items);
        msgBundle.putBoolean(MORE_PAGES_KEY, this.hasMorePages);
    }
}