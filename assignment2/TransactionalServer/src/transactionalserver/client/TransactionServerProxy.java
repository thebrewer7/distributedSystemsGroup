package transactionalserver.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import transactionalserver.Message;
import transactionalserver.MessageType;

/**
 *  The TransactionServerProxy communicates with the server side for a Client
 *  object.
 *
 *  It's the one handling the low-level networking operations for the client so
 *  the client is not exposed to this. It's essentially translating the
 *  high-level API calls to low-level network messages
 */
public class TransactionServerProxy {
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    /**
     * Constructor
     * created with a host and port number
     */
    public TransactionServerProxy(String host, int port){
        // -------- Connect to server
        // get host
        InetAddress inetHost = null;
         try{
             inetHost = InetAddress.getByName(host);
         } catch(UnknownHostException e){
             System.out.println(e);
         }
        
         // connect to server's socket & input/output streams
         Socket clientSocket = null;
         try{
             clientSocket = new Socket(inetHost, port);
             toServer = new ObjectOutputStream(clientSocket.getOutputStream());
             fromServer = new ObjectInputStream(clientSocket.getInputStream());
         } catch(IOException e){
             System.out.println(e);
         }
      
    }

    /**
    * opens a transaction and gets a transactionID from the input stream
    */
        public int openTransaction(){
      Message openTransMessage = new Message(MessageType.OPEN_TRANSACTION);
      
      // connect to server
      int transID = -1;
      try{
          //tell server it wants to open a transactions
          toServer.writeObject(openTransMessage);
          
          // get Transaction ID
          transID = (int) fromServer.readObject();
      } catch(IOException | ClassNotFoundException e){
          System.out.println(e);
      }
      
      return transID;
    }

    /**
    * closes a transaction
    */
    public void closeTransaction(){
        Message message = new Message(MessageType.CLOSE_TRANSACTION);
        
        // connect to server
      int transID = -1;
      try{          
          //tell server it wants to open a transactions
          toServer.writeObject(message);
      } catch(IOException e){
          System.out.println(e);
      }
    }

    /**
    * reads the balance for a given account number and return that balance
    */
    public int read(int accountNumber) {
        Message message = new Message(MessageType.READ_REQUEST, accountNumber);
        
        int balance = -1;
        try{
            // send account ID to server
            toServer.writeObject(message);
            
            // get balance of the account
            balance = (int) fromServer.readObject();
        } catch(IOException e){
            System.out.println(e);
        } catch(ClassNotFoundException e){
          System.out.println(e);
        }
      
      return balance;
    }

    /**
    * translates the clients withdraw into what is happening with it
    * behind the scenes
    */
    public void write(int accountNumber, int amount){        
      int[] content = new int[]{accountNumber, amount};
      Message message = new Message(MessageType.WRITE_REQUEST, 
        accountNumber, amount);
//      int balance += amount;

        try{
            // send the balanc it wants to do
            toServer.writeObject(message);
        } catch(IOException e){
            System.out.println(e);
        }
    }
}