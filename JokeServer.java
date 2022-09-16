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

class Worker extends Thread {
	Socket sock;
	
	//using hashmap to store jokes, proverbs, and uuid
	private static HashMap<UUID, List<Integer>> uuidJokeListHashMap = new HashMap<>();
	private static HashMap<UUID, List<Integer>> uuidProverbListHashMap = new HashMap<>();
	private static HashMap<UUID, String> uuidNameHashMap = new HashMap<>();

	public static final String[] jokeList = { "JA", "JB", "JC", "JD" };
	//jokes from https://parade.com/968634/parade/jokes-for-kids/
	public static final String[] jokeBodyList = {
		"What do you call a boomerang that will not come back? Stick", // JA
		"What does a cloud wear under his raincoat? Thunderwear", // JB
		"Two pickles fell out of a jar onto the floor. What did one say to the other? Dill with it.", // JC
		 "What time is it when the clock strikes 13? Time to get a new clock. "}; // JD
	
	public static final String[] proverbList = { "PA", "PB", "PC", "PD" };
	//proverbs from https://www.phrases.org.uk/meanings/proverbs.html
	public static final String[] proverbBodyList = {
		"Opportunity never knocks twice at any man's door", //  "PA" 
		"Set a thief to catch a thief", //  "PB"
		"The truth will out", //  "PC"
		"Time is money" //  "PD" 
		};
	
	//for random joke and proverb
	//when cycle completed re-random the list
	private static void initJokeProverbIndexOrderList(List<Integer> list) {
		list.clear();
		list.addAll(Arrays.asList(0, 1, 2, 3));
		Collections.shuffle(list);
		// System.out.println(list.toString());
	}

	Worker(Socket s) {
		sock = s;
	}

	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		try {

			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());

			String clientMessage = in.readLine();

			UUID uuid;
			List<Integer> list;

			try {  
				// if client send uuid, send joke/proverb back
				uuid = UUID.fromString(clientMessage); 
				String userName = uuidNameHashMap.get(uuid);
				// System.out.print("client: " + uuid.toString() + " ");

				switch (JokeServer.mode) {
					case JOKE_MODE:
						list = uuidJokeListHashMap.get(uuid);
						
						sendJokeToClient(out, list, userName);
						break;
					case PROVERB_MODE:
						list = uuidProverbListHashMap.get(uuid);
						sendProverbToClient(out, list, userName);
						break;
				}
			} catch (IllegalArgumentException iae) { // client not send uuid, assume is a name
				//create uuid
				uuid = UUID.randomUUID();
				String name = clientMessage;
				
				//save uuid and name in hashmap
				uuidNameHashMap.put(uuid, name);

				//create joke lists
				//then put into hashmap
				list = new ArrayList<>();
				initJokeProverbIndexOrderList(list);
				uuidJokeListHashMap.put(uuid, list);

				//create proverb list
				//then put into hashmap
				list = new ArrayList<>();
				initJokeProverbIndexOrderList(list);
				uuidProverbListHashMap.put(uuid, list);

				// send uuid back to client
				out.println(uuid.toString());
				out.flush();
			}

