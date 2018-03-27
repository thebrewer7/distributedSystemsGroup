package transactionalserver.lock;

/**
 * Represents a lock for an Account object. If it's locked, then the Account
 * object cannot be accessed
 */
public class Lock {
    Account account;
    int currentLockType;
    ArrayList<Transaction> lockHolders;
    HashMap<Transaction, Object[]> lockRequestors;

    /**
     *  Constructor
     */
    public Lock(Account account){
      this.lockHolders = new ArrayList();
      this.lockRequestors = new HashMap();
      this.account = account;
      this.currentLockType = "EMPTY_LOCK";
    }

    /**
    * checks if there is conflict, if not it creates the lock
    */
    public void acquire(Transaction transaction, int newLockType){
      transaction.lock(Lock acquire);
      while(isConflict(transaction, newLockType)){
        //transaction.log("lock acquire");
        addLockRequestor(transaction, newLockType);
        wait();
        removeLockRequestor(transaction);
        //transaction.log("lock acquire");
      }
      if(lockHolders.isEmpty()){
        lockHolders.add(transaction);
        currentLockType = newLockType;
        transaction.addLock(this);
        //transaction.log("lock acquire");
      }
      elseif(!lockHodlers.contains(transaction)){
        Iterator<transaction> lockIterator = lockHolders.iterator();
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
        currentLockType = "EMPTY_LOCK";
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
    public Boolean isConflict(Transaction transaction, int newLockType)){
      if(lockHolders.isEmpty()){
        //transaction.log("Is conflict on current lock");
        return false;
      }
      else if(lockHolders.size() == 1 && lockHolders.contains(transaction)){
        //TODO:
        return true;
      }
    }
}
