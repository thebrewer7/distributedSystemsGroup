package transactionalserver.transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import transactionalserver.MessageType;
import transactionalserver.account.Account;

/**
 * The TransactionManager handles all of the transactions
 */
public class TransactionManager {
    private ArrayList<Transaction> transactionList;
    private int numTransactions = 0;
    
    /**
     * Constructor
     * 
     */
    public TransactionManager(){
        // initialize the arraylist
        transactionList = new ArrayList<Transaction>();
    }
    
    /**
     * Returns the list of transactions 
     * 
     * @return the list of transactions
     */
    public ArrayList<Transaction> getTransactions(){
        return transactionList;
    }
    
    public void runTransactions(Socket client){
        (new TransactionManagerThread(client)).run();
    }
    
    class TransactionManagerThread implements Runnable{
         // client variables
        private Socket client;
        private ObjectInputStream fromClient;
        private ObjectOutputStream toClient;

        // Transaction Related Variables
        private Transaction transaction = null;
        private Account account = null;
        private int currentBalance = 0;
        private boolean isRunning = true;
    
        /**
        * Constructor
        * @param client the client the server connected to
        */
       public TransactionManagerThread(Socket client){
           this.client = client;
       }

       public void run(){
           try{
               //connect to input and output streams
               fromClient = new ObjectInputStream(client.getInputStream());
               toClient = new ObjectOutputStream(client.getOutputStream());

               //TODO: wait for messages from the client
               boolean isRunning = true;
               while(isRunning){
                   try{
                       Message message = fromClient.readObject();
                       
                       switch((MessageType) message.getType()){
                           case OPEN_TRANSACTION:
                               openTransaction();
                               break;
                               
                           case CLOSE_TRANSACTION:
                               closeTransaction();
                               break;
                               
                           case READ_REQUEST:
                               readRequest(message);
                               break;
                           
                           case WRITE_REQUEST:
                               writeRequest(message);
                               break;
                           
                           default:
                               System.out.println("Could not interpret message "
                                       + "type");
                       }
                       
                   } catch(IOException e){
                       // do nothing
                   } catch(ClassNotFoundException e){
                       System.out.println(e);
                   }
               }
               
           }catch(IOException e){
               System.out.println(e);
           }


       }
       
       // ------------- Handling Messages
       private void openTransaction(){
           //synchronized(transaction)
           
           // create new transaction
           transaction = new Transaction(numTransactions++);
           transactionList.add(transaction);
           
           //send the new transaction's id to the client
           try{
               toClient.writeObject(transaction.getID());
           } catch(IOException e){
               System.out.println(e);
           }
           
           //TODO: write to log
       }
       
       private void closeTransaction(){
           // release all locks that the transaction ahs           
           TransactionServer.lockManager.unlock(transaction);
           
           // remove transaction from transaction list
           for(int i=0; i<transactionList.size(); i++){
               if(transactionList.get(i).getID() == transaction.getID()){
                   transactionList.remove(i);
                   break;
               }
           }

           // close input/output streams
           try{
               toClient.close();
               fromClient.close();
           } catch(IOException e){
               System.out.println(e);
           }
           
           //TODO: print transactions log
           
           //TODO: "keepGoing" == false
       }
       
       private void readRequest(Message message){
           //get account to read from
           int accountID = ((int[]) message.getContent())[0];
           account =  TransactionServer.accountManager.getAccount(accountID);
           
           /**
            * TODO:
            * transaction.log(TRANSactionmanagerworker.run READREQUEST FOR ACCOUNTNUMBER)
            */
           
           currentBalance = TransactionServer.accountManager
                   .read(currentAccount, transaction);
           
           try{
               toClient.writeObject(currentBalance);
               /**
                * TODO:
                * transaction.log(transactionmanagerworker READREQUEST accountnumner balance$
                */
           } catch(IOException e){
               System.out.println(e);
           }
       }
       
       private void writeRequest(Message message){
           /**
            * int[0] = account id
            * int[1] = new balance
            */
           int[] content = (int[]) message.getContent();
           int accountID = content[0];
           account = TransactionServer.accountManager.getAccount(accountID);
           currentBalance = content[1];
           
           try{
               toClient.writeObject(currentBalance);
           } catch(IOException e){
               System.out.println(e);
           }
           
           /**
            *  TODO: 
            * transaction.log(transactionmanagerworker.run WRITEREQUEST account# new balance $
            */
           
           
       }
    }
}
