package appserver.server;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Server {

    // Singleton objects - there is only one of them. For simplicity, this is not enforced though ...
    static SatelliteManager satelliteManager = null;
    static LoadManager loadManager = null;

    // Network objects
    static ServerSocket serverSocket = null;
    private int port = 0;
    private InetAddress host = null;
    
    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();

    public Server(String serverPropertiesFile) {

        // create satellite manager and load manager
        // ...
        satelliteManager = new SatelliteManager();
        loadManager = new LoadManager();
        
        // read server properties and create server socket
        // ...
        Properties props = new Properties();
        try{     
            props.load(new FileInputStream(serverPropertiesFile));
            
            //get host & port number
            port = Integer.valueOf(props.getProperty("PORT"));
            host = InetAddress.getByName(props.getProperty("HOST"));
            
            //create server socket
            serverSocket = new ServerSocket(port);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void run() {
    // serve clients in server loop ...
    // when a request comes in, a ServerThread object is spawned
    
        try{
            // loop that is always listening for new clients
            // if a clien connects it sends the client to ServerThread
            while(true){
                System.out.println("Waiting for clients");
                
                //connect to client
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a new client");
                new Thread(new ServerThread(socket)).start();
            }
        } catch(IOException e){
            
        }
    }

    // objects of this helper class communicate with satellites or clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;
        
        private ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            // set up object streams and read message
            // ...
            try{
                readFromNet = new ObjectInputStream(client.getInputStream());
                writeToNet = new ObjectOutputStream(client.getOutputStream());
                message = (Message)(readFromNet.readObject());
            }catch(IOException e){
                System.out.println(e);
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }
            
            // process message
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    // read satellite info
                    // ...
                    satelliteInfo = (ConnectivityInfo)message.getContent();
                    
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        // ...
                        satelliteManager.registerSatellite(satelliteInfo);
                    }

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        // ...
                        loadManager.satelliteAdded(satelliteInfo.getName());
                    }
                    break;

                case JOB_REQUEST:
                    System.err.println("\n[ServerThread.run] Received job request");

                    String satelliteName = null;
                    synchronized (Server.loadManager) {
                    try {
                        // get next satellite from load manager
                        // ...
                        satelliteName = loadManager.nextSatellite();
                        
                        // get connectivity info for next satellite from satellite manager
                        // ...
                        satelliteManager.getSatelliteForName(satelliteName);
                    } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    Socket satellite = null;
                    // connect to satellite - use 'satellite name'
                    // ...

                    // open object streams,
                 
                    
                    
                    // forward message (as is) to satellite,
                    
                    
                    // receive result from satellite and
                    
                    
                    // write result back to client
                    
                    
                    // ...

                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
        }
        
        public void readSatelliteInfo(Message message){
            //get the satellites name
            String satelliteName = satelliteManager.getSatelliteForName(string((message)));
   
        }

        private void addSatellite() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    // main()
    public static void main(String[] args) {
        // start the application server
        Server server = null;
        if(args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server("../../config/Server.properties");
        }
        server.run();
    }
}
