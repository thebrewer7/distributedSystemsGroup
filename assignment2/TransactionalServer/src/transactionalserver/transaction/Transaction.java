package transactionalserver.transaction;

import java.util.Objects;
import java.util.UUID;

/**
 *  Represents a transaction
 */
public class Transaction {
    private UUID id;
    /**
     * Constructor
     */
    public Transaction(){
        id = UUID.randomUUID();
    }
    
    //----------- Transaction Methods
       
    /**
     * Reads an object which requires getting a lock for the object to read 
     * from
     */
    public void read(){
        //TODO: get read lock
        //TODO: read from account
    }
    
    /**
     * Writes to an object which requires getting a lock for the object to 
     * write on
     */
    public void write(){
        //TODO: get write lock
        //TODO: write to account
    }
    
    /**
     * Closes the transactions and releases all the locks it has
     */
    public void close(){
        //TODO: release keys
    }
    
    //----------- getters
    
    /**
     * returns it's ID
     * @return the Transaction's ID which is a UUID object
     */
    public UUID getID(){
        return id;
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