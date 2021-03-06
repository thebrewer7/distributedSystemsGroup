package transactionalserver.transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import transactionalserver.Message;
import transactionalserver.MessageType;
import transactionalserver.account.Account;
import transactionalserver.account.AccountManager;
import transactionalserver.lock.LockManager;
import transactionalserver.lock.LockType;

/**
 * The TransactionManager handles all of the transactions
 */
public class TransactionManager {
    private ArrayList<Transaction> transactionList;
    private int numTransactions = 0;
    
    private static AccountManager accountManager;
    private static LockManager lockManager;
    
    /**
     * Constructor
     * 
     */
    public TransactionManager(AccountManager accountManager, 
            LockManager lockManager){
        // initialize the arraylist
        transactionList = new ArrayList<Transaction>();
        this.accountManager = accountManager;
        this.lockManager = lockManager;
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
        new Thread(new TransactionManagerThread(client)).start();
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
                       Message message = (Message) fromClient.readObject();
                       
                       switch(message.getType()){
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
           lockManager.unLock(transaction);
           
           // remove transaction from transaction list
           for(int i=0; i<transactionList.size(); i++){
               if(transaction == null){
                   return;
               }
               else if(transactionList.get(i) == null){
                   continue;
               }

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
           account =  accountManager.getAccount(accountID);
           
           // acquire lock to read
           lockManager.lock(account, transaction, LockType.READ);
           
           /**
            * TODO:
            * transaction.log(TRANSactionmanagerworker.run READREQUEST FOR ACCOUNTNUMBER)
            */
           
           currentBalance = accountManager.read(account, transaction);
           
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
           // get the account to change the balance & the new balance
           int[] content = (int[]) message.getContent();
           int accountID = content[0];
           account = accountManager.getAccount(accountID);
           currentBalance = content[1];
           
           // change the balance on the account object
           lockManager.lock(account, transaction, LockType.WRITE);
           account.setBalance(currentBalance);
           
           
           /**
            *  TODO: 
            * transaction.log(transactionmanagerworker.run WRITEREQUEST account# new balance $
            */
           
           
       }
    }
}