			sock.close();
		} catch (IOException ioe) {
			System.out.println("server: read error");
			ioe.printStackTrace();
		}
	}



	//send joke to client
	private void sendJokeToClient(PrintStream out, List<Integer> list, String userName) {
		int index;

		String jokeHead; //ja jb ......
		String jokeBody; //joke body
		String jokeMessage; //combining jokeHead, userName, and joke body

		//if joke doesn't complete yet, server will send joke
		if (list.size() > 1) {
			index = list.get(0);
			list.remove(0);

			jokeHead = jokeList[index];
			jokeBody = jokeBodyList[index];
			jokeMessage = jokeHead + " " + userName + ": " + jokeBody;

			out.println(jokeMessage);  // send joke to client
			System.out.println(jokeMessage);
		} else if (list.size() == 1) { // last joke, size = 1
			index = list.get(0);
			list.remove(0);

			jokeHead = jokeList[index];
			jokeBody = jokeBodyList[index];
			jokeMessage = jokeHead + " " + userName + ": " + jokeBody;
			out.println("JOKE CYCLE COMPLETED"); // send "JOKE CYCLE COMPLETED"
			out.println(jokeMessage);; // send joke to client

			System.out.println(jokeMessage);
			System.out.println(userName + ": JOKE CYCLE COMPLETED");
			// reset textJokeProverb order
			initJokeProverbIndexOrderList(list);
		} 
	}

	//same like sendJokeToClient
	private void sendProverbToClient(PrintStream out, List<Integer> list, String userName) {
		int index;
		String proverbHead;
		String proverbBody;
		String proverbMessage;
		
		if (list.size() > 1) {
			index = list.get(0);
			list.remove(0);

			proverbHead = proverbList[index];
			proverbBody = proverbBodyList[index];
			proverbMessage = proverbHead + " " + userName + ": " + proverbBody;

			// System.out.println("server: send to client: " + proverb);
			out.println(proverbMessage);
			System.out.println(proverbMessage);
		} else if (list.size() == 1) { // last proverb, size = 1
			index = list.get(0);
			list.remove(0);
			proverbHead = proverbList[index];
			proverbBody = proverbBodyList[index];
			proverbMessage = proverbHead + " " + userName + ": " + proverbBody;
			
			out.println("PROVERB CYCLE COMPLETED"); // send Completed
			out.println(proverbMessage); // send message to client
			
			// System.out.println("server: send to client: " + proverb);
			System.out.println(proverbMessage);
			System.out.println(userName + ": PROVERB CYCLE COMPLETED");

			// reset textproverbProverb order
			initJokeProverbIndexOrderList(list);
		}
	}
}

//for jokeClientAdmin
class AdminWorker extends Thread {
	Socket sock;

	AdminWorker(Socket s) {
		sock = s;
	}

	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());
			//when admin send any key toggle mode between joke and proverb
			switch (JokeServer.mode) {
				case JOKE_MODE:
					JokeServer.mode = JokeServer.JokeMode.PROVERB_MODE;
					//send to admin client the current mode
					out.println("Proverb Mode");
					out.flush();
					System.out.println("server: change mode: Proverb");
					break;
				case PROVERB_MODE:
					JokeServer.mode = JokeServer.JokeMode.JOKE_MODE;
					//send to admin client the current mode
					out.println("Joke Mode"); 
					out.flush();
					System.out.println("server: change mode: Joke");
					break;
			}
			sock.close();
		} catch (IOException ioe) {
			System.out.println("server: read error");
			ioe.printStackTrace();
		}

	}
}

public class JokeServer {

	public static JokeMode mode = JokeMode.JOKE_MODE;

	enum JokeMode {
		JOKE_MODE,
		PROVERB_MODE
	}

	public static void main(String a[]) throws IOException {

		JokeAdminServer jas = new JokeAdminServer();
		Thread adminThread = new Thread(jas); 
		adminThread.start(); //start a new thread to get connection from adminClient

		int q_len = 6;
		int port = 4545;
		Socket sock;
		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Sornthorn Anujavanit's JokeServer server 1.8 starting up, listening at port 4545.\n");

		while (true) {
			sock = servsock.accept();
			new Worker(sock).start();
		}
	}
}

class JokeAdminServer implements Runnable {

	JokeAdminServer() {
		System.out.println("Sornthorn Anujavanit's JokeAdminServer server 1.8 starting up, listening at port 5050.\n");
	}

	public void run() {
		int q_len = 6;
		int port = 5050;
		Socket sock;

		try {
			ServerSocket servsock = new ServerSocket(port, q_len);
			while (true) {
				sock = servsock.accept();
				new AdminWorker(sock).start(); 
			}
		} catch (IOException ioe) {
			System.out.println("server: read error");
			ioe.printStackTrace();
		}

	}
}