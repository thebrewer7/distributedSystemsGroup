import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    /*
        inputStream = fromClient
        outputStream - toClient
     */

    public static void main(String args[]) throws IOException{
        ServerSocket serverSocket = new ServerSocket(8080);

        //this while loop represents how the server is "always listening"
        while(true){
            //connect to a client
            Socket client = serverSocket.accept();
            System.out.println("Connected to a client");

            //get input and output streams
            PrintWriter toClient = new PrintWriter(client.getOutputStream());
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

        }
    }


}
