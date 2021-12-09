package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostUpdateFeedRequest;
import edu.byu.cs.tweeter.server.dao.queue.BatchService;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        assert event != null;
        assert event.getRecords() != null;

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            PostUpdateFeedRequest request = (new Gson()).fromJson(msg.getBody(), PostUpdateFeedRequest.class);
            BatchService batchService = new BatchService();
            batchService.updateFeedTable(request);
        }
        return null;
    }
}
