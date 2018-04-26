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
        satelliteManager = new SatelliteManager();
        loadManager = new LoadManager();
        
        // read server properties and create server socket
        Properties props = new Properties();
        try{     
            props.load(new FileInputStream(serverPropertiesFile));
            
            //get host & port number
            port = Integer.valueOf(props.getProperty("PORT"));
            host = InetAddress.getByName(props.getProperty("HOST"));
            
            //create server socket
            serverSocket = new ServerSocket(port);
        }catch(IOException e){
            System.out.println("SERVER: error in retrieving properties");
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
            System.out.println("Error in acceptng new client");
            System.out.println(e);
        }
    }

    // objects of this helper class communicate with satellites or clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;
        Message message = null;
        
        private ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            //-------set up object streams and read message
            // Create object inputstream
            try{
                fromClient = new ObjectInputStream(client.getInputStream());

            } catch(IOException e){
                System.out.println("Error in creating input stream w/ client");
                System.out.println(e);
            }
            
            // Read message
            try{
                message = (Message)(fromClient.readObject());
            }catch(IOException e){
                System.out.println("Error in reading message from client");
                System.out.println(e);
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }
            
            // Create object output stream
            try{
                toClient = new ObjectOutputStream(client.getOutputStream());
            } catch(IOException e){
                System.out.println("Error in creating output stream w/ client");
                System.out.println(e);
            }
            
            // process message
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    System.out.println("\nRegistering a new satelling: ");
                    //read satellite info
                    satelliteInfo = (ConnectivityInfo)message.getContent();
                    
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        satelliteManager.registerSatellite(satelliteInfo);
                    }

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        loadManager.satelliteAdded(satelliteInfo.getName());
                    }
                    break;

                case JOB_REQUEST:
                    System.out.println("\nProcessing a new Job Request ");
                    
                    String satelliteName = null;
                    synchronized (Server.loadManager) {
                        try {
                            // get next satellite from load manager
                            satelliteName = loadManager.nextSatellite();
                            System.out.println("Using " + satelliteName + " to "
                                    + "process job");

                            // get connectivity info for next satellite from satellite manager
                            satelliteInfo = satelliteManager.getSatelliteForName(satelliteName);
                        } catch (Exception ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    Socket satellite = null;
                    {
                        try {
                            // connect to satellite - use 'satellite name'
                            satellite = new Socket(satelliteInfo.getHost(), satelliteInfo.getPort());
                            System.out.println("Connected with satellite");
                        } catch (IOException ex) {
                            System.out.println("Error in creating socket with"
                                    + " satellite");
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    //------ forward message (as is) to satellite,
                    // create object streams w/ satellite
                    ObjectOutputStream toSatellite = null;
                    ObjectInputStream fromSatellite = null;
                    try{
                        //send message
                        toSatellite = new ObjectOutputStream(satellite.getOutputStream());
                    } catch(IOException e){
                        System.out.println("Error in creating object output"
                                + " stream");
                        System.out.println(e);
                    }
                    
                    try{
                        toSatellite.writeObject(message);
                    }catch(IOException e){
                        System.out.println("Error in sending message to "
                                + "satellite");
                        System.out.println(e);
                    }
                    
                    //receive results from satellite
                    try{
                        fromSatellite = new ObjectInputStream(satellite.getInputStream());
                    } catch(IOException e){
                        System.out.println("Error in creating object input "
                                + "stream");
                        System.out.println(e);
                    }
                    
                    Integer results = null;
                    try{
                        results = (Integer) fromSatellite.readObject();
                    } catch(IOException e){
                        System.out.println("Error in getting results");
                        System.out.println(e);
                    }catch(ClassNotFoundException e){
                        System.out.println(e);
                    }
                    
                    try{
                        // write result back to client
                        toClient.writeObject(results);
                    }
                    catch(IOException e){
                        System.out.println("Error sending results to cient");
                        System.out.println(e);
                    } 
                    
                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
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
