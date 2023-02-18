import java.io.Serializable;

public class ShareHoldingImpl implements ShareHolding, Serializable {
    private String shareHoldingName;
    private int numberOfOwnedShareHoldings;
    private float shareHoldingPrice;

    public ShareHoldingImpl(String shareHoldingName, int numberOfOwnedShareHoldings, float shareHoldingPrice){
        this.shareHoldingName = shareHoldingName;
        this.numberOfOwnedShareHoldings = numberOfOwnedShareHoldings;
        this.shareHoldingPrice = shareHoldingPrice;
    }
    @Override
    public String getShareHoldingName() {
        return shareHoldingName;
    }

    @Override
    public int getNumberOfOwnedShareHoldings() {
        return numberOfOwnedShareHoldings;
    }

    @Override
    public float getShareHoldingPrice() {
        return shareHoldingPrice;
    }
}