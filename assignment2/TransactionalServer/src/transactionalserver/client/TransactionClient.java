package transactionalserver.client;

/**
 *  This represents a Client that will communicate with the
 *  TransactionServerProxy which will in turn communicate with the Server side.
 *
 *  The client side will be specifying actions to occur with bank accounts
 */
public class TransactionClient {
    private Bool hasALock;
    private int value;

    /**
     * Constructor
     * takes in a value that represents the clients account value
     */
    public TransactionClient(int value){
      this.value = value;
      hasALock = false;
    }

    /**
    * returns true is the client currently has a lock
    * returns false if the client doesnt have a lock
    */
    public Bool hasLock(){
      if(hasALock == true){
        return true;
      }
      else{
        return false;
      }
    }

    /**
    * the clients call to withdraw from their account
    * this will simply remove money from their account on their end
    */
    public void withdraw(int withdrawAmount){
      value -= withdrawAmount;
    }

    /**
    * the clients call to insert to their account
    * this will simply insert money to their account on their end
    */
    public void insert(int insertAmount){
      value += insertAmount;
    }
}
