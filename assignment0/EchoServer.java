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

public class EchoServer{
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
}

class EchoThread implements Runnable
{
    Socket client;
    String line = null;
    int readInt = 0;
    BufferedReader  fromClient = null;
    PrintWriter toClient = null;

    /*
        variables used to determine if user wants to quit

        quitStr holds the word 'quit' as the user types it. If the last letter is not valid
        the quitFlag is set to 0. If it is valid quitFlag is set to 1
     */
    int quitFlag = 0;
    String quitStr = "";

    public EchoThread(Socket client)
    {
        this.client = client;
    }

    public void run()
    {
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
            while(readInt != -1){
                readInt = fromClient.read();

                //convert to char, then string with compliant characters (letters only)
                char charFromClient = (char)readInt;
                String stringFromClient = String.valueOf(charFromClient);
                stringFromClient = stringFromClient.replaceAll("[\\d\\W]", "");
                //if it's a null string go to next iteration of loop
                if(stringFromClient.equals("")){
                    continue;
                }

                //print char
                System.out.println(stringFromClient);
                toClient.println(stringFromClient);

                //this checks if the user is writing "quit" or "QUIT"
                if(quitFlag == 1 || stringFromClient.equalsIgnoreCase("q")){
                    //guarantees that the the quit flag is on
                    quitFlag = 1;
                    quitStr += stringFromClient;

                    //see if the user is typing valid letters & order for 'quit'
                    quitStr = quitStr.toLowerCase();
                    if(checkQuit(quitStr)){
                        //see if the entire word has been typed
                        if(quitStr.equals("quit")){
                            //close connection
                            break;
                        }
                    }
                    //invalid letter has been typed for the word 'quit'
                    else{
                        quitFlag = 0;
                        quitStr = "";
                    }
                }

            }
        }
        catch (IOException e)
        {
            System.out.println("IO Error/ Client terminated abruptly");
        }
        finally
        {
            try
            {
                System.out.println("Connection Closing..");
                if (fromClient!=null)
                {
                    fromClient.close();
                    System.out.println("Socket Input Stream Closed");
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

    //checks if the given string matches a user typing quit
    private static boolean checkQuit(String str){
        if(str.equals("q") || str.equals("qu") || str.equals("qui") || str.equals("quit")){
            return true;
        }
        else{
            return false;
        }
    }
}
