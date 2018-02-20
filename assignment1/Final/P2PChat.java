import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class P2PChat {
    public static void main(String[] args){
        //create BufferedReader to read user's typed input
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        try{

            //get the server port number the user wants to use for other peers to connect to
            System.out.println("Enter the port number you want others to connect to.");
            input = userInput.readLine();
            int serverPort = Integer.valueOf(input);

            //get a username
            System.out.println("Enter a username");
            String username = userInput.readLine();

            //create a peerInfo object to intialize with the peer class
            PeerInfo myPeerInfo = new PeerInfo(serverPort, username);
            Peer myPeer;    //represents the new peer

            System.out.println("Are you the first peer in the network? (Y/N)");
            input = userInput.readLine();
            //make sure it's a valid answer
            while(!input.equals("Y") && !input.equals("N")){
                System.out.println("Not an appropriate answer, 'Y' or 'N' only");
                input = userInput.readLine();
                System.out.println("**** Waiting for peers ***\n\n\n");
            }

            if(input.equals("N")){
                System.out.println("Enter the port number you want to connect to.");
                input = userInput.readLine();
                System.out.println("\n\n *** You can begin messaging ***");
                int destinationPort = Integer.valueOf(input);

                //make PeerInfo object about firstPeer it will connect to
                PeerInfo firstPeer = new PeerInfo(destinationPort);

                //create Peer object for user
                myPeer = new Peer(myPeerInfo, firstPeer);
            }
            else{
                myPeer = new Peer(myPeerInfo);
            }
            myPeer.startConnection();

            //TODO: run myPeer code

        } catch (UnknownHostException e){
            System.out.println(e);

        } catch (IOException e){
            System.out.println(e);
        }
    }
}
