import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ShareServer extends Remote {
    //login 
    long login(String username, String password) throws RemoteException, AuthenticationFailed;

    //paramenters for token
    void someRemoteMethod(long token, Object timeout) throws RemoteException, AuthenticationFailed;

    //download all shares currently available to purchase on server
    List<Share> getAllShares(long token) throws RemoteException, AuthenticationFailed;

    //transfer funds to trading account (should require token)
    void depositFunds(long token, double amount) throws RemoteException, AuthenticationFailed;

    //withdraw funds (exception for insufficient funds)
    void withdrawFunds(long token, double amount) throws RemoteException, AuthenticationFailed, InsufficientFundsException;

    //purchase shares from account 
    void purchaseShares(long token, String currency, int quantity) throws RemoteException, AuthenticationFailed, InsufficientFundsException;

    //list of shares owned and their currency value
    List<ShareHolding> getShareHoldings(long token) throws RemoteException, AuthenticationFailed;
}


//authentification failure
class AuthenticationFailed extends Exception {
    //exceptions means authentification failure (incorrect token or timeout)
    public AuthenticationFailed(String message) {
        message = "Error: token invalid"
        super(message);
    }
}

//insufficient funds
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        message = "Error: balance insufficient to withdraw desired amount"
        super(message);
    }
}

//share holding position (currency, quantity, value)
class ShareHolding {
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
}


