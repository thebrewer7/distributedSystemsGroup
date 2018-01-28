import java.io.*;
import java.net.*;

/**
 * Class [EchoThread] - An abstract template class for a Socket-Server.
 * Different servers can be derived from this generic server. In order to do that,
 * the method processConnection() needs to be implemented.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class EchoThread implements Runnable {

    protected ServerSocket serverSocket;
    protected int port;
    protected Socket socket;
    protected boolean isStopped;

    public GenericServer(int port)
    {
    	isStopped = false;
      this.port = port;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                System.out.println("Waiting for connections on port #" + port);
                socket = serverSocket.accept();
                System.out.println("Connection with client established!");
                new Thread(new HTTPWorker(socket)).start();
            }

        } catch (IOException ioe) {
            System.err.println("IOException" + ioe.getMessage());
            ioe.printStackTrace();
        }
    }
    public void stop() throws IOException
    {
    	isStopped = true;
    	this.serverSocket.close();
    }
}
