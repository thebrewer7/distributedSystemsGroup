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

    /**
     * Constructor
     * created with a host and port number
     */
    public TransactionServerProxy(String host, int port){
      this.host = host;
      this.port = port;
    }

    /**
    * opens a transaction and creates a socket stream
    * opens a transactionID from the input stream
    */
    public int openTransaction(){
      Message openTransMessage = new Message("OPEN_TRANSACTION", null)
      dbConnection = new Socket(host, port);
      writeToNet = new ObjectOutputStream(dbConnection.getOutputStream());
      readFromNet = new ObjectInputStream(dbConnection.getInputStream());
      transID = (int) readFromNet.readObject();

      return transID;
    }

    /**
    * closes a transaction
    */
    public void closeTransaction(){
      Message clonseTransMessage = new Message("CLOSE_TRANSACTION", null);
      //TODO:
    }

    /**
    * reads the balance for a given account number and return that balance
    */
    public int read(int accountNumber){
      Message readMessage = new Message("READ_REQUEST", new int(accountNumber));
      int balance = null;
      writeToNet.writeObject(readMessage);
      balance = (int) readFromNet.readObject();

      return balance;
    }

    /**
    * translates the clients withdraw into what is happening with it
    * behind the scenes
    */
    public void write(int accountNumber, int amount){
      Object[] content = new Object[]{accountNumber, amount};
      Message writeMessage = new Message("WRITE_REQUEST", new int(accountNumber), amount);
      int balance += amount;
      writeToNet.writeObject(writeMessage);
      balance += (int) readFromNet.readObject();
    }
}
