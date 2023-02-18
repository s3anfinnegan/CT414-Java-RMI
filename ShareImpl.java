import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import static java.util.concurrent.TimeUnit.*;
public class ShareImpl implements Share, Serializable {

    private String shareName;
    private int noOfSharesAvailable;
    private float sharePrice;
    private transient final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private long timeNow;
    private long timeToPriceUpdate;

    public ShareImpl(String shareName, int noOfSharesAvailable, float sharePrice) {
        this.shareName = shareName;
        this. noOfSharesAvailable = noOfSharesAvailable;
        this.sharePrice = sharePrice;
        // create a new timer object and set it its first run of the updatePrice method to be in one minute and schedule the method to be invoked again in another minute
        timeNow = System.currentTimeMillis();
        timeToPriceUpdate = System.currentTimeMillis() + 60000;
        scheduler();
    }


    // schedule a timer to update the price of a share every minute by adding a value in range -30 to 30 to its price. append 60000 milliseconds (1 min) to timeNow
    // schedule a second timer to update the timeNow every second
    private void scheduler() {
        final Runnable updatePrice = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                sharePrice += -30 + random.nextFloat() * (60);
                timeToPriceUpdate =  timeNow + 60000;
            }
        };

        final Runnable updateTime = new Runnable() {
            @Override
            public void run() {
                timeNow = System.currentTimeMillis();
            }
        };
        scheduler.scheduleAtFixedRate(updatePrice, 60, 60, SECONDS);
        scheduler.scheduleAtFixedRate(updateTime, 0, 1, SECONDS);
    }



    @Override
    public String getShareName() {
        return shareName;
    }

    @Override
    public int getVolumeOfSharesAvailable() {
        return noOfSharesAvailable;
    }

    @Override
    public float getSharePrice() {
        return sharePrice;
    }

    @Override
    // return the difference between the two time values to get the time left until the share price will be updated.
    public double getTimeRemainingToPriceUpdate() {
        //DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf((timeToPriceUpdate/1000) - (timeNow/1000));
    }
}