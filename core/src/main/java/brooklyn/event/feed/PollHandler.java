package brooklyn.event.feed;

/**
 * Notified by the Poller of the result for each job, on each poll.
 * 
 * @author aled
 */
public interface PollHandler<V> {

    public boolean checkSuccess(V val);

    public void onSuccess(V val);

    public void onFailure(V val);

    public void onException(Exception exception);

    public String getDescription();
    
}
