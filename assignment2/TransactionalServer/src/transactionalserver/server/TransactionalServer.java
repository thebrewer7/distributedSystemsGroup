package transactionalserver.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import transactionalserver.account.AccountManager;
import transactionalserver.lock.LockManager;
import transactionalserver.transaction.TransactionManager;

/**
 * This is the server that handles transactions from its clients
 */
public class TransactionalServer implements Runnable{
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private LockManager lockManager;
    private boolean isRunning = true;
    
    public TransactionalServer(){
        accountManager = new AccountManager(100, 100);
        transactionManager = new TransactionManager();
        lockManager = new LockManager();
    }
    
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(9090);
        
            //create the "always-listening" loop for new clients
            boolean isRunning = true;
            while(isRunning){
                System.out.println("Waiting for clients");

                //accept new client
                Socket client = serverSocket.accept();  
                System.out.println("Connected to a new client\n\n");
                
                //send client to a thread to communicate with server
                transactionManager.runTransactions(client);
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public static void main(String[] args){
        TransactionalServer transServer = new TransactionalServer();
        
    }
}
