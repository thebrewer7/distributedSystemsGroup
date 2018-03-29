package transactionalserver.lock;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import transactionalserver.account.Account;
import transactionalserver.lock.LockType;
import transactionalserver.transaction.Transaction;
       
/**
 *  The LockManager handles and initializes all of the Lock objects
 * 
 *  It will acquire and release locks for the AccountManager.
 *  Unlocking will be done in the TransactionsMaangerWorker.
 */
public class LockManager implements LockType{
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
            //find the lock associated with the specific account
            found = locks.get(account);
            
            if(found == null){
               found = new Lock(account);
               locks.put(account, found);
            } 
        }
        found.acquire(transaction, lockType);
    }
}
