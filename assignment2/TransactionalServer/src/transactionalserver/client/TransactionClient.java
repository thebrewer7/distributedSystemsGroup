package transactionalserver.client;

/**
 *  This represents a Client that will communicate with the
 *  TransactionServerProxy which will in turn communicate with the Server side.
 *
 *  The client side will be specifying actions to occur with bank accounts
 */
public class TransactionClient {
    private int numberTransactions;
    private int numberAccounts;
    private int initialBalance;
    private String host;
    private int port;
    //private StringBuilder log;

    /**
     * Constructor
     * takes in a value that represents the clients account value
     */
    public TransactionClient(String clientPropertiesFile, serverPropertiesFile){
     Properties serverProperties = new PropertyHandler(serverPropertiesFile);
     host = serverProperties.getProperty("host");
     port = int.parseInt(serverProperties.getProperty("port"));
     numberAccounts = int.parseInt(serverProperties.getProperty("numAccounts"));
     numberTransactions = int.parseInt(serverProperties.getProperty("numTransactions"));
     initialBalance = int.parseInt(serverProperties.getProperty("initialBalance"));
    }

    public void run(){
      for(int i=0; i<numberTransactions; i++){
        new Thread(){
          public void run(){
            TransactionServerProxy transaction = new TransactionServerProxy(host,port);
            int transID = transaction.openTransaction()
            System.out.println("Transaction " + transID + " has started.");
            int accountMovingFrom = (int) Math.floor(Math.random() * numberAccounts);
            int accountMovingTo = (int) Math.floor(Math.random() * numberAccounts);
            int amountToMove = (int) Math.ceil(Math.random() * initialBalance);
            int balance;
            System.out.println("Transaction " + transID + " going from account " + accountMovingFrom +
            " to account " + accountMovingTo + ".");
            balance = transaction.read(accountMovingFrom);
            transaction.write(accountMovingFrom, balance-amount);
            balance = transaction.read(accountMovingTo);
            transaction.write(accountMovingTo, balance+amount);
            transaction.closeTransaction()
            System.out.println("Transaction " + transID + " has finished.");
          }
        }.start();
      }
    }
}
