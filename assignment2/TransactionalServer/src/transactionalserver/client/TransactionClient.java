package transactionalserver.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Random;

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
    //private StringBuilder log

    /**
     * Constructor
     * takes in a value that represents the clients account value
     */
    public TransactionClient(String propertiesFile){
        // load properties
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(propertiesFile));
        } catch(IOException e){
            System.out.println(e);
        }
        
        // get host & port
        this.port = Integer.valueOf(prop.getProperty("port"));
        String hostInt = prop.getProperty("host");
        try{
            InetAddress host = InetAddress.getByName(hostInt);                
        } catch(UnknownHostException e){
            System.out.println(e);
        }
        
        // get the number of accounts and transactions
        this.numberAccounts = Integer.valueOf(prop.getProperty("numAccounts"));
        this.numberTransactions = Integer.valueOf(prop.getProperty
            ("numTransactions"));
        this.initialBalance = Integer.valueOf(prop.getProperty
            ("initialBalance"));
    }

    /**
     * The bulk of the withdraws and deposits for multiple accounts and 
     * transactions
     */
    public void run(){
         for(int i=0; i<numberTransactions; i++){
        new Thread(){
          public void run(){
            TransactionServerProxy transaction = new 
                TransactionServerProxy(host,port);
            
            int transID = transaction.openTransaction();
            System.out.println("Transaction " + transID + " has started.");
            
            // determining transferring of funds between accounts
            Random rand = new Random();
            int accountMovingFrom = rand.nextInt(numberAccounts);
            int accountMovingTo = rand.nextInt(numberAccounts);
            int amountToMove = rand.nextInt(initialBalance);
            
            System.out.println("Transaction " + transID + " going from account "
                    + accountMovingFrom + " to account " + accountMovingTo 
                    + ".");

            // ------- Begin Transactions
            // withdraw from accountMovingFrom
            int balance;
            balance = transaction.read(accountMovingFrom);
            transaction.write(accountMovingFrom, balance-amountToMove);
            
            // deposit to accountMovingTo
            balance = transaction.read(accountMovingTo);
            transaction.write(accountMovingTo, balance+amountToMove);
            
            // close transaction
            transaction.closeTransaction();
            System.out.println("Transaction " + transID + " has finished.");
          }
        }.start();
      }
    }

}
