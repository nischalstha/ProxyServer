import java.net.*;
import java.io.*;

public class Server
{
	
	public static void main(String[] args) throws IOException 
	{
        ServerSocket serverSocket = null;
        boolean listening = true;
        int port = 3339;	//will be changed anyway by the args
        try 
        {
            port = Integer.parseInt(args[0]);	//change from string to int
        } 
        catch (Exception e)
        {
            //ignore 
        }
        try 
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        }
        catch (IOException e) 
        {
            System.err.println("Couldn't listen on port: " + args[0]);
            System.exit(-1);
        }
        //will always listen
        while (listening) 
        {
        	new ProxyThread(serverSocket.accept()).start();
        }
        //won't reach here 
        serverSocket.close();
        System.out.println("Server Closed");
    }

}
