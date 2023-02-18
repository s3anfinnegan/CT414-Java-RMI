import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;


public interface ShareServer extends Remote {
    //login 
    long login(String username, String password) throws RemoteException, AuthentificationFailed;

    //download all shares currently available to purchase on server
    HashMap<String, Share> getAllShares(long token) throws RemoteException, AuthentificationFailed;

    //transfer funds to trading account (should require token)
    void depositFunds(long token, float amount) throws RemoteException, AuthentificationFailed;

    //withdraw funds (exception for insufficient funds)
    void withdrawFunds(long token, float amount) throws RemoteException, AuthentificationFailed, InsufficientFundsException;

    //purchase shares from account 
    void purchaseShares(long token, String name, int amount) throws RemoteException, AuthentificationFailed, InsufficientFundsException;

    //list of shares owned and their currency value
    HashMap<String, ShareHolding> getShareHoldings(long token) throws RemoteException, AuthentificationFailed;
}






//share holding position (currency, quantity, value)
/*class ShareHolding {
    private final String currency;
    private final int quantity;
    private final double value;

    public ShareHolding(String currency, int quantity, double value) {
        this.currency = currency;
        this.quantity = quantity;
        this.value = value;
    }

    public String getCurrency() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getValue() {
        return value;
    }
}*/


