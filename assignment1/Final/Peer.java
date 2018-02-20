import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Peer {
    private PeerInfo myInfo;  //info about itself like port & host
    private PeerInfo firstPeer; //this is the first peer it connects to.
    private ArrayList<PeerInfo> contactList;    //this is the list of all the peers in the network
    private boolean isRunning = true;   //this indicates if the user is still part of the network

    //----------- Codes to know which type of message is being sent
    private final static int MESSAGE_CODE_JOIN = 0;
    private final static int MESSAGE_CODE_JOIN_NOTIFY = 1;
    private final static int MESSAGE_CODE_SEND = 2;
    private final static int MESSAGE_CODE_LEAVE = 3;

    /**
     * This constructor is used if this peer is not the first peer in the network & has the contact information for
     * another peer in the network. That peer is the new peer's entry point.
     *
     * @param myInfo: the peer's info
     * @param peerInfo: the peer's contact info contact point for entering the network
     */
    public Peer(PeerInfo myInfo, PeerInfo peerInfo){
        this.myInfo = myInfo;
        firstPeer = peerInfo;
        contactList = new ArrayList<PeerInfo>();
    }

    /**
     * This constructor is used if this peer is the very first peer in the network
     * @param myInfo: the peer's info
     */
    public Peer(PeerInfo myInfo){
        this.myInfo = myInfo;
        contactList = new ArrayList<PeerInfo>();
    }

    //--------- Class Methods

    /**
     * Starts the peer's server
     * Connects to the peer where it knows the peer's port and host number
     * Receives the networks contact list for all the peers in the network
     * Contact everyone in the network letting them know that this peer has joined the network along with its info so
     *  they can contact this peer.
     * Start client thread so it can start sending messages
     */
    public void startConnection(){
        //start server thread
        startServer();
        //if it's not the first peer, then get a contact list & notify everyone that this peer has joined the network
        if(firstPeer != null){
            joinNetwork();
        }
        //start client thread so that we can message and leave the network
        startClient();
    }

    /**
     *  This method contacts firstPeer so that it can get the list of peers in the network & uses that to creates its
     *  own contactList (including firstPeer). Then it contact all of the peers of the list notifying them that it has
     *  joined the network.
     */
    private void joinNetwork(){
        //TODO: try to connect to a specific IP address besides localhost
        try{
            //send message code to tell the connecting server's peer that it's a join message
            Socket socket = new Socket(firstPeer.getHost(), firstPeer.getServerPort());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeInt(MESSAGE_CODE_JOIN);

            //Receive contactList from firstPeer
            ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
            contactList = (ArrayList<PeerInfo>) inputObject.readObject();

            //close stream and socket objects
            output.close();
            inputObject.close();
            socket.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        //notify network that peer has joined network
        notifyJoin();
    }

    /**
     * This notifies everyone in the network so that all the other peers can contact the knew peer and are aware of the
     * joining peer
     */
    private void notifyJoin(){
        //iterate through contact list to let network know they have joined
        for(int i=0; i < contactList.size(); i++){
            try{
                //get current peer's info so that we can connect to their server & message them
                PeerInfo currentPeer = contactList.get(i);
                Socket socket = new Socket(currentPeer.getHost(), currentPeer.getServerPort());

                //use DataOutputStream to send the notify join message code
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeInt(MESSAGE_CODE_JOIN_NOTIFY);

                //send new peer's info so that all the other peers can add its info to their contact list
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(myInfo);

                //close connections
                outputStream.close();
                objectOutputStream.close();
                socket.close();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void notifyLeave(){
        //send a message to all peers that the peer is leaving the network
        for(int i=0; i<contactList.size(); i++){
            //get current peer's info
            PeerInfo currentPeer = contactList.get(i);

            //connect to current peer's server
            try{
                Socket socket = new Socket(currentPeer.getHost(), currentPeer.getServerPort());

                //send message code
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeInt(MESSAGE_CODE_LEAVE);

                //send peer's info so other peers can remove it from their contact list
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(myInfo);

                //close output streams and socket
                outputStream.close();
                objectOutputStream.close();
                socket.close();

                //change isRunning to false to stop server & client threads
                isRunning = false;
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * This thread sends messages to everyone in the network when the user types something
     */
    private void startClient(){
        (new Thread(){
            @Override
            public void run(){
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

                /* this loop runs the entire time the user is part of the network. It just waits for the user to type
                 * something. When they do, we can send their message to the rest of the network.
                 */
                while(isRunning){
                    try{
                        //get the message they typed
                        String message = "";
                        message = userInput.readLine();
                        message += "";

                        //see if it's sending a message or the peer wants to leave the network
                        if(!message.equals("exit")){
                            //iterate through contactList and send everyone your message
                            for(int i=0; i<contactList.size(); i++){
                                //get current peer
                                PeerInfo currentPeer = contactList.get(i);

                                //open socket that connects to currentPeer's server & create OutputStream
                                Socket socket  = new Socket(currentPeer.getHost(), currentPeer.getServerPort());
                                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                                //send message code to let peer know it's a send message
                                dataOutputStream.writeInt(MESSAGE_CODE_SEND);

                                //Send message with following format: "Username: message"
                                BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(socket
                                        .getOutputStream()));
                                outWriter.write(myInfo.getUsername() + ": " + message);
                                outWriter.flush();

                                //close OutputStream and socket
                                dataOutputStream.close();
                                outWriter.close();
                                socket.close();
                            }
                        }
                        //leave network
                        else{
                            //close stream for listening to user input
                            userInput.close();
                            //let network know that they're leaving
                            notifyLeave();
                        }

                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
    }

    /**
     *  This starts a thread for the server component of the peer class. It waits for connects and when it receives one
     *  it hands that off to a new thread (MessengerThread) which handles each type of message appropriately
     */
    private void startServer(){
        (new Thread(){
            @Override
            public void run(){
                try{
                    //open server end of socket
                    ServerSocket serverSocket = new ServerSocket(myInfo.getServerPort());

                    //this loop waits for client connections
                    while(isRunning){
                        Socket client = serverSocket.accept();

                        //move client to be handled by server thread
                        new Thread(new MessengerThread(client)).start();
                    }
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }

        }).start();
    }

    /**
     * The server is responsible for handling received messages. For each type of message that it can receive it handles
     *  it appropriately.
     *
     *  The messages it can receive are: Join, Join Notify, Send, Leave
     *
     *  For Join:
     *      when it receives this message, it sends a copy of its contact list with its own info to the peer that
     *      send its message. This enables the new peer to have info for all the peers in the network so that it can
     *      contact everyone.
     *
     *  For Join Notify:
     *      When it receives this message, it takes the info for the peer that is sending this message. It adds that
     *      info to its contact list. A print statement is made to the command line so that the user knows a new user
     *      has joined along with their username.
     *
     *  For Send:
     *      When this message is received, it will print the message received so the user can see it. The message should
     *      be in the format of:
     *          Username: <message>
     *
     *  For Leave:
     *      When this message is received, it will receive the info of the peer that is leaving the network. This peer
     *      will be reomved from the contact list.
     *
     */
    class MessengerThread implements Runnable{
        Socket client;

        //----------- Codes to know which type of message is being sent
        private final static int MESSAGE_CODE_JOIN = 0;
        private final static int MESSAGE_CODE_JOIN_NOTIFY = 1;
        private final static int MESSAGE_CODE_SEND = 2;
        private final static int MESSAGE_CODE_LEAVE = 3;

        public MessengerThread(Socket client){
            this.client = client;
        }

        public void run(){
            try{
                DataInputStream input = new DataInputStream(client.getInputStream());
                int messageCode = input.readInt();

                switch (messageCode){
                    case MESSAGE_CODE_JOIN:
                        //Send new peer a complete list of the network
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                        //just need to add this peers info to the contact list for complete list of users in network
                        ArrayList<PeerInfo> completeList = new ArrayList<PeerInfo>(contactList);
                        completeList.add(myInfo);
                        objectOutputStream.writeObject(completeList);

                        //close stream & socket
                        objectOutputStream.close();
                        client.close();
                        break;

                    case MESSAGE_CODE_JOIN_NOTIFY:
                        //receive new peer's info
                        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                        try{
                            PeerInfo newPeerInfo = (PeerInfo) objectInputStream.readObject();

                            //add newPeer's info to contact list
                            contactList.add(newPeerInfo);

                            //notify user about the new peer
                            System.out.println("\n" + newPeerInfo.getUsername() + " has joined the network.");
                        } catch (ClassNotFoundException e){
                            System.out.println(e.getMessage());
                        }

                        //close stream and object
                        objectInputStream.close();
                        client.close();
                        break;

                    case MESSAGE_CODE_SEND:
                        //open input stream
                        BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        try{
                            String line = inputStream.readLine();
                            System.out.println(line);

                            //close sockets
                            inputStream.close();
                            client.close();
                        } catch (IOException e){
                            System.out.println(e.getMessage());
                        }

                        break;

                    case MESSAGE_CODE_LEAVE:
                        ObjectInputStream inputObjectStream = new ObjectInputStream(client.getInputStream());
                        try{
                            //get the info of the leaving peer
                            PeerInfo leavingPeer = (PeerInfo) inputObjectStream.readObject();
                            //remove them from the contact list
                            for(int i=0; i<contactList.size(); i++){
                                PeerInfo currentPeer = contactList.get(i);
                                if(leavingPeer.getId().equals(currentPeer.getId())){
                                    contactList.remove(i);
                                    contactList.size();
                                    break;
                                }
                            }


                            //notify user that the peer has left the network
                            System.out.println(leavingPeer.getUsername() + " has left the network");

                        } catch (ClassNotFoundException e){
                            System.out.println(e.getMessage());
                        }
                        break;

                     default:
                         System.out.println("\n\n Unhandled message code: " + messageCode);

                }

            } catch (IOException e){
                System.out.println(e.getMessage());

            }
        }
    }
}
