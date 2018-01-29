/*Assignment 0 - Echo Server
  Team: Chandler Hayes, Kelli Ruddy, Logan Brewer
  1/29/18
*/
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

                //accept new client
                Socket client = serverSocket.accept();
                System.out.println("Connected to a new client\n\n");

                //send client to a thread to communicate with server
                new Thread(new EchoThread(client)).start();
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }

//method used because implementing Runnable to create a client thread
    public void run(){
       Thread clientThread = Thread.currentThread();
    }
}

//Implements Runnable to get multithreading
class EchoThread implements Runnable
{
  Socket client;
  String line = null;
  BufferedReader  fromClient = null;
  PrintWriter toClient = null;

  public EchoThread(Socket client)
  {
    this.client = client;
  }

  public void run()
  {
    //Read what client says and output that back - store in expected variables
    try
    {
      fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
      toClient =new PrintWriter(client.getOutputStream());
    }
    catch(IOException e)
    {
      System.out.println("IO error in server thread");
    }
    try
    {
      line = fromClient.readLine();
      //Check for client to say "quit"
      while(line.compareToIgnoreCase("QUIT") != 0)
      {
        line = fromClient.readLine();
        //use a regex to remove all nonletters when echoing back to the client
        line = line.replaceAll("[\\W\\d]", "");
        toClient.println(line);
        toClient.flush();
        System.out.println("Response to Client  :  " + line);
      }
    }
    catch (IOException e)
    {
      System.out.println("IO Error/ Client terminated abruptly");
    }
    finally
    {
      //Close socket input and output
      try
      {
        System.out.println("Connection Closing..");
        if (fromClient!=null)
        {
          fromClient.close();
          System.out.println(" Socket Input Stream Closed");
        }
        if(toClient!=null)
        {
          toClient.close();
          System.out.println("Socket Out Closed");
        }
        if (client!=null)
        {
          client.close();
          System.out.println("Socket Closed");
        }
      }
      catch(IOException ie)
      {
        System.out.println("Socket Close Error");
      }
    }
  }
}
