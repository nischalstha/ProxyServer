import java.net.*;
import java.io.*;

public class Server
{
	
	public static void main(String[] args) throws IOException 
	{
        ServerSocket serverSocket = null;
        boolean listening = true;
        //Initialize a port to listen
        int port = 8080;	       
        try 
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening for connection on port: " + port);
    		System.out.println();        
    	}
        catch (IOException e) 
        {
            System.err.println("Failed listening on port:  " + port);
            System.exit(-1);
        }
        //The port in the socket will always be listening
        while (listening) 
        {
        	new ProxyThread(serverSocket.accept()).start();
        }
        //It will not reach here but will close socket for safety
        serverSocket.close();
        System.out.println("Server Closed");
    }

}
