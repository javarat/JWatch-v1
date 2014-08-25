import java.net.InetAddress;
import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;


public class WindowTester {
	
//	private final static int PORT = 7575; 
//	private static List<ClientInfo> connectedClients = new ArrayList<ClientInfo>();
	public static void main(String args[]) {
		
		
		try {
			System.out.println("HOST NAME IS: " + InetAddress.getLocalHost().getHostName());
			System.out.println("LOCAL HOST IS: " + InetAddress.getLocalHost());
			System.out.println("LOCAL HOST IS: " + InetAddress.getLocalHost().getHostAddress());


		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ReentrantLock lock = new ReentrantLock();
		//starts listening and accepting clients trying to communicate with HQ, aka JServer
//		Window window = new Window();
		new Window();

	}

}
