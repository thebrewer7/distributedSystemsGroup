package transactionalserver.lock;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import transactionalserver.account.Account;
import transactionalserver.transaction.Transaction;
        
/**
 *  The LockManager handles and initializes all of the Lock objects
 * 
 *  It will acquire and release locks for the AccountManager.
 *  Unlocking will be done in the TransactionsMaangerWorker.
 */
public class LockManager{
    private boolean applyLocking;
    //Some kind of data structure to hold locks info
    private HashMap<Account, Lock> locks;
    

    public LockManager(String clientProperties){
        // get configurations from client properties file
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(clientProperties));
        } catch(IOException e){
            System.out.println(e);
        }
        
        applyLocking =  Boolean.valueOf(prop.getProperty("applyLocking"));
        //initialize structure holding locks
        locks = new HashMap<>();   
    }
    /*
    Sets a lock on a transaction
    */
    public void lock(Transaction transaction, Account account, 
            LockType lockType){
        if (!applyLocking){
            return;
        }
        Iterator<Lock> lockIterator = transaction.getLocks().listIterator();
//        currentLock.acquire(transaction, lockType);
    }
    /*
    Unlocks a lock on a transaction
    */
    public void unLock(Transaction transaction){
        if (!applyLocking){
            return;
        }
        Iterator<Lock> lockIterator = transaction.getLocks().listIterator();
        while (lockIterator.hasNext()){
            Lock currentLock = lockIterator.next();
            currentLock.release(transaction);
        }
    }
}
