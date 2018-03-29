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
public class Lock implements LockType {
    Account account;
    LockType currentLockType;
    ArrayList<Transaction> lockHolders;

    /**
     *  Constructor
     */
    public Lock(Account account){
      this.lockHolders = new ArrayList();
      this.account = account;
      this.currentLockType = LockType.EMPTY_LOCK;
    }

    /**
    * checks if there is conflict, if not it creates the lock
    */
    public synchronized void acquire(Transaction transaction, LockType newLockType){
      while(isConflict(transaction, newLockType)){
          try {
            this.wait();
          } catch (InterruptedException e) {
              System.out.println(e);
          }
      }
      if(lockHolders.isEmpty()){
        lockHolders.add(transaction);
        currentLockType = newLockType;
        transaction.addLock(this);
      }
      else if(!lockHolders.contains(transaction)){
          lockHolders.add(transaction)
      }
      else if(!lockHolders.contains(transaction) && currentLockType == LockType.READ && newLockType == LockType.WRITE){
          currentLockType = newLockType;
          //TODO: Promote lock
      }
    }

    /**
    * checks if there are lock holders, if there are,
    * set lock type to be an empty lock and free
    */
    public synchronized void release(Transaction transaction){
      lockHolders.remove(transaction);
      if(lockRequestors.isEmpty()){
        currentLockType = LockType.EMPTY_LOCK;
      }
      notifyAll();
    }

    /**
    *   Checks if the transaction trying to acquire the specific lock type is 
    *   conflict free
    */
    public Boolean isConflict(Transaction transaction, LockType newLockType){
      if(lockHolders.isEmpty()){
        return false;
      }
      else if(currentLockType == LockType.READ && newLockType == LockType.READ){
          return false;
      }
      else if(lockHolders.size() == 1){
          return false;   
      }
      /**
       * otherwise its a conflict
       */
      else{
          return true;
      }
    }
}
