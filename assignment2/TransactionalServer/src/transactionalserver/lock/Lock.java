package transactionalserver.lock;

import transactionalserver.client.TransactionClient;

/**
 * Represents a lock for an Account object. If it's locked, then the Account
 * object cannot be accessed
 */
public class Lock {

    /**
     *  Constructor
     */
    public Lock(){

    }

    /**
    * checks the client list to see if a lock is there or not
    * if there is a lock then it is cannot open one
    */
    public boolean acquire(TransactionClient client){
      //TODO: check if there is a lock existing
//       if(transactionList.hasLock == false){
//        client.hasLock = true;
//      }
//      else{
//        System.out.println("Lock already exists, wait.");
//      }

       return false;
    }

    /**
    * this will only be called if there is an existing lock
    * if there is a lock then the lock will be removed
    */
    public boolean release(TransactionClient client){
//      client.hasLock = false;s
      return false;
    }

}
