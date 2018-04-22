import java.io.*;
import java.net.* ;
import java.util.* ;

/*A proxy server that recieves request from the client, forwards it to
 * web server and receives a response to send it back to the client.*/

public final class Webserver
{
	//Allocating a buffer size to read from FileInputStream
    private static final int BUFFER_SIZE = 32768;
    
    //Main-method
	public static void main(String args[]) throws IOException
	{
		//Opening a socket in port 8080 and start listening
		ServerSocket server = new ServerSocket(8080); 
		System.out.println("Listening for connection on port 8080...");
		System.out.println();
		
		while(true)
		{
			Socket clientSocket = server.accept();
			
			//To read data from the server
			InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());
			//To send data to the server
			BufferedReader out = new BufferedReader(in);
			
            //To read data from the client         
            BufferedReader rd = null;
			//To send data to the client
            DataOutputStream outData =new DataOutputStream(clientSocket.getOutputStream());
		
			//Printing the HTTP Request
			String urlToCall = "";
			String line = out.readLine();
            StringTokenizer tok = new StringTokenizer(line);
            String[] tokens = line.split(" ");
            urlToCall = tokens[1];
            
            /*This loop reads the request from the client line-by-line,
             * and prints out the request as string in the console.*/
			while(!line.isEmpty())
			{
				System.out.println(line);
				line = out.readLine(); 
			}
			
			//send HTTP response
			//Date current = new Date(); 
			//String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + current; 
			//clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			
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
        	file.createNewFile();
        	//A file to store the cache of the URL
        	File cache=new File("Cache.txt");
        	cache.createNewFile();
        	
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
                	//System.out.println("Error 404 Not Found");
                    //outData.writeBytes("HTTP/1.1 404 NOT FOUND" + "\r\n");
                    System.out.println(	"********* IO EXCEPTION **********: " + ioe);
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
            
            
           //Read the result from the server
           InputStream rdData = conn.getInputStream();
       	   rd = new BufferedReader(new InputStreamReader(rdData));
            
//            Map<String, List<String>> map = conn.getHeaderFields();
//
//            System.out.println();
//            System.out.println(); 
//         	System.out.println("-------RESPONSE--------\n");
//
//         	for (Map.Entry<String, List<String>> entry : map.entrySet()) 
//         	{
//         		System.out.println(entry.getKey() + " : " +entry.getValue());
//         	}
//
//         	System.out.println("\nGet Response Header By Key ...\n");
//         	String webserver = conn.getHeaderField("Server");
//
//         	if (webserver == null) {
//         		System.out.println("Key 'Server' is not found!");
//         	} else {
//         		System.out.println("Server - " + server);
//         	}
//
//         	System.out.println("\n Done");
              
             //Clear the outData steam using flush
             outData.flush();          
             
             //Closing all the sockets and data stream.
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
		
	}
	
}
