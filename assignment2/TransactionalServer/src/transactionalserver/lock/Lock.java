package transactionalserver.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import transactionalserver.account.Account;
import transactionalserver.transaction.Transaction;

/**
 * Represents a lock for an Account object. If it's locked, then the Account
 * object cannot be accessed
 */
public class Lock {
    Account account;
    LockType currentLockType;
    ArrayList<Transaction> lockHolders;
    HashMap<Transaction, Object[]> lockRequestors;

    /**
     *  Constructor
     */
    public Lock(Account account){
      this.lockHolders = new ArrayList();
      this.lockRequestors = new HashMap();
      this.account = account;
      this.currentLockType = LockType.EMPTY_LOCK;
    }

    /**
    * checks if there is conflict, if not it creates the lock
    */
    public void acquire(Transaction transaction, LockType newLockType){
//      transaction.lock(Lock acquire);
      while(isConflict(transaction, newLockType)){
              //transaction.log("lock acquire");
//        lockRequestors.put(transaction, newLockType);
          try {
            this.wait();
          } catch (InterruptedException e) {
              System.out.println(e);
          }
        lockRequestors.remove(transaction);
        //transaction.log("lock acquire");
      }
      if(lockHolders.isEmpty()){
        lockHolders.add(transaction);
        currentLockType = newLockType;
        transaction.addLock(this);
        //transaction.log("lock acquire");
      }
      else if(!lockHolders.contains(transaction)){
        Iterator<Transaction> lockIterator = lockHolders.iterator();
        Transaction otherTransaction;
        //StringBuilder logString = new StringBuilder("lock acquire");
        while(lockIterator.hasNext()){
          otherTransaction = lockIterator.next();
        }
      }
    }

    /**
    * checks if there are lock holders, if there are,
    * set lock type to be an empty lock and free
    */
    public void release(Transaction transaction){
      lockHolders.remove(transaction);
      if(!lockHolders.isEmpty()){
        currentLockType = LockType.EMPTY_LOCK;
        if(lockRequestors.isEmpty()){
          //TODO:
        }
      }
      notifyAll();
    }

    /**
    * checks if there are lock holders, if there are,
    * set lock type to be an empty lock and free
    */
    public Boolean isConflict(Transaction transaction, LockType newLockType){
      if(lockHolders.isEmpty()){
        //transaction.log("Is conflict on current lock");
        return false;
      }
      else if(lockHolders.size() == 1 && lockHolders.contains(transaction)){
        //TODO:
        return true;
      }
      
      return false;
    }
}
