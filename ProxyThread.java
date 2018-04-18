import java.net.*;
import java.io.*;
import java.util.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class ProxyThread extends Thread
{
	
	private Socket socket = null;
    private static final int BUFFER_SIZE = 32768;
    public ProxyThread(Socket socket)
    {
        super("ProxyThread");	//ensure proper behaviour from superclass: Thread
        this.socket = socket;
        
    }
    
    //get input from browser
    //send request to server
    //get response from server
    //send response to browser
    @Override
    public void run() 
    {

    	
        try 
        {
            DataOutputStream out =new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String requestString = in.readLine();
	        String headerLine = requestString;
	        StringBuffer responseBuffer = new StringBuffer();
	        
	        StringTokenizer tokenizer = new StringTokenizer(headerLine);
	        String httpMethod = tokenizer.nextToken();
	        String httpQueryString = tokenizer.nextToken();
	
	        responseBuffer.append("The HTTP Client request is ....<BR>");
	        
	        System.out.println("-----RESPONSE------");
            while (in.ready())
 	       {
 	            // Read the HTTP complete HTTP Query
 	            responseBuffer.append(requestString + "<BR>");
 	            System.out.println(requestString);
 	            requestString = in.readLine();
 	        } 
            
            //end get request from client


            BufferedReader rd = null;
            try 
            {
            	Boolean blocked=false;
            	File file = new File("url.txt");
            	//check to see if url is blocked
            	try 
            	{
            	    Scanner scanner = new Scanner(file);
            	    while(scanner.hasNextLine()) 
            	    {
            	        String line = scanner.nextLine();  
            	        if(line.equals(httpQueryString)) 
            	        { 
            	            blocked=true;
            	        }
            	    }
            	    scanner.close();
            	} 
            	catch(FileNotFoundException e)
            	{ 
            	
            	    System.out.println("File was not found");
            	}
            	
            	
            	if(!blocked)
            	{
            		boolean https=false;
            		System.out.println("sending request to real server for url: "+ httpQueryString);
	                
	                //determine whether http or https
	                URL url;
	                try
	                {
	                	url = new URL(httpQueryString); 
	                }
	                catch(Exception e)
	                {
	                	https=true;
	                	url = new URL("https://"+httpQueryString);
	                }
	                
	                //begin send request to server, get response from server
	                URLConnection conn = url.openConnection();
	                conn.setDoInput(true);
	                conn.setDoOutput(false);
	
	                // Get the response
	                InputStream is = null;
	                //https
	                if(https)
	                {
	                	System.out.println("Enter an http url");
	                }               
	                else //http
	                {
	                	boolean cacheHit=false;
	                	File cache=new File("Cache.txt");
	                	try 
	                	{
	                		//check to see if the website was cached
	                	    Scanner scanner = new Scanner(file);
	                	    if(scanner.hasNextLine())
	                	    {
		                	    String cacheURL=scanner.nextLine();
		                	    if(cacheURL.equals(httpQueryString))
		                	    {
		                	    	cacheHit=true;
		                	    }
	                	    }
	                	    scanner.close();

	                	} 
	                	catch(FileNotFoundException e)
	                	{ 
	                	    System.out.println("File was not found");
	                	}
	                	
		                if (conn.getContentLength() > 0)
		                {

		                	try 
		                    {
		                    	if(cacheHit)
		                    	{
		                    		//uses cache
		                      		FileInputStream fis = new FileInputStream(cache);
		                    		byte[] data = new byte[(int) cache.length()];
		                    		fis.read(data);
		                    		fis.close();
		                    		out.write(data);
		                       	}
		                        is = conn.getInputStream();
		                    	rd = new BufferedReader(new InputStreamReader(is));
		                    } 
		                    catch (IOException ioe)
		                    {
		                        System.out.println(	"********* IO EXCEPTION **********: " + ioe);
		                    }
		                    
		                }
	                

	                //end send request to server, get response from server
	                
	                //begin send response to client
	                byte by[] = new byte[ BUFFER_SIZE ];
	                int index = is.read( by, 0, BUFFER_SIZE );

	                //write url and data to cache
	                PrintWriter writer = new PrintWriter("Cache.txt");
	                writer.println(httpQueryString);
	                writer.println(by);

	                writer.close();
	                //output to client
	                while (index != -1)
	                {
	                  out.write( by, 0, index );
	                  index = is.read( by, 0, BUFFER_SIZE );
	                }
	                out.flush();
	                //end send response to client
	                }
            	}
            	else
            	{
            		System.out.println("URL BLOCKED");
            	}
            	
            	
            } 
            catch (Exception e) 
            {
                //output to management console
                System.err.println("Encountered exception: " + e);
                out.writeBytes("");
            }

            //close all resources
            if (rd != null)
            {
                rd.close();
            }
            if (out != null)
            {
                out.close();
            }
            if (in != null) 
            {
                in.close();
            }
            if (socket != null)
            {
                socket.close();
            }

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
}
