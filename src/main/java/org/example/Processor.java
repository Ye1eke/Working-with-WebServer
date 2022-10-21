package org.example;

import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processor of HTTP request.
 */

public class Processor extends Thread{
    private final Socket socket;
    private final HttpRequest request;
    

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    public String response = "";
    //variables...
    //...
    @Override
    public void run(){
        try {
            process();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
   
    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        String s = request.toString();
        if(s.contains("exec")){
            int n;
            n = 100000;
            
            boolean prime[] = new boolean[n+1];
            for(int i=0;i<=n;i++)
                prime[i] = true;
            
            for(int p = 2; p*p <=n; p++)
            {
                // If prime[p] is not changed, then it is a prime
                if(prime[p] == true)
                {
                    // Update all multiples of p
                    for(int i = p*p; i <= n; i += p)
                        prime[i] = false;
                }
            }

            for(int i = 2; i <= n; i++)
            {
                if(prime[i] == true)
                    System.out.print(i + " ");
            }
        }
        else if(s.contains("delete"))
        {
            String filename = "";
            int find = 12;
            while(s.charAt(find) != ' '){
                char c = s.charAt(find);
                filename += c;
                find++;
            }
            File f= new File(filename);
            if(f.delete()){
                this.response += f.getName() + " deleted";   //getting and printing the file name
            }
            else
            {
                this.response += "failed";
            }
        }
        else if(s.contains("create"))
        {   
            int find = 12;
            String name = "";
            while(s.charAt(find) != ' '){
                char c = s.charAt(find);
                name += c;
                find++;
            }

            File myObj = new File(name);
            if (myObj.createNewFile()) {
                this.response += ("File created: " + myObj.getName());
            }
            else {
                this.response += "File Already Exists!";
            }
        }
       
        PrintWriter output = new PrintWriter(socket.getOutputStream());

        // We are returning a simple web page now.
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.println("<html>");
        output.println("<head><title>Hello</title></head>");
        output.println("<body><p>" + this.response + "</p></body>");
        output.println("</html>");
        output.flush();
        socket.close();
        this.response = "";
    }
}
