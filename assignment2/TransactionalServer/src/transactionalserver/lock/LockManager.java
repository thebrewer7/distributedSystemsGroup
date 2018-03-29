package transactionalserver.lock;
import java.util.HashMap;
import java.util.Iterator;

import transactionalserver.account.Account;
        
/**
 *  The LockManager handles and initializes all of the Lock objects
 * 
 *  It will acquire and release locks for the AccountManager.
 *  Unlocking will be done in the TransactionsMaangerWorker.
 */
public class LockManager implements LockType{
    
    //Some kind of data structure to hold locks info
    private HashMap<Account, Lock> locks;

    public LockManager(String clientProperties){
        //get the apply locking property from the client properties file
        properties clientProperties = new propertyhandler(clientProperties);
        applyLocking = clientProperties.getProperty(applyLocking);
        //initialize structure holding locks
        locks = new HashMap<Account, Lock>();   
    }
    /*
    Sets a lock on an account
    */
    void lock(Account account, Transaction transaction, LockType lockType, applyLocking){
        
        Lock found;
        
        // prevents race conditions
        synchronized(this){
            if (!applyLocking){
            return;
            }
            //find the lock associated with the specific account
            found = locks.get(account);
            
            if(found == null){
               found = new Lock(lock);
               locks.put(account, found);
            } 
        }
        found.acquire(transaction, lockType);
    }
    /* 
    Unlocks a lock on an account
    */
    void unLock(Transaction transaction){
        if (!applyLocking){
            return;
        }
        Iterator<Lock> lockIterator = transaction.getLocks().listIterator();
        while (lockIterator.hasNext()){
            Lock currentLock = transaction.getLocks().next();
            currentLock.realease(transaction);
        }
    }
}
