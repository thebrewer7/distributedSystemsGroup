package transactionalserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the server that handles transactions from its clients
 */
public class TransactionalServer {

    public TransactionalServer(){
        //TODO: get pre-defined configurations from a config file
    }
    
    public static void main(String[] args){
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
                new Thread(new TransactionManagerThread(client)).start();
            }
        }catch(IOException e){
            System.out.println(e);
        }
        
    }
}
