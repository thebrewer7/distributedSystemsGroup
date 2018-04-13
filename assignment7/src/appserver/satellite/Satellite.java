package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.FileInputStream;
import appserver.server.Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code dynamically over a network
 * or locally from the cache, if a tool got executed before.
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Satellite extends Thread {
    // Class Loading Related Objects
    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private HashMap toolsCache = null;

    // Network Related Objects
    private ServerSocket serverSocket = null;
    private int port;
    private InetAddress host = null;

    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {
        
        // read this satellite's properties and populate satelliteInfo object,
        // which later on will be sent to the server
        Properties satelliteProps = new Properties();
        try{
            satelliteProps.load(new FileInputStream(satellitePropertiesFile));
        }catch(IOException e){
            System.out.println(e);
        }
        this.satelliteInfo.setPort(Integer.parseInt(satelliteProps.getProperty("PORT")));
        this.satelliteInfo.setName(satelliteProps.getProperty("NAME"));
        
        // read properties of the application server and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it out
        Properties serverProps = new Properties();
        try{
            satelliteProps.load(new FileInputStream(serverPropertiesFile));
        }catch(IOException e){
            System.out.println(e);
        }
        this.serverInfo.setHost(serverProps.getProperty("HOST"));
        this.serverInfo.setPort(Integer.parseInt(satelliteProps.getProperty("PORT")));
        
        // read properties of the code server and create class loader
        // -------------------
        Properties codeServerProps = new Properties();
        try{
            codeServerProps.load(new FileInputStream(classLoaderPropertiesFile));
        }catch(IOException e){
            System.out.println(e);
        }
        this.classLoader = new HTTPClassLoader(codeServerProps.getProperty("HOST"),
                                          Integer.parseInt(codeServerProps.getProperty("PORT")));
        
        // create tools cache
        // -------------------
        this.toolsCache = new HashMap<String, Tool>();
        
    }

    @Override
    public void run() {

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        
        
        // create server socket
        // ---------------------------------------------------------------
        try{
            // create ServerSocket
            serverSocket = new ServerSocket(port);
            
            // loop that is always listening for new clients
            // if a clien connects it sends the client to ServerThread
            while(true){
                System.out.println("Waiting for clients");
                
                //connect to client
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a new client");
                new Thread(new SatelliteThread(socket, this)).start();
            }
        } catch(IOException e){
            
        }
        
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
    }

    // inner helper class that is instanciated in above server loop and processes single job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            
            // reading message
            // ...
            
            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...
                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     */
    public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject = null;

        // ...
        
        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
    }
}
