package transactionalserver.transaction;

import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import transactionalserver.server.TransactionManagerThread;

/**
 * The TransactionManager handles all of the transactions
 */
public class TransactionManager {
    private ArrayList<Transaction> transactionList;
    
    /**
     * Constructor
     * 
     */
    public TransactionManager(){
        // initialize the arraylist
        transactionList = new ArrayList<Transaction>();
    }
    
    /**
     * Returns the list of transactions 
     * 
     * @return the list of transactions
     */
    public ArrayList<Transaction> getTransactions(){
        return transactionList;
    }
    
    public void runTransactions(Socket client){
        (new TransactionManagerThread(client)).run();
    }
}
