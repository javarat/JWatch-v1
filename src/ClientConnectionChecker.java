//import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//this runs and checks the connection
//for client to see if it's still connected
//if not it will throw an error for the parent
//to use and do other stuff with

public class ClientConnectionChecker implements Runnable {
	private Socket socket;
		
	public ClientConnectionChecker(Socket socket) {
		this.socket = socket;
	}
		
	@Override
	//it's synchronized so it's thread safe
	//assumes that there is nothing in the buffer from other stuff
	//make sure other methods accessing the streams
	//are also synchronized
	public void run() {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			char[] temp = new char[1];
			while(true) {
				out.write('a');
				if(out.checkError()) {
					//THIS IS THE UNCAUGHT EXCEPTION! :) SHOULD THROW ARITHMETICEXCEPTION. WHICH THEN CLIENT CATCHES OOOOOO!!!
					int i = 0/0; //this will throw an error which jClient will deal with
					if(i == 1) {
						System.out.println("TESTING!");
					}
					//above code kind of hacky but it gets the job done. :)
					
//					throw new Exception("Server has disconnected. Connection lost.");
				} else { //success so read back so not mess up stream!
//					out.flush();
					reader.read(temp);
				}
				
//UNCOMMENT THIS PLEASE :o
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
//		return null;
		// TODO Auto-generated method stub
//		return null;
	}
	

}
