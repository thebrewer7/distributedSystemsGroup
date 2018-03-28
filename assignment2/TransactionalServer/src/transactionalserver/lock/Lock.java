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
    HashMap<Transaction, Object[]> lockRequestors;  //transactions requesting a 
                                                    //this lock

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
          currentLockType = LockType.EMPTY_LOCK;
        }
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
      else if(currentLockType == LockType.READ && newLockType == 
              LockType.READ){
          return false;
      }
      // if the transactions wants to promote the lock and it's the only hodler
      else if(currentLockType == LockType.READ && newLockType == 
              LockType.WRITE && lockHolders.size() == 1  && lockHolders
              .contains(transaction)){
          return true;
      }
      /**
       * All other cases are conflicts:
       *    * currentLockType  == READ && newLockType == WRITE 
       *        & lockHolder size is greater than 1
       *    * currentLockType == WRITE (can't demote & can't share write locks)
       */
      else{
          return false;
      }
      /**
       * TODO: check other cases for conflicts
       * 
       * if lockType == write, it's a conflict
       * if newLockType == write, it's a conflict
       * if lockType ==  read && newLockType == read, no conflict
       */
    }
}
