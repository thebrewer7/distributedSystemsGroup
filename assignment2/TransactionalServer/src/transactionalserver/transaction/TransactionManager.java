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
     * 
     * @param numTransactions   the number of transactions the manager needs to
     *                          keep track of & initialize
     */
    public TransactionManager(int numTransactions){
        // initialize the arraylist
        transactionList = new ArrayList<Transaction>();
        
        // initialize each transactions
        for(int i=0; i<numTransactions; i++){
            transactionList.add(new Transaction(i));
        }
    }
    
    /**
     * Closes a transaction object
     * 
     * @param transactionID the ID of the transaction
     */
    public void closeTransaction(int transactionID){
        // look for Transaction object in the Transaction list & remove it
        for(int i=0; i<transactionList.size(); i++){
            Transaction currTransaction = transactionList.get(i);  
            
            // check if it's the transactions it's looking for
            if(currTransaction.getID() == transactionID){
                //close transaction so it will release its locks
                currTransaction.close();
                //remove from list because it's closed
                transactionList.remove(i);
                break;
            }
        }
    }
}
