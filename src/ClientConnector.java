import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

//just a thread that helps JServer accepts clients
//by waiting continuously
public class ClientConnector implements Runnable {
//	private int clientCount; //clientCount starts at 0
	private int PORT;
	private List<ClientInfo> connectedClients;
	private Lock lock;
	private DefaultTableModel model;
	private Object monitor;
	
	public ClientConnector(int PORT, ArrayList<ClientInfo> connectedClients, ReentrantLock lock, DefaultTableModel model, Object monitor) {
//		this.clientCount = 1;
		this.PORT = PORT;
		this.connectedClients = connectedClients;
		this.lock = lock;
		this.model = model;
		this.monitor = monitor;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			//Creates the ServerSocket
			//opens up the connection to receive signals from at the specified port
			ServerSocket serverSocket;
			serverSocket = new ServerSocket(PORT);
			while(true) {
				//waits for a connection and accepts it
				Socket clientSocket = serverSocket.accept();
				lock.lock();
				boolean isNewClient = isNewClient(clientSocket);
				if(isNewClient) {
					System.out.println("A Client has connected!");
					String clientName = null;
					String country = null;
					ImageIcon flag = null; //country get from the country
					String city = null;
					String operatingSystem = null;
					String internalIP= null;
					String externalIP = null;
					BufferedReader inDepthClientInfo = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line;
					int i = 0; 
					System.out.println("GAY of death.");
//					clientSocket.getInputStream().flush();
//					byte[] temp = new byte[1];
					//reads whatever command and determines what to do.
//					clientSocket.getInputStream().read(temp, 0, 1);
//					System.out.println("GOT IT IT'S: " + temp[0]);
//					for(int k = 0; k < 4; k++) {
//					System.out.println((char) 5);
//					}
					System.out.println("stfu!");
					while(!((line = inDepthClientInfo.readLine()).equals("end")))  {
						System.out.println(line);
						switch(i) {
						case 0: clientName = line;
						System.out.println("CUUUUUNT: " + clientName);
						System.out.println("33333333: " + line);

							break;
						case 1: country = line;
							break;
						case 2: city = line;
							break;
						case 3: operatingSystem = line;
							break;
						case 4: internalIP = line;
							break;
						case 5: externalIP = line;
						default:
							break;
						}
						i++;
						System.out.println("loop of death.");
					}
					System.out.println("I'M GAY!");
					model.addRow(new Object[]{clientName, "flag", country, city, operatingSystem, internalIP, externalIP});
//					connectedClients.add(new ClientInfo(clientSocket, "Client " + (connectedClients.size() + 1)));
					connectedClients.add(new ClientInfo(clientSocket, clientName, flag, country, city, operatingSystem, internalIP, externalIP));
					if(connectedClients.size() == 1) { //FIRST CLIENT JOINED,  WAKE UP SERVERCONNECTIONCHECKER!
						synchronized(monitor) {
							monitor.notify();
						}
					}

				}
				lock.unlock();
//				System.exit(0);
				
				//Add it to the List
//				if(clientCount > 1 && clientCount > connectedClients.size()) {
//					clientCount = clientCount - 2;
//				}
//				if(isNewClient) {
//					lock.lock();
////					System.out.println("FUCK ME!");
//					connectedClients.add(new ClientInfo(clientSocket, "Client " + (connectedClients.size() + 1)));
//					lock.unlock();
//				}
//				clientCount++; //increment clientCount!
//				serverSocket.close();
//				}
			}
		} catch(IOException e) {
			//idk what to do
		}	
	}
	
//	public String stringParser(InputStream inDepthClientInfo) {
//		for (i = nextChar; i < nChars; i++) {
//		    c = cb[i];
//		    if ((c == '\n') || (c == '\r')) {
//			eol = true;
//			break charLoop;
//		    }
//		}
//		
//	}
	
	//checks if it's a new client, or a subsocket of a client
	public boolean isNewClient(Socket clientSocket) {
		System.out.println("FUCK TREES");
		System.out.println("CONNECTED CLIENTS NUMBER: " + connectedClients.size());
		for(ClientInfo client : connectedClients) {
			System.out.println("DICK SUCKS");
			if(clientSocket.getInetAddress().equals(client.getMainSocket().getInetAddress())) {
				System.out.println("EQUALS MOTHERFUCKER!");
				if(client.getCommandSocket() == null) {
					client.addCommandSocket(clientSocket);
					System.out.println("CLIENTCONNECTOR: HAVE ADDED COMMAND SOCKET");
				} else {
					//THIS IS WHERE WE ADD IT TO THE SUBSOCKET!
					BufferedReader bf;
					String type;
					try {
						bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						type = bf.readLine();
						System.out.println("TYPE: " + type);
//						if(!type.equals("maxilius")) {
							client.addSubSocket(new SocketInfo(clientSocket, type));
//						} else {
//							System.out.println("FUCK YOU BUG!");
//						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
		}
		return true;
	}
	

}
