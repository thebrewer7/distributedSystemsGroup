package transactionalserver.transaction;

import java.util.ArrayList;
import java.util.Objects;
import transactionalserver.lock.Lock;

/**
 *  Represents a transaction
 */
public class Transaction {
    // private variables
    private int id;
    private ArrayList<Lock> locksList = null;
    
    
    /**
     * Constructor
     */
    public Transaction(int id){
        this.id = id;
    }
    
    //----------- Transaction Methods
    
    /**
     * This is a lock that the Transaction has so that other transactions cannot
     * write/read on the object the lock belongs to
     * 
     * @param lock the lock to add to the list of locks the transaction has
     */
    public void addLock(Lock lock){
        locksList.add(lock);
    }
    
    /**
     * This is a lock that the Transaction is releasing so that other 
     * transactions can use it.
     * 
     * @param lock the lock to remove from the list of locks
     */
    public void removeLock(Lock lock){
        locksList.remove(lock);
    }
    
    
    //----------- getters
    
    /**
     * returns it's ID
     * @return the Transaction's ID which is a UUID object
     */
    public int getID(){
        return id;
    }
    
    /**
     * returns the locks the Transactions has
     * 
     * @return the list of locks the Transaction has
     */
    public ArrayList<Lock> getLocks(){
        return locksList;
    }
    
    //------------- Overiding Object Methods
    /**
     * Tells if the given object is equal with this object
     * 
     * @param obj the object to see if it's equal with this object
     * @return true if they're equal, false if they're not equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}