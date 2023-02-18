import java.rmi.*;

public interface ShareHolding extends Remote {
    String getShareHoldingName();
    int getNumberOfOwnedShareHoldings();
    float getShareHoldingPrice();

}