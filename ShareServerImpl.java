import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class ShareServerImpl implements ShareServer {

    private String login = new String("user");
    private String password = new String("password");
    private static long token;
    private float userFunds = 0.0f;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    HashMap<String, Share> listOfAllShares = new HashMap<String, Share>();
    HashMap<String, ShareHolding> listOfUserOwnedShares = new HashMap<String, ShareHolding>();


    public ShareServerImpl() throws RemoteException {
        super();
        readShareData();
    }

    public static void main(String args[]) {
        try {
            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
                System.out.println("Setting the new security manager");
            }

            String serverName = "ShareServer";
            // create the share server stub and the share
            ShareServer shareServer = new ShareServerImpl();
            ShareServer stub = (ShareServer) UnicastRemoteObject.exportObject(shareServer, 0);
            // use the default port for rmi registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(serverName, stub);
            scheduler();
            System.out.println("Share Server has started");
        } catch (Exception exception) {
            System.out.println("Exception in Share Server");
            exception.printStackTrace();
        }
    }

    // generate a token if the user credentials are correct
    @Override
    public long login(String login, String password) throws AuthentificationFailed, RemoteException {
        if(this.login.equals(login) && this.password.equals(password)) {
            return token;
        } else {
            throw new AuthentificationFailed("Provided login or password does not match our records.");
        }
    }

    // check for exceptions and pass the list of all shares to the user
    @Override
    public HashMap<String, Share> getAllShares(long token) throws AuthentificationFailed, RemoteException {
        if(!(token == ShareServerImpl.token)) {
            throw new AuthentificationFailed("Your session has expired!");
        }
        return listOfAllShares;
    }

    // check for exceptions and deposit money to the user account
    @Override
    public void depositFunds(long token, float amount) throws RemoteException, AuthentificationFailed {
        if(!(token == ShareServerImpl.token)) {
            throw new AuthentificationFailed("Session timout");
        }
        userFunds += amount;
    }

    // check for exceptions and withdraw money from the user account

    @Override
    public void withdrawFunds(long token, float amount) throws AuthentificationFailed, RemoteException, InsufficientFundsException {
        if(!(token == ShareServerImpl.token)) {
            throw new AuthentificationFailed("Session timout");
        }
        if(amount > userFunds) {
            throw new InsufficientFundsException("Your account balance is too low to make this withdraw this amount of money!");
        }
        userFunds -= amount;
    }

    // check for exceptions and carry out a purchase of shares
    @Override
    public void purchaseShares(long token, String name, int amount) throws AuthentificationFailed, RemoteException, InsufficientFundsException {
        if(!(token == ShareServerImpl.token)) {
            throw new AuthentificationFailed("Session timout");
        }
        if(listOfAllShares.get(name).getSharePrice() > userFunds*amount) {
            throw new InsufficientFundsException("Your account balance is too low to buy this amount of shares!");
        }

        // check if the user has already bought some shares from the company
        // if no, create a new shareholding for this company
        // if yes, overwrite the shareholding with updated number of shares and price at the same key
        Share temp = listOfAllShares.get(name); //index 1 = name
        if(listOfUserOwnedShares.get(name) == null) {
            listOfUserOwnedShares.put(name, new ShareHoldingImpl(temp.getShareName(), amount, temp.getSharePrice()));
        } else {
            ShareHolding temp2 = listOfUserOwnedShares.get(name);
            listOfUserOwnedShares.replace(name, new ShareHoldingImpl(temp2.getShareHoldingName(),temp2.getNumberOfOwnedShareHoldings()+amount, temp.getSharePrice()));
        }
        // check if user decided to buy all shares
        // remove the share from the market if the user bought all shares for this company
        // otherwise adjust the share object to reflect the deducted amount of shares
        if((temp.getVolumeOfSharesAvailable() - amount) != 0) {
            listOfAllShares.replace(name, new ShareImpl(temp.getShareName(),temp.getVolumeOfSharesAvailable()-amount, temp.getSharePrice()));
        } else {
            listOfAllShares.remove(name);
        }
        userFunds -= temp.getSharePrice()*amount;
    }

    // check for exceptions and sell shares
    public void sell(Long token, String name, int amount) throws AuthentificationFailed, RemoteException {
        if(!(token.equals(ShareServerImpl.token)) || token == null) {
            throw new AuthentificationFailed("Session timout");
        }
        // check if the user has already bought some shares from the company
        // if no, create a new shareholding for this company
        // if yes, overwrite the shareholding with updated number of shares and price at the same key
        ShareHolding temp = listOfUserOwnedShares.get(name);
        if(listOfAllShares.get(name) == null) {
            listOfAllShares.put(name, new ShareImpl(name, amount, temp.getShareHoldingPrice()));
        } else {
            Share temp2 = listOfAllShares.get(name);
            listOfAllShares.replace(name, new ShareImpl(name, temp2.getVolumeOfSharesAvailable()+amount, temp.getShareHoldingPrice()));
        }
        // check if user decided to sell all shares
        // remove the share from user shares list if the user sold all shares for this company
        // otherwise adjust the share object to reflect the deducted amount of shares
        if((temp.getNumberOfOwnedShareHoldings() - amount) != 0) {
            listOfUserOwnedShares.replace(name, new ShareHoldingImpl(name, temp.getNumberOfOwnedShareHoldings()-amount, temp.getShareHoldingPrice()));
        } else {
            listOfUserOwnedShares.remove(name);
        }
        userFunds += temp.getShareHoldingPrice()*amount;

    }
    // check for exceptions and pass the user the list of all shares that he owns
    @Override
    public HashMap<String, ShareHolding> getShareHoldings(long token) throws AuthentificationFailed, RemoteException {
        if(!(token == ShareServerImpl.token)) {
            throw new AuthentificationFailed("Session timout");
        }
        return listOfUserOwnedShares;
    }

    // read data from the shares.csv file
    private void readShareData() {
        try (BufferedReader br = new BufferedReader(new FileReader("shares.csv"))) {
            String line;
            String delimiter = ",";
            String[] data;
            while ((line = br.readLine()) != null) {
                data = line.split(delimiter);
                listOfAllShares.put(data[0], new ShareImpl(data[0], Integer.parseInt(data[1]), Float.parseFloat(data[2])));
            }
         }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    // set up a runnable method to generate a new random token after 5 minutes pass
    private static void scheduler() {
        final Runnable tokenTimeout = new Runnable() {
            @Override
            public void run() {
                token = new Random().nextLong();
            }
        };

        // invoke the method every 5 minutes
        scheduler.scheduleAtFixedRate(tokenTimeout, 0, 5, MINUTES);

    }

}