import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Making it Serializable enables the ability to send this object over the network through Input & Output Streams
 */
public class PeerInfo  implements java.io.Serializable {
    private InetAddress host;
    private int clientPort; //this is the port that it will use on the client side
    private int serverPort; //this is the port others will connect to its server side
    private String username;
    private UUID id;

    //--------- Constructors
    public PeerInfo(int serverPort){
        this.serverPort = serverPort;
        id = UUID.randomUUID();

        try{
            host = InetAddress.getLocalHost();
        } catch(UnknownHostException e){
            System.out.println(e);
        }
    }

    public PeerInfo(int serverPort, String username){
        this.username = username;
        this.serverPort = serverPort;
        id = UUID.randomUUID();

        try{
            host = InetAddress.getLocalHost();
        } catch(UnknownHostException e){
            System.out.println(e);
        }
    }

    //--------- Getters & Setters

    public InetAddress getHost() {
        return host;
    }

    public int getClientPort() {
        return clientPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }
}
