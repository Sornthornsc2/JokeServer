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

public class JokeClient {
	public static String name = null;
	public static String uuidString = null;

	public static void main(String args[]) {
		String serverName;
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];

		System.out.println("Sornthorn Anujavanit's JokeClient, 1.8\n");
		System.out.println("Using server: " + serverName + ", Port: 4545");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		try {
			String input;
			//when start new client get name from user
			do {
				System.out.print("Enter name: ");
				input = in.readLine();
				if (!input.trim().isEmpty()) {
					name = input;
				}
				//keep asking for name
			} while (name ==  null) ;

			//after get a name from user
			while (uuidString == null) {
				// send name to server, to get uuid

				Socket sock = new Socket(serverName, 4545);
				PrintStream toServer = new PrintStream(sock.getOutputStream());
				toServer.println(name);
				BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				uuidString = fromServer.readLine();
			}

			//when press any key send uuid to server to get joke and proverb
			do {
				System.out.print("Press (Enter), or (quit) to end: ");
				input = in.readLine();

				Socket sock = new Socket(serverName, 4545);

				PrintStream toServer = new PrintStream(sock.getOutputStream());
				toServer.println(uuidString);

				BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				String serverMessage;
				serverMessage = fromServer.readLine();
				switch (serverMessage) {
					case "JOKE CYCLE COMPLETED":
						serverMessage = fromServer.readLine(); // read joke
						System.out.println(serverMessage);
						System.out.println("JOKE CYCLE COMPLETED" + System.lineSeparator());
						break;

					case "PROVERB CYCLE COMPLETED":
						serverMessage = fromServer.readLine(); // read proverb
						System.out.println(serverMessage);
						System.out.println("PROVERB CYCLE COMPLETED" + System.lineSeparator());
						break;
					default:
						System.out.println(serverMessage);
						break;
				}

				sock.close();
			} while (!input.equalsIgnoreCase("quit"));
			System.out.println("Cancelled by user request.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}