import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.DefaultTableModel;

//this runs and checks the connection
//for client to see if it's still connected
//if not it will throw an error for the parent
//to use and do other stuff with

public class ServerConnectionChecker implements Runnable {
	List<ClientInfo> connectedClients;
	Lock lock;
	DefaultTableModel model;
	Object monitor;
		
	public ServerConnectionChecker(ArrayList<ClientInfo> connectedClients, ReentrantLock lock, DefaultTableModel model, Object monitor) {
		this.connectedClients = connectedClients;
		this.lock = lock;
		this.model = model;
		this.monitor = monitor;
	}
		
	@Override
	//assumes that there is nothing in the buffer from other stuff
	//make sure other methods accessing the streams
	//are also synchronized
	public void run() {
		

		PrintWriter out;
		InputStreamReader reader;
		
		while(true) {
			//wait until ONE client connects
			synchronized(monitor) {
				try {
//					System.out.println("why waste the chance fucker?");
					monitor.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				//out = new PrintWriter(socket.getOutputStream(), true);
				//reader = new InputStreamReader(socket.getInputStream());
				char[] temp = new char[1];
				while(true) {
			    //System.out.println("FUCK!");
                //synchronized(connectedClients) {
					lock.lock();
//				     System.out.println("LOCKED NIGGA!");
//				     System.exit(0);
					Iterator<ClientInfo> i = connectedClients.iterator();
					ClientInfo clientInfo;
					while(i.hasNext()) {
						clientInfo = i.next();
//					    System.out.println("CLIENT NAME: " + clientInfo.getClientName());
						out = new PrintWriter(clientInfo.getMainSocket().getOutputStream(), true);
						reader = new InputStreamReader(clientInfo.getMainSocket().getInputStream());
						out.write('a');
						if(out.checkError()) {
							//error so means the client has disconnected
							//remove from the list of clients
							System.out.println("\n" + clientInfo.getClientName() + " has disconnected!");
							//CLOSE COMMAND SOCKET DON'T FORGET. EVEN IF IT ALREADY CLOSED YOU DON'T KNOW!
							//actually WHEN PROGRAM DIES. ALL THESE SOCKETS ARE ASSUMED TO BE CLOSED.
							//CUZ THEY ALL DEAD LOL.
							//ALL OTHER SOCKETS ARE DEALT WITH ! :o
							//STILL CLOSE COMMAND SOCKET JUST IN CASE!
							//YOU KNOW MAIN SOCKET ALREADY DEAD ROFL CUZ U CAN'T WRITE FOR SHIT!
							//MAIN IS BY DEFAULT ALREADY CLOSED. THAT IS NOW WE GOT HERE. LOL.
							//EVERYTHING BELOW IS CLOSED JUST IN CASE. JUST FOR SAFE KEEPINGS.
							clientInfo.closeCommandSocket();
							clientInfo.closeAllSubSockets();
							
							//FINALLY REMOVE FROM THE ARRAY BYE BYE!
							i.remove(); //remove from the array
							findAndDeleteRow(clientInfo);
						} else { //success so read back so not mess up stream!
//					    	out.flush();
//							out.flush();
							reader.read(temp);
						}
//						System.out.println("WELL FUCK YOU!");
//						System.exit(0);
					}
					lock.unlock();
					if(connectedClients.size() == 0) {  //if size is zero, break and go enter that monitor synchronized block to wait until get notified.
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					out.close(); //resets
//					reader.close();
//				}
//				for(ClientInfo clientInfo : clients) {
//					out = new PrintWriter(clientInfo.getSocket().getOutputStream(), true);
//					reader = new InputStreamReader(clientInfo.getSocket().getInputStream());
//					out.write('a');
//					if(out.checkError()) {
//						//error so means the client has disconnected
//						//remove from the list of clients
//						clients.remove(clientInfo);
//					} else { //success so read back so not mess up stream!
//						reader.read(temp);
//					}
//					out.close(); //resets
//					reader.close();
//				}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
//		} catch (ConcurrentModificationException e) {
//			//does nothing because there is no harm at all.
			} catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
//		return null;
		// TODO Auto-generated method stub
//		return null;
	}
	
	//searches the rows for the same person with same internal IP. then remove it.
	public void findAndDeleteRow(ClientInfo client) {
		for(int i = 0; i < model.getRowCount(); i++) {
			String internalIP = (String)model.getValueAt(i, 5);
			if(client.getInternalIP().equals(internalIP)) {
				model.removeRow(i);
				break;
			}
		}
	}
}


