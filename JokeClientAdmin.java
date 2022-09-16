/*--------------------------------------------------------

1. Sornthorn Anujavanit 1/19/2022

2. java version "17.0.1" 2021-10-19 LTS

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java
> javac JokeClient.java
> javac JokeAdminClient.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

5. List of files needed for running the program.

 a. JokeServer.java
 b. JokeClient.java
 c. JokeClientAdmin.java

5. Notes:

----------------------------------------------------------*/

import java.io.*;
import java.net.*;
import java.util.*;

public class JokeClientAdmin {
    public static void main(String args[]) {
        String serverName;
        serverName = "localhost";

        System.out.println("Sornthorn Anujavanit's JokeClientAdmin, 1.8\n");
        System.out.println("Using server: " + serverName + ", Port: 5050");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            String input;
            //press enter to toggle between joke and proverb
            do {
                System.out.print("Press <Enter> to change mode,  or (quit) to end: ");
                input = in.readLine();

                Socket sock = new Socket(serverName, 5050);
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                
                String serverMessage;
                try {
                    //get current mode from server
                    serverMessage = fromServer.readLine();
                    System.out.println("Changing Mode to " + serverMessage);
                } catch (Exception ex) {
                    System.out.println("error");
                    ex.printStackTrace();
                }
                sock.close();
            } while (!input.equalsIgnoreCase("quit"));
            System.out.println("Cancelled by user request.");
        } catch (IOException ioe) {
            System.out.println("server: read error");
            ioe.printStackTrace();
        }
    }
}
