package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
//import appserver.server.Server;
import java.io.FileInputStream;
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

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private HashMap toolsCache = null;
    
    Properties codeServerProps = null;

    // Network Related Objects
    private ServerSocket serverSocket = null;
    private int port;
    private InetAddress host = null;
    private String name;
    
    // Other needed variables
    Properties satelliteProps = new Properties();
    
    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {
        
        // read this satellite's properties and populate satelliteInfo object,
        // which later on will be sent to the server
        this.satelliteProps = new Properties();
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
            serverProps.load(new FileInputStream(serverPropertiesFile));
        }catch(IOException e){
            System.out.println(e);
        }
            
        this.serverInfo.setHost(serverProps.getProperty("HOST"));
        this.serverInfo.setPort(Integer.parseInt(serverProps.getProperty("PORT")));
        
        // read properties of the code server and create class loader
        // -------------------
        codeServerProps = new Properties();
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

        // create server socket
        try{
            // create ServerSocket
            serverSocket = new ServerSocket(satelliteInfo.getPort());
        } catch(IOException e){
            System.out.println(e);
        }

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        Message message = new Message(REGISTER_SATELLITE, satelliteInfo);
        
        // get App Server info
        InetAddress appHost = null;
        int port = serverInfo.getPort();
        try{
            appHost = InetAddress.getByName(serverInfo.getHost());
            
            // create socket to Application Server
            Socket appServer = new Socket(appHost, port);
            
            // create OutputStream and send message
            ObjectOutputStream toAppServer = new ObjectOutputStream(appServer.
                    getOutputStream());
            toAppServer.writeObject(message);
            System.out.println("Sent Reigstration to App Server");
            
        } catch(UnknownHostException e){
            System.out.println(e);
        } catch(IOException e){
            System.out.println(e);
        }
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        try{
            // loop that is always listening for new clients
            // if a clien connects it sends the client to ServerThread
            while(true){
                System.out.println("Waiting for clients");
                
                //connect to client
                //System.out.println("Accpeting client socket here");
                Socket socket = serverSocket.accept();
                //System.out.println("Client socket accepted");
                System.out.println("Connected to a new client");
                new Thread(new SatelliteThread(socket, this)).start();
            }
        } catch(IOException e){
            System.out.println(e);
        }
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
            ObjectInputStream fromClient = null;
            ObjectOutputStream toClient = null;
            Message message = null;
                    
            try{
                // setting up object streams
                fromClient = new ObjectInputStream(jobRequest.getInputStream());
                toClient = new ObjectOutputStream(jobRequest.getOutputStream());
                
                // reading message
                message = (Message)(fromClient.readObject());
            } catch(IOException e){
                System.out.println(e);
            } catch(ClassNotFoundException e){
                System.out.println(e);
            }
            
            switch (message.getType()) {
                case JOB_REQUEST:
                    //System.out.println("Here is the job request");
                    Job job = (Job)message.getContent();
                    Tool tool = null;
                    {
                        try {
                            // processing job request
                            String className = job.getToolName();
                            tool = getToolObject(className);
                            
                            Object results = tool.go(job.getParameters());
                            System.out.println("Results are: " + 
                                    (Integer) results);
                            toClient.writeObject(results);
                            System.out.println("Sent results");
                            
                            //System.out.println("Done with job request");
                        } catch (Exception ex) {
                            Logger.getLogger(Satellite.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
           
                default:
                    System.out.println("Wrong MessageType: " + message
                            .getType());
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     * @param toolString
     * @return 
     * @throws appserver.job.UnknownToolException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public Tool getToolObject(String toolString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Tool toolObject = null;
        //try to get tool object if its in the cache
        
        toolObject = (Tool) toolsCache.get(toolString);

        // check if the wanted Tool Object is already in cache
        if(toolObject == null){
            
            // the wanted object is not in cache, so we must retrieve it
            String toolClassString = codeServerProps.getProperty(toolString);
            
            // if it's still null, then it the class doesn't exist
            if(toolClassString == null){
                throw new UnknownToolException();
            }
            
            System.out.println("Tool's Class: " + toolClassString);
            
            // load the class
            Class toolClass = classLoader.loadClass(toolClassString);
            toolObject = (Tool) toolClass.newInstance();
            
            // store class in cache
            toolsCache.put(toolString, toolObject);          
        }
        else{
            System.out.println("Tool: " + toolString + " already in cache");
        }
        
        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
    }
}