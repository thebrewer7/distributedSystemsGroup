package transactionalserver.client;

/**
 *  The TransactionServerProxy communicates with the server side for a Client
 *  object.
 *
 *  It's the one handling the low-level networking operations for the client so
 *  the client is not exposed to this. It's essentially translating the
 *  high-level API calls to low-level network messages
 */
public class TransactionServerProxy {
    private TransactionClient client;
    private int looseMoney;

    /**
     * Constructor
     * created with a client and sets the amount of loose money to 0
     * loose money is money that has been withdrawn but not inserted yet
     */
    public TransactionServerProxy(TransactionClient client){
      this.client = client;
      this.looseMoney = 0;
    }

    /**
    * translates the clients withdraw into what is happening with it
    * behind the scenes
    */
    public void withdraw(int withdrawAmount){
      looseMoney += withdrawAmount;
      client.withdraw(withdrawAmount);
    }

    /**
    * translates the clients insert into what is happening with it 
    * behind the scenes
    */
    public void insert(int insertAmount){
      looseMoney -= insertAmount;
      client.insert(insertAmount);
    }
}
