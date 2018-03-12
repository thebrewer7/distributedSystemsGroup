package transactionalserver.lock;

import transactionalserver.account.Account;
        
/**
 *  The LockManager handles and initializes all of the Lock objects
 * 
 *  It will acquire and release locks for the AccountManager.
 *  Unlocking will be done in the TransactionsMaangerWorker.
 */
public class LockManager {
    
    //Some kind of data structure to hold locks info
    
    public LockManager(){
        //initialize structure holding locks
        
    }
    
    /**
     * Set Locks
     * @param account being acted on and specific transactionID 
     */
    public void setLock(Account account, int transactionID){
        
    }
    
    public void unlock(Account account, int transactionID){
        
    }
    
    /**
     * Check for conflict before setting locks
     * 1. Read already performed... do not allow anyone else to write 
     *      until commit or abort
     * 2. Write already performed... do not allow anyone else to 
     *      read OR write until commit or abort
     * @param account 
     * @param transactionID
     */
    public boolean checkConflict(Account account, int transactionID){
        if( "read".equals(lockType)){
            //wait for a commit or abort
            return false;
        }
        
        if("write".equals(lockType)){
            //wait for commit or abort
            return false;
        }
        return true; 
    }
}