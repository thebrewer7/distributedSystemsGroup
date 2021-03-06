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
    private static AccountManager accountManager;
    private static TransactionManager transactionManager;
    private static LockManager lockManager;
    private boolean isRunning = true;
    
    public TransactionalServer(String propertiesFilePath){
        // get configurations from properties file
        Properties props = new Properties();
        try{
            props.load(new FileInputStream(propertiesFilePath));
        } catch(IOException e){
            System.out.println(e);
        }

        lockManager = new LockManager("src/transactionalserver/server/clientproperties.properties");
        accountManager = new AccountManager(100, 100, lockManager);
        transactionManager = new TransactionManager(accountManager,
                lockManager);
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
        TransactionalServer transServer = new TransactionalServer("src/"
                + "transactionalserver/server/clientproperties.properties");
        transServer.run();
        
    }
}
