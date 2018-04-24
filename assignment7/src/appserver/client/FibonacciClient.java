/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appserver.client;

import appserver.comm.Message;
import appserver.comm.MessageTypes;
import appserver.job.Job;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;

/**
 *
 * @author Chandler
 */
public class FibonacciClient extends Thread implements MessageTypes{
    // class variables
    int argument;   // the fibonacci number to compute
    Properties properties = null;
    
    // network variables
    int port;
    String host = null;
    
    
    public FibonacciClient(String serverProperties, int argument){
        this.argument = argument;
        
        // retrieve server properties
        try{
            properties = new PropertyHandler(serverProperties);
        } catch(FileNotFoundException e){
            System.out.println("Could not open server properties file in "
                    + "FibClient");
            System.out.println(e);
        } catch(IOException e){
            System.out.println("IOException for opening server proprties file "
                    + "in FibClient");
            System.out.println(e);
        }
        
        //get host and port that the server is running on
        host = properties.getProperty("HOST");
        port = Integer.valueOf(properties.getProperty("PORT"));
        
        System.out.println("Fib Client connecting to Host: " + host + " and "
                + "Port: " + port);
    }
    
    public void run(){
        Socket serverSocket = null;
        try{
            // connect to app server
            serverSocket = new Socket(host, port);
        }catch(IOException e){
            System.out.println("FibonacciClient: Could not connect to server "
                    + "socket");
            System.out.println(e);
        }
        
        //--------- Create Job & Message
        
        // hard-coded string of Fibonacci tool class name
        String classString = "appserver.job.impl.Fibonacci";
        // fib number to be computed
        Integer fibNumber = new Integer(42);
            
        // create job and message
        Job job = new Job(classString, fibNumber);
        Message message = new Message(JOB_REQUEST, job);
            
        //--------- Send Message to Application Server
        ObjectOutputStream toServer = null;
        // create ObjectOutputStream
        try{
            // send message w/ job request to app server
            toServer = new ObjectOutputStream(serverSocket.getOutputStream());
        } catch(IOException e){
            System.out.println("FibonacciClient: could not create "
                    + "ObjectOutputStream");
            System.out.println(e);
        }
        
        // send message
        try{
            toServer.writeObject(message);
        } catch(IOException e){
            System.out.println("FibonacciClient: could not send message ");
            System.out.println(e);
        }
        
        // --------- Receive Response From Application Server
        // create ObjectInputStream
        ObjectInputStream fromServer = null;
        try{
            fromServer = new ObjectInputStream(serverSocket.getInputStream());
        } catch(IOException e){
            System.out.println("FibonacciClient: could not create "
                    + "ObjectInputStream");
            System.out.println(e);
        }
        
        // receive message
        try{
            Integer result = (Integer) fromServer.readObject();
            System.out.println("RESULT: " + result);
        } catch(IOException e){
            System.out.println("FibonacciClient: could not receive message ");
            System.out.println(e);
        } catch(ClassNotFoundException e){
            System.out.println("FibonacciClient: wrong class received for "
                    + "result");
            System.out.println(e);
        }
    }
    
    public static void main(String[] args){
        if(args.length == 1){
           for(Integer i=46; i>0; i--){
                FibonacciClient client = new FibonacciClient(args[0], i);
                client.start();       
           }
        }
        else{
            for(Integer i =46; i>0; i--){
                FibonacciClient client = new FibonacciClient("../../config"
                        + "/Server.properties", i);
               client.start();
            }
        }
    }
    
}
