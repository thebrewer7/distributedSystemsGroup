//package transactionalserver.server;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import transactionalserver.account.Account;
//import transactionalserver.transaction.Transaction;
//
///**
// *  This is the server's counterpart to the client's TransactionServerProxy. 
// *  This object translates the low-level network messages to high-level API 
// *  calls.
// * 
// *  The high-level calls will be sent to the Account Manager which is the 
// *  server's counterpart of a client.
// */
//public class TransactionManagerThread implements Runnable{
//    // client variables
//    private Socket client;
//    private ObjectInputStream fromClient;
//    private ObjectOutputStream toClient;
//    
//    // Transaction Related Variables
//    private Transaction transaction = null;
//    private Account account = null;
//    private int currentBalance = 0;
//    private boolean isRunning = true;
//    
//    /**
//     * Constructor
//     * @param client the client the server connected to
//     */
//    public TransactionManagerThread(Socket client){
//        this.client = client;
//    }
//    
//    public void run(){
//        try{
//            //connect to input and output streams
//            fromClient = new ObjectInputStream(client.getInputStream());
//            toClient = new ObjectOutputStream(client.getOutputStream());
//            
//            //TODO: wait for messages from the client
//            
//            
//        }catch(IOException e){
//            System.out.println(e);
//        }
//        
//        
//    }
//}
//
