import java.io.Serializable;
import java.rmi.*;

public interface Share extends Remote, Serializable {
    String getShareName();
    int getVolumeOfSharesAvailable();
    float getSharePrice();
    double getTimeRemainingToPriceUpdate();
}