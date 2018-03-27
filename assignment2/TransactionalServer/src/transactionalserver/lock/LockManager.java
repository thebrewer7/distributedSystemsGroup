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
public class LockManager{
    
    //Some kind of data structure to hold locks info
    private HashMap<Account, Lock> locks;

    public LockManager(String clientProperties){
        properties clientProperties = new propertyhandler(clientProperties);
        applyLocking = clientProperties.getProperty(applyLocking);
        //initialize structure holding locks
        locks = new HashMap<>();   
    }
    /*
    Sets a lock on a transaction
    */
    void lock(Transaction transaction, LockType lockType){
        if (!applyLocking){
            return;
        }
        Iterator<Lock> lockIterator = transaction.getLocks().listIterator();
        Lock currentLock = currentLock.acquire(transaction);
    }
    /* 
    Unlocks a lock on a transaction
    */
    void unLock(Transaction transaction){
        if (!applyLocking){
            return;
        }
        Iterator<Lock> lockIterator = transaction.getLocks().listIterator();
        Lock currentLock = currentLock.realease(transaction);
    }
