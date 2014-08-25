import java.net.Socket;


public class SocketInfo {
	
	private Socket socket;
	private String socketType;
	
	public SocketInfo(Socket socket, String socketType) {
		this.socket = socket;
		this.socketType = socketType;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getSocketType() {
		return socketType;
	}

}
