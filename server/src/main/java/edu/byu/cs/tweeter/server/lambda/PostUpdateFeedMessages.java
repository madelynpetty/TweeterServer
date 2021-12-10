package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.queue.BatchService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        assert event != null;
        assert event.getRecords() != null;

        BatchService batchService = new BatchService();
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Status status = (new Gson()).fromJson(msg.getBody(), Status.class);
            batchService.postUpdateFeed(status);
        }
        return null;
    }
}
