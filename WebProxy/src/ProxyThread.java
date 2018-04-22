import java.net.*;
import java.io.*;
import java.util.*;
public class ProxyThread extends Thread
{
	
	private Socket clientSocket = null;
    private static final int BUFFER_SIZE = 32768;
    public ProxyThread(Socket socket)
    {
    	//Ensure proper behavior from superclass: Thread
        super("ProxyThread");	
        this.clientSocket = socket;        
    }
    
    //Get input from browser
    //Send request to server
    //Get response from server
    //Send response to browser

    public void run() 
    {           	
        try 
        {
            DataOutputStream out =new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            //To read data from the client         
            BufferedReader rd = null;
			//To send data to the client
            DataOutputStream outData =new DataOutputStream(clientSocket.getOutputStream());
            //Printing the HTTP Request
            
			String urlToCall = "";
			String inputLine = in.readLine();
            String[] tokens = inputLine.split(" ");
            urlToCall = tokens[1];
            
            /*This loop reads the request from the client line-by-line,
             * and prints out the request as string in the console.*/
			while(!inputLine.isEmpty())
			{
				System.out.println(inputLine);
				inputLine = in.readLine(); 
			}
			
			URL url;
            url = new URL(urlToCall); 
			
			//Initializing a HTTP Connection in the URL requested
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            InputStream is = null;
            
            //getResponseCode would get the response code for the requested URL
            conn.getResponseCode();
            
            //Initializing a boolean to check the cache.
            boolean cacheHit=false;
            
            //A file to store the URL requested
        	File file = new File("url.txt");

        	//A file to store the cache of the URL
        	File cache=new File("Cache.txt");
        	

        	try 
        	{
        		//Checking to see if the website is cached.
        	    Scanner scanner = new Scanner(file);
        	    if(scanner.hasNextLine())
        	    {
            	    String cacheURL=scanner.nextLine();
            	    
            	    //If the cached URL is same as the URL requested, the website is cached.
            	    if(cacheURL.equals(urlToCall))
            	    {
            	    	cacheHit=true;
            	    	System.out.println("This website has been cached.");
            	    }
        	    }
        	    scanner.close();

        	}
        	//Print File Not Found Exception if the files are unable to open
        	catch(FileNotFoundException e)
        	{ 
        	    System.out.println("File was not found");
        	}
        	
        	//System.out.println(conn.getContentLength()); 
        	
            if (conn.getContentLength() > 0)
            {
            	try 
                {
                	if(cacheHit)
                	{
                		//Uses cache to write all the data
                  		FileInputStream fis = new FileInputStream(cache);
                		byte[] data = new byte[(int) cache.length()];
                		fis.read(data);
                		fis.close();
                		outData.write(data);
                   	}
                    is = conn.getInputStream();
                	rd = new BufferedReader(new InputStreamReader(is));
                	//System.out.println(cacheHit);
                } 
                catch (IOException ioe)
                {
                	System.out.println("Error 404 Not Found");
                	outData.writeBytes("HTTP/1.1 404 NOT FOUND" + "\r\n");
                    System.out.println(	"********* IO EXCEPTION **********: " + ioe);
                    System.exit(0);
                }           
            }
			
            //Allocating a byte size in the given buffer.
			byte by[] = new byte[ BUFFER_SIZE ];
            int index = is.read( by, 0, BUFFER_SIZE );
            
            //A string initialized to read all the content of website
            String s = new String(by); 

            //Writing the URL requested and HTML content to cache file
            PrintWriter writer = new PrintWriter("Cache.txt");
            writer.println(urlToCall);
            writer.println(s);

            writer.close();
            
            //Sending the output to the client
            while (index != -1)
            {
               outData.write( by, 0, index );
               index = is.read( by, 0, BUFFER_SIZE );
            }
           
            int responseCode = conn.getResponseCode();
            System.out.println();
                       
            if (responseCode == 200)
            {
            	System.out.println("200 OK");
            }
            else if (responseCode == 404)
            {
            	System.out.println("404 Not Found");

            }
            else if (responseCode == 400)
            {
            	System.out.println("400 Bad Request");
            }
            
            
            String outputLine = rd.readLine();
            while(!outputLine.isEmpty())
			{
				System.out.println(outputLine);
				outputLine = in.readLine(); 
			}
			
//          Map<String, List<String>> map = conn.getHeaderFields();
//          System.out.println("-------RESPONSE--------\n");
// 
//          for (Map.Entry<String, List<String>> entry : map.entrySet()) 
//          {
//          		System.out.println(entry.getKey() + " : " +entry.getValue());
//          }
//              
            System.out.println();
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
            if (clientSocket != null)
            {
                clientSocket.close();
            }

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
}