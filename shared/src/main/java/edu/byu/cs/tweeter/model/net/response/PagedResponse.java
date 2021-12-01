package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

/**
 * A response that can indicate whether there is more data available from the server.
 */
public class PagedResponse<T> extends Response {


    private boolean hasMorePages;
    private List<T> items;

    PagedResponse(boolean success, boolean hasMorePages, List<T> items) {
        super(success);
        this.hasMorePages = hasMorePages;
        this.items = items;
    }

    PagedResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message);
        this.hasMorePages = hasMorePages;
    }

    PagedResponse() {
        super();
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */
    public boolean getHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(Boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    /**
     * Returns the items for the corresponding request.
     *
     * @return the items.
     */
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
