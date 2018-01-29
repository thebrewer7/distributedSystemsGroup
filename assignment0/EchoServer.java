import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class EchoServer implements Runnable{
    private Socket client;
    private Thread currThread;

    public static void main(String args[]) throws IOException{
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true){
            try {
                System.out.println("Waiting for clients...");

                Socket client = serverSocket.accept();  //accept new client
                System.out.println("Connected to a new client\n\n");

                //send client to a thread to communicate with server
                new Thread(new EchoThread(client)).start();
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }
//Might need this below but I doubt it
    public void run(){
       // Thread clientThread = Thread.currentThread();
    }

}

class EchoThread implements Runnable {
    Socket client;

    public EchoThread(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            PrintWriter toClient = new PrintWriter(client.getOutputStream());
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("Thread is connected");

//            System.out.println("Closing client socket");
//            client.close();

            //close input and output streams
            toClient.close();
            fromClient.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
