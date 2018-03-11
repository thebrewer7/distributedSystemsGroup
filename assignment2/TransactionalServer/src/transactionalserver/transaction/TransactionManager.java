package transactionalserver.transaction;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The TransactionManager handles all of the transactions
 */
public class TransactionManager {
    private ArrayList<Transaction> transactionList;
    
    /**
     * Constructor
     */
    public TransactionManager(){
        transactionList = new ArrayList<Transaction>();
    }
    
    /**
     * Opens a transaction object
     */
    public void openTransaction(){
        //add new transaction to the transaction list
        transactionList.add(new Transaction());
    }
    
    /**
     * Closes a transaction2 object
     */
    public void closeTransaction(UUID transactionID){
        // look for Transaction object in the Transaction list & remove it
        for(int i=0; i<transactionList.size(); i++){
            Transaction currTransaction = transactionList.get(i);            
            if(currTransaction.getID().equals(transactionID)){
                //close transaction so it will release its locks
                currTransaction.close();
                //remove from list because it's closed
                transactionList.remove(i);
                break;
            }
        }
    }
}
