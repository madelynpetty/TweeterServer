package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedStatusTask extends PagedTask<Status> {

    protected PagedStatusTask(AuthToken authToken, User targetUser, int limit, Status lastItem,
                              boolean hasMorePages, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, hasMorePages, messageHandler);
    }

    @Override
    protected final List<User> getUsersForItems(List<Status> items) {
//        List<User> users = new ArrayList<>();
//        for (Status s : items) {
//            users.add(s.getUser());
//        }
//        return users;
        return items.stream().map(x -> x.user).collect(Collectors.toList());
    }

    protected List<Status> getPageOfStatusItem(Status lastStatus, int limit, List<Status> statuses) {

        List<Status> result = new ArrayList<Status>();

        int index = 0;;

        if (lastStatus != null) {
            for (int i = 0; i < statuses.size(); ++i) {
                Status curStatus = statuses.get(i);
                if (curStatus.getUser().getAlias().equals(lastStatus.getUser().getAlias()) &&
                        curStatus.getDate().equals(lastStatus.getDate())) {
                    index = i + 1;
                    break;
                }
            }
        }

        if (statuses != null) {
            for (int count = 0; index < statuses.size() && count < limit; ++count, ++index) {
                Status curStatus = statuses.get(index);
                result.add(curStatus);
            }
        }

        return result;
    }

}