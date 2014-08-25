import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class RemoteWebcam extends Thread {
	private ClientInfo requestedClient;
	private WindowAdapter windowEventClose;
//	private int frameRateDelay = 200; //delay between framerates
	
	//WINDOWADAPTER EXTENDS WINDOWLISTENER? WTF???

	public RemoteWebcam(ClientInfo requestedClient, WindowAdapter windowEventClose) {
		this.requestedClient = requestedClient;
		this.windowEventClose = windowEventClose;
	}
	
	@Override
	public void run() {
		JFrame frame = null;
//		ImportantMessage.infoBox(requestedClient.getClientName() + " does not have a webcam", "No Webcam Error");
		try {
			while(true) {
			DataInputStream inate = null;
			int width = 0, height = 0;
			inate = new DataInputStream(new BufferedInputStream(
						requestedClient.getSubSocketInfo("webcamScreen").getSocket().getInputStream()));
//						requestedClient.getSubSocket(0).getInputStream()));
				width = inate.readInt();
				//CHECK WHETHER -1 IS SENT THAT MEANS THERE IS NO WEBCAM ON THE CLIENT'S MACHINE! :o
				if(width == -1) { //NO NEED TO CLOSE JFRAME IF YOU HAVEN'T GIVEN ANYTHING TO IT YET RIGHT ? :)
					//called to hog the screen until ok is pressed :)
					ImportantMessage.infoBox(requestedClient.getClientName() + " does not have a webcam", "No Webcam Error");
					break;
				}
				height = inate.readInt();
				System.out.println("Connected to Server. Screen size " + width + "x"
						+ height);
			// create in-memory Image, and get access to its raw pixel data array
				final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			DataBufferInt db = (DataBufferInt) image.getRaster().getDataBuffer();
			int[] pixels = db.getData();

		    frame = new JFrame(requestedClient.getClientName() + "@Webcam" );
		    
			frame.setLocation(20, 20);
			final JLabel label = new JLabel();
			label.setFocusable(true); //ALLOWS REMOTE CONTROL!

			label.setIcon(new ImageIcon(image));
			frame.add(new JScrollPane(label), BorderLayout.CENTER);
			JPanel screenShotSlider = new JPanel();
		    JButton screenShot = new JButton("Take Screenshot");
		    screenShot.setFocusable(false);
		    screenShot.addActionListener(new ScreenShotButton(image));    
		    //ADD RECORD LATER MAYBE??? :)  //200 - 252
		    //0 - 52
		    JSlider consecutiveFrameDelay = new JSlider(0, 252, 52); //800 is 200 in reverse !
		    consecutiveFrameDelay.setFocusable(false);
		    consecutiveFrameDelay.addChangeListener(new ChangeListener() {
		        public void stateChanged(ChangeEvent evt) {
		          JSlider slider = (JSlider) evt.getSource();
		          if (!slider.getValueIsAdjusting()) {
		            int value = slider.getValue();
		            try {
			            System.out.println(value);
			            OutputStream clientOut = requestedClient.getSubSocketInfo("webcamFrameRate").getSocket().getOutputStream();
						clientOut.write(Math.abs(value - 252));
						clientOut.flush();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		          }
		        }
		      });
		    screenShotSlider.add(screenShot, BorderLayout.WEST);
		    screenShotSlider.add(consecutiveFrameDelay, BorderLayout.EAST);
		    frame.add(screenShotSlider, BorderLayout.SOUTH);
			frame.pack();
//			setWindowSize(frame);
			frame.setVisible(true);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			 frame.addWindowListener(windowEventClose);
			 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				boolean keepRunning = true;
				while (keepRunning) {
					// read updates as long as they keep coming...
					int i = 0; // index into pixel array
					while (i < pixels.length) {
						int next = inate.readInt();
						if (next == -1) { // EOF from server
							keepRunning = false;
							break;
						}
						if ((next & 0x80000000) != 0) { // "Unchanged" record
							// skip specified number of unchanged pixels
							i += (next & 0x00ffffff);
						} else { // "Changed" record
							int value = next | 0xFF000000; // RGB, with Alpha byte 255
							int count = next >>> 24; // number of repeated values
							for (int k = 0; k < count; k++) {
								pixels[i++] = value;
							}
						}
					}
//					Thread.sleep(20);
//					label.setIcon(new ImageIcon(image));
					  SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				      // Here, we can safely update the GUI
				      // because we'll be called from the
				      // event dispatch thread
						label.setIcon(new ImageIcon(image));
				    }
				  });
				}
			}
		}catch(Exception e) {
			//AS SOON AS THING BELOW ME ACTIVATES, THE ACTIONLISTENER GETS CALLED
			//REMOVING ALL OPEN SOCKETS!
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			e.printStackTrace();
			System.out.println("CLIENT FINALLY ENDED!");
		}
	}
	
//	@Override
//	public void interrupt() {
//		try {
//			//close the JFrame
//			
//		} finally {
//			super.interrupt(); //still calls interrupt
//		}
//	}
	private class ScreenShotButton implements ActionListener {

//	    createFolder
	    private int numScreen; //last screeenie  num to start counting from when saving !
	    private File screenShotFolder;
	    private BufferedImage image;
//	    private JLabel label;
	    
	    public ScreenShotButton(BufferedImage image) {
	    	screenShotFolder = createFolder();
	    	numScreen = findLastScreenieInt();
//	    	this.label = label;
	    	this.image = image;
	    }
		
		//creates a folder called "Desktop Screenshots"
		public File createFolder() {
			File toSave = new File(requestedClient.getClientName() + "(" + requestedClient.getExternalIP() + ")" + " Webcamshots");
			if(!toSave.isDirectory()) {
				toSave.mkdir(); //make it a directory.
			}
			if(!toSave.exists()) { //create a new file out of it
				try {
					toSave.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			return toSave;
		}
		
		//sets last screenie int to start numbering from !
		public int findLastScreenieInt() {
			int start = 0;
			File test;
			while(true) { 
				//FILE SEPARATOR!
				System.out.println("SCREENSHOTFOLDER ABSOLUTE PATH!: " + screenShotFolder.getAbsolutePath());
				
				test = new File(screenShotFolder.getAbsolutePath() + File.separator + "Screenshot " + start + ".png");
				if(!test.exists()) {
					break;
				}
				start++;
			}
			return start;
		}
		
		@Override 
		public void actionPerformed(ActionEvent e) {
			    numScreen = findLastScreenieInt();
				File toSave = new File(screenShotFolder.getAbsolutePath() + File.separator + "Screenshot " + numScreen + ".png");
				numScreen++; //increment it ! :)
				try {
//					Image img = icon.getImage();
//
//					BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_BYTE_ARGB);
//
					
					//http://stackoverflow.com/questions/11626307/how-to-save-java-swing-imageicon-image-to-file
//					Graphics2D g2 = bi.createGraphics();
//					g2.drawImage(img, 0, 0, null);
//					g2.dispose();
//					ImageIO.write(bi, "jpg", new File("img.jpg"));
					ImageIO.write(image, "png", toSave);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
		
	}

	public static void setWindowSize(JFrame frame) { // cosmetic

		frame.setLocation(20, 20);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		int width = frame.getSize().width + 15; // +15 is for scroll bars
		int height = frame.getSize().height + 15;

		int newWidth = Math.min(width, screenWidth - 40);
		int newHeight = Math.min(height, screenHeight - 80);

		if (newWidth != width || newHeight != height) {
			frame.setSize(newWidth, newHeight);
		}
	}

}
