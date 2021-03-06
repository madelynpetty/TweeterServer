package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;

public class FollowService {
    private static final int PAGE_SIZE = 10;

    private static ServerFacade serverFacade;

    public ServerFacade getServerFacade() {
        if (serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }

    //for tests
    public GetFollowersTask getGetFollowerTask(FollowerRequest followerRequest, User user,
                                               User lastFollower, boolean hasMorePages,
                                               GetFollowersObserver observer) {
        return new GetFollowersTask(followerRequest, user,
                lastFollower, hasMorePages, new GetFollowersHandler(observer));
    }

    //GET FOLLOWING

    public interface GetFollowingObserver extends ServiceObserver {
        void followSucceeded(List<User> users, boolean hasMorePages, User lastFollowee);
    }

    public void getFollowing(User targetUser, int limit, User lastFollowee, boolean hasMorePages, GetFollowingObserver observer) {
        FollowingRequest followingRequest;

        if (lastFollowee == null) {
            followingRequest = new FollowingRequest(Cache.getInstance().getCurrUserAuthToken(),
                    targetUser.getAlias(), limit, null);
        }
        else {
            followingRequest = new FollowingRequest(Cache.getInstance().getCurrUserAuthToken(),
                    targetUser.getAlias(), limit, lastFollowee.getAlias());
        }

        GetFollowingTask getFollowingTask = new GetFollowingTask(followingRequest, targetUser,
                lastFollowee, hasMorePages, new GetFollowingHandler(observer));
        new ExecuteTask<>(getFollowingTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends BackgroundTaskHandler {
        public GetFollowingHandler(GetFollowingObserver observer) {
            super(observer);
        }


        @Override
        protected void handleSuccessMessage(Message msg) {
            List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            User lastFollowee = null;
            if (followees != null)
                lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;

            try {
                ((FollowService.GetFollowingObserver)observer).followSucceeded(followees, hasMorePages, lastFollowee);
            } catch (Exception e) {
                observer.handleException(e);
            }
        }
    }


    //GET FOLLOWERS

    public interface GetFollowersObserver extends ServiceObserver {
        void followSucceeded(List<User> followers, boolean hasMorePages, User lastFollower);
    }

    public static void getFollowers(GetFollowersObserver observer, User user, User lastFollower,
                                    boolean hasMorePages) {
        FollowerRequest followerRequest;
        if (lastFollower == null) {
            followerRequest = new FollowerRequest(Cache.getInstance().getCurrUserAuthToken(),
                    user.getAlias(), PAGE_SIZE, null);
        }
        else {
            followerRequest = new FollowerRequest(Cache.getInstance().getCurrUserAuthToken(),
                    user.getAlias(), PAGE_SIZE, lastFollower.getAlias());
        }

        GetFollowersTask getFollowersTask = new GetFollowersTask(followerRequest, user,
                lastFollower, hasMorePages, new GetFollowersHandler(observer));
        new ExecuteTask<>(getFollowersTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowersHandler extends BackgroundTaskHandler {
        GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
            User lastFollower = null;
            if (followers != null)
                lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            try {
                ((GetFollowersObserver)observer).followSucceeded(followers, hasMorePages, lastFollower);
            } catch (Exception e) {
                observer.handleException(e);
            }
        }
    }


    //FOLLOW

    public interface FollowObserver extends ServiceObserver {
        void follow(User user);
        void setFollowButton(boolean enabled);
        void updateFollowButton(boolean removed);
        void callUpdateSelectedUserFollowingAndFollowers(User user);
    }

    private User selectedUser;

    public void follow(FollowService.FollowObserver observer, User selectedUser) {
        this.selectedUser = selectedUser;
        FollowRequest followRequest = new FollowRequest(selectedUser,
                Cache.getInstance().getCurrUser(),
                Cache.getInstance().getCurrUserAuthToken());
        FollowTask followTask = new FollowTask(followRequest, new FollowService.FollowHandler(observer));
        new ExecuteTask<>(followTask);
    }

    private class FollowHandler extends BackgroundTaskHandler {
        public FollowHandler(FollowService.FollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            ((FollowObserver)observer).callUpdateSelectedUserFollowingAndFollowers(selectedUser);
            ((FollowObserver)observer).updateFollowButton(false);
            ((FollowObserver)observer).setFollowButton(true);
        }
    }

    public void updateSelectedUserFollowingAndFollowers(FollowerCountObserver followerCountObserver,
                                                        FollowingCountObserver followingCountObserver,
                                                        User selectedUser) {
        // Get count of most recently selected user's followers.
        FollowerCountRequest followerCountRequest = new FollowerCountRequest(
                Cache.getInstance().getCurrUserAuthToken(), selectedUser);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(followerCountRequest,
                new FollowService.GetFollowersCountHandler(followerCountObserver));

        // Get count of most recently selected user's followees (who they are following)
        FollowingCountRequest followingCountRequest = new FollowingCountRequest(
                Cache.getInstance().getCurrUserAuthToken(), selectedUser);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(
                followingCountRequest, new FollowService.GetFollowingCountHandler(followingCountObserver));

        new ExecuteTask<>(followersCountTask, followingCountTask);
    }


    //UNFOLLOW

    public interface UnfollowObserver extends ServiceObserver {
        void setFollowButton(boolean enabled);
        void updateFollowButton(boolean removed);
        void callUpdateSelectedUserFollowingAndFollowers(User user);
    }

    public void unfollow(FollowService.UnfollowObserver observer, User selectedUser) {
        this.selectedUser = selectedUser;
        UnfollowRequest unfollowRequest = new UnfollowRequest(selectedUser,
                Cache.getInstance().getCurrUser(),
                Cache.getInstance().getCurrUserAuthToken());
        UnfollowTask unfollowTask = new UnfollowTask(unfollowRequest, new UnfollowHandler(observer));
        new ExecuteTask<>(unfollowTask);
    }

    private class UnfollowHandler extends BackgroundTaskHandler {
        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            ((UnfollowObserver)observer).callUpdateSelectedUserFollowingAndFollowers(selectedUser);
            ((UnfollowObserver)observer).updateFollowButton(true);
            ((UnfollowObserver)observer).setFollowButton(true);
        }
    }


    //FOLLOWER COUNT

    public interface FollowerCountObserver extends ServiceObserver {
        void setFollowerCount(int count);
    }

    private class GetFollowersCountHandler extends BackgroundTaskHandler {
        public GetFollowersCountHandler(FollowerCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            ((FollowerCountObserver)observer).setFollowerCount(count);
        }
    }


    //FOLLOWING COUNT

    public interface FollowingCountObserver extends ServiceObserver {
        void setFollowingCount(int count);
    }

    private class GetFollowingCountHandler extends BackgroundTaskHandler {
        public GetFollowingCountHandler(FollowingCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            ((FollowingCountObserver)observer).setFollowingCount(count);
        }
    }


    //IS FOLLOWER

    public interface IsFollowerObserver extends ServiceObserver {
        void setIsFollowerButton();
        void setIsNotFollowerButton();
    }

    public void isFollower(IsFollowerObserver observer, User user) {
        IsFollowerRequest request = new IsFollowerRequest(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), user);
        IsFollowerTask isFollowerTask = new IsFollowerTask(request, Cache.getInstance().getCurrUserAuthToken(),
                new IsFollowerHandler(observer));
        new ExecuteTask<>(isFollowerTask);
    }

    private class IsFollowerHandler extends BackgroundTaskHandler {
        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            // If logged-in user is a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                ((IsFollowerObserver)observer).setIsFollowerButton();
            } else {
                ((IsFollowerObserver)observer).setIsNotFollowerButton();
            }
        }
    }
}
