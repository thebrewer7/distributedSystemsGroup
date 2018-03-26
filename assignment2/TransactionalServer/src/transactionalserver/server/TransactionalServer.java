package transactionalserver.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import transactionalserver.account.AccountManager;
import transactionalserver.transaction.TransactionManager;

/**
 * This is the server that handles transactions from its clients
 */
public class TransactionalServer {
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    
    public TransactionalServer(){
        // ---------- Get Configurations
        
        /**
         * Get config file to know how many transactions & how many bank 
         * accounts to have & what the initial balance of all the bank accounts
         */
        Properties prop = new Properties();
        InputStream input = null;
        
        // initialize variables for need info from config file
        int numTransactions = 0;
        int initialBalance = 0;
        int numAccounts = 0;
        
        try{
            //load properites file
            input = new FileInputStream("src/transactionalserver/server/config"
                    + ".properties");
            prop.load(input);
            
            // get the number of transactions and accounts
            numTransactions = Integer.valueOf(prop
                    .getProperty("numTransactions"));
            numAccounts = Integer.valueOf(prop.getProperty("numAccounts"));
            initialBalance = Integer.valueOf(prop
                    .getProperty("initialBalance"));
            
            //close input stream
            input.close();
            
        } catch(IOException e){
            System.out.println(e);
        }
        
        // ---------- Initialize Accounts & Transactions
        // initialize accounts
        accountManager = new AccountManager(numAccounts, initialBalance);
        
        //initialize transactions
        transactionManager = new TransactionManager(numTransactions);
        
    }
    
    public static void main(String[] args){
        TransactionalServer transServer = new TransactionalServer();
        try{
            ServerSocket serverSocket = new ServerSocket(9090);
        
            //create the "always-listening" loop for new clients
            boolean isRunning = true;
            while(isRunning){
                System.out.println("Waiting for clients");

                //accept new client
                Socket client = serverSocket.accept();  
                System.out.println("Connected to a new client\n\n");
                
                //send client to a thread to communicate with server
                new Thread(new TransactionManagerThread(client)).start();
            }
        }catch(IOException e){
            System.out.println(e);
        }
        
    }
}
