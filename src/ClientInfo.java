import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;

//just contain the Socket and a name
public class ClientInfo {
	//FIRST SOCKET IS THE MAIN SOCKET CONNECTION WHERE COMMANDS ARE PASSED!
	
	//mainSocket is the one that only deals with if connection is still there.
	private Socket mainSocket; 
	private Socket commandSocket;
	private ArrayList<SocketInfo> subSockets; //includes both socket and TYPE!
	private String clientName; //userName of Client
	private ImageIcon flag;
	private String country;
	private String city;
	private String operatingSystem;
	private String internalIP;
	private String externalIP;
	private ReentrantLock lock;

	
	
	
	public ClientInfo(Socket socket, String clientName, ImageIcon flag, String country, String city, String operatingSystem, String internalIP, String externalIP) {
		lock = new ReentrantLock(true);
		subSockets = new ArrayList<SocketInfo>();
		this.mainSocket = socket;
		this.clientName = clientName;
		this.flag = flag;
		this.country = country;
		this.city = city;
		this.operatingSystem = operatingSystem;
		this.internalIP = internalIP;
		this.externalIP = externalIP;
	}
	
	public ImageIcon getFlag() {
		return flag;
		
	}
	
	public String getCountry() {
		return country;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getOperatingSystem() {
		return operatingSystem;
	}
	
	public String getInternalIP() {
		return internalIP;
	}
	
	public String getExternalIP() {
		return externalIP;
	}
	
	@Override
	public String toString() {
		return clientName;
		
	}
	
	public Socket getMainSocket() {
		return mainSocket;
	}
	
	public Socket getCommandSocket() {
		return commandSocket;
	}
	
	public void closeCommandSocket() {
		try {
			commandSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeAllSubSockets() {
		for(SocketInfo c : subSockets) {
			try {
				c.getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addCommandSocket(Socket socket) {
		this.commandSocket = socket;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	
	public int getNumSubSockets() {
		return subSockets.size();
	}
	
	public SocketInfo getSubSocketInfo(int index) {
		return subSockets.get(index);
	}
	
	//gets subsocket by type
	public SocketInfo getSubSocketInfo(String type) {
		Iterator<SocketInfo> i = subSockets.iterator();
		SocketInfo s;
		while(i.hasNext()) {
			s = i.next();
			if(s.getSocketType().equals(type)) {
				return s;
			}
		}
		return null;
	}
	
	public int getNumSubSocketsByType(String type) {
//		CopyOnWriteArrayList<SocketInfo> si = new CopyOnWriteArrayList<SocketInfo>(subSockets);
//		Iterator<SocketInfo> i = si.iterator();
		lock.lock();
		Iterator<SocketInfo> i = subSockets.iterator();
		SocketInfo s;
		int start = 0;
		while(i.hasNext()) {
			s = i.next();
			if(s == null) return 0;
			System.out.println("TYPE: " + s.getSocketType());
			if(s.getSocketType().contains(type)) {
				start++;
			}
		}
		lock.unlock();
		return start;
	}
	
//	//finds the index of subsocket and it first closes the socket and then removing the socket from the subsocket array.
//	public void removeFirstSubSocket() {
////		System.out.println("NUMBER OF SOCKETS IS: " + subSockets.size());
////		Socket temp = subSockets.get(0);
////		try {
////			temp.close();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		subSockets.remove(0);
////		if(index == 0) {
////			subSockets.remove(0);
////		} else {
////			subSockets.remove(index ); //remove it.
////		}
////		subSockets.remove(index); //remove it.
//	}
	
	//will remove all sockets with this type
	public void removeSocketsByType(String type) {
		lock.lock();
		Iterator<SocketInfo> i = subSockets.iterator();
		SocketInfo s;
		while(i.hasNext()) {
			s = i.next();
			if(s.getSocketType().contains(type)) {
				try {
					s.getSocket().close();
				}catch(IOException e) {
					e.printStackTrace();
				}
				i.remove();
			}
		}
		lock.unlock();
	}
	
	//adds a socket connection in relation to this Client
	public void addSubSocket(SocketInfo socket) {
		lock.lock();
//		System.out.println("I'M IN HERE MOTHERFUCKER!");
//		System.exit(0);
		System.out.println("NUMBER OF SOCKETS IS: " + subSockets.size());
		subSockets.add(socket);
		lock.unlock();
	}

}
