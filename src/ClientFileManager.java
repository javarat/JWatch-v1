import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.filechooser.FileSystemView;
//import javax.swing.tree.DefaultMutableTreeNode;

//Thread on client side that deals with this clientFileManager
public class ClientFileManager extends Thread{
	private Socket fileManagerCommandSocket; //socket for commands
	private Socket fileManagerDataSocket; //socket for transferring data
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    
    private InputStream input;
    private ObjectOutputStream objectOut;

	
	public ClientFileManager(Socket fileManagerCommandSocket,Socket fileManagerDataSocket) {
		this.fileManagerCommandSocket = fileManagerCommandSocket;
		this.fileManagerDataSocket = fileManagerDataSocket;
		start();
	}
	
	@Override
	public void run() {
		byte[] temp = new byte[1];
		InputStream commandIn;
		BufferedReader dataIn;
//		OutputStream dataOut; //for data stream! 
		try {

			commandIn = fileManagerCommandSocket.getInputStream();
//			dataOut = fileManagerDataSocket.getOutputStream();
			System.out.println("CREATING OBJECTOUT FOR OBJECTINPUTSTREAM !!!");
//			objectOut.flush();
//			objectOut.flush();
			objectOut = new ObjectOutputStream(fileManagerDataSocket.getOutputStream());
			objectOut.flush();
//			Thread.sleep(3000);
			input = fileManagerDataSocket.getInputStream();
			dataIn = new BufferedReader(new InputStreamReader(fileManagerDataSocket.getInputStream()));
			
			//reads whatever command and determines what to do.
//			System.out.println("LOVE YOU SO!");
//			objectOut = new ObjectOutputStream(dataOut);
//			objectOut.flush();
			while(true) {
				System.out.println("YO MAN WHAT'S UP JUST SITTING HERE BEFORE !");
				commandIn.read(temp, 0, 1);
				System.out.println("GOT IT OOOOOOO AFTER !!");
				if(temp[0] == (byte) 10) { //10 MEANS QUIT ! AKA BREAK!
					System.out.println("\n\n\n\n\nBREAKINg\n\n\n\n\n");
					break;
				} else if(temp[0] == (byte)4) {
					
					
				} else if(temp[0] == (byte)2) { //2 is FOR SENDING THE SYSTEM ROOTS !
					System.out.println("fire bug!!");
			        File[] clientRoots = fileSystemView.getRoots();
			        for(int i = 0; i < clientRoots.length; i++) {
			        	clientRoots[i] = new FilePlus(clientRoots[i].getAbsoluteFile());
//			        	FilePlus temporary = (FilePlus)clientRoots[i];
//			        	temporary.setHasChildren();
//			        	clientRoots[i] = temporary;
			        }
//					objectOut = new ObjectOutputStream(dataOut);
//					objectOut.flush();
//					   objectOut = new ObjectOutputStream(fileManagerDataSocket.getOutputStream());
//					   objectOut.flush();
				       objectOut.writeInt(clientRoots.length);
			        	objectOut.flush();
			        for(File clientFile : clientRoots) {
//			        	objectOut.flush();
			        	objectOut.writeObject(clientFile);
			        	objectOut.flush();
//						writeStringOutStream(dataOut, clientFile.getName());
//						writeStringOutStream(dataOut, clientFile.getAbsolutePath());
//						writeStringOutStream(dataOut, (clientFile.isDirectory()) ? "1" : "0");
			        }
//					writeStringOutStream(dataOut, "end");
				} else if(temp[0] == (byte)3) { //3 IS FOR SHOW CHILDREN !
					System.out.println("RACHEL MONGOLDER");

					String directoryAbsoluteFile = dataIn.readLine();
//					String directoryAbsoluteFile = readNextLine();

//					String directoryAbsoluteFile = "chicken";

					File directory = new File(directoryAbsoluteFile);
		            File[] directoryChildren;
		            directoryChildren = fileSystemView.getFiles(directory, true);
			        for(int i = 0; i < directoryChildren.length; i++) {
						System.out.println("HORSE FLY 1!!");

			        	directoryChildren[i] = new FilePlus(directoryChildren[i].getAbsoluteFile());
						System.out.println("HORSE FLY 2!!");

//			        	FilePlus temporary = (FilePlus)directoryChildren[i];
//						System.out.println("HORSE FLY 3!!");
//
//			        	temporary.setHasChildren();
//						System.out.println("HORSE FLY 4!!");
//
//			        	directoryChildren[i] = temporary;
//						System.out.println("HORSE FLY 5!!");

			        }
//					   objectOut = new ObjectOutputStream(fileManagerDataSocket.getOutputStream());
//					   objectOut.flush();
		            System.out.println("LION MANE !");
		            System.out.println("DIRECTORY CHILDREN NUM: " + directoryChildren.length);
//					objectOut = new ObjectOutputStream(dataOut);
//					objectOut.flush();
		            objectOut.writeInt(directoryChildren.length);
		            objectOut.flush();
				    System.out.println("EVERYBODY LIKES YOU!");
		            for(File clientFile : directoryChildren) {
//			        	objectOut.flush();
		            	objectOut.writeObject(clientFile);
		            	objectOut.flush();
//						writeStringOutStream(dataOut, clientFile.getName());
//						writeStringOutStream(dataOut, clientFile.getAbsolutePath());
//						writeStringOutStream(dataOut, (clientFile.isDirectory()) ? "1" : "0");
		            }				
//					writeStringOutStream(dataOut, "end");
				} else {
					
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			
		}	
	}
	
	//reads next line  FRO INPUTSTREAM  !!! and returns it !
	public String readNextLine() {
		String build = "";
		try {
			while(true) {
				byte temp = (byte)input.read();
				if(temp != '\n') { //add to build
					build += (char)temp;
				} else {
					break; //it equals new line. you're done !
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return build;
	}
	
	//writes a string char by char to outputstream
	public static void writeStringOutStream(OutputStream out, String line) {
//		if(line == null) {
//			line = "null";
//		}
		byte[] convert = stringToByteArray(line);
		try {
			out.write(convert, 0, line.length() + 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//addds newline character!
	//ADDS NEWLINE CHARACTER SO IT'S LIKE WRITING A LINE !
	public static byte[] stringToByteArray(String line) {
		System.out.println(line);
		byte[] temp = new byte[line.length() + 1];
		for(int i = 0; i < line.length(); i++) {
			temp[i] = (byte)line.charAt(i);
//			System.out.println(line);
		}
		temp[line.length()] = '\n';
		return temp;
	}

}
