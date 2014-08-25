import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;


public class ClientWebcam extends Thread{
	DataOutputStream out;
	private int[] prevData = null;
	private int width = 640, height = 480;
	//http://stackoverflow.com/questions/20022612/databufferbyte-cannot-be-cast-to-databufferint
	private BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
	protected int targetRefreshInterval;
	
	public ClientWebcam(OutputStream out, int targetRefreshInterval) {
		this.out = new DataOutputStream(new BufferedOutputStream(out));
		
//		try {
//			this.out.writeInt(width);
//			this.out.writeInt(height);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.targetRefreshInterval = targetRefreshInterval;
		start();
	}
	
//	public void run() {
//		// get default webcam and open it
//		Webcam webcam = Webcam.getDefault();
//		webcam.setViewSize(new Dimension(640, 480));
//		//	webcam.setViewSize(WebcamResolution.VGA.getSize());
//		webcam.open();
//		// get image
//		//	BufferedImage image = webcam.getImage();
//	
////		JFrame troll = new JFrame();
////		JLabel boy = new JLabel();
////		troll.add(boy);
////		boy.setPreferredSize(new Dimension(640, 480));
////		troll.pack();
////		troll.setResizable(false);
////		troll.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		troll.setVisible(true);
//	
//	
//		while(true) {
//			boy.setIcon(new ImageIcon(webcam.getImage()));
//			//		Thread.sleep(200);
//		}
//		
//		
//
//	}
	
	@Override
	//THESE THREADS SHOULD END ITSELF AS SOON AS SOCKETS ARE CLOSED, THE EXCEPTION IS CAUGHT AND THEN RUN IS ENDED AND THREAD DIES.
	public void run() {
		Webcam webcam = Webcam.getDefault();
		if(webcam == null) {
			try {
				this.out.writeInt(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return; //no CAMERA! :o
		}
		webcam.setViewSize(new Dimension(width, height));
		//	webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();
		try {
			this.out.writeInt(width);
			this.out.writeInt(height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		BufferedImage image;
	
		try {
			while (true) {
				long startTime = new Date().getTime();
				
//				Webcam buildin = Webcam.getWebcams().get(0);
//				if(buildin == null) {
//					System.out.println("FUCK YOU webcam!");
//				}
//				image = buildin.getImage();
//				image = robot.createScreenCapture(new Rectangle(width, height));
//				int[] pixels = directRobot.getRGBPixels(screen);
//				ColorModel model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
//				image = new BufferedImage(model, Raster.createWritableRaster(model.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, width * height), null), false, new Hashtable<Object, Object>());
//				image = robot.createScreenCapture(screen);
//				Point p = MouseInfo.getPointerInfo().getLocation();
//				Point p = directRobot.
				BufferedImage temp = webcam.getImage();
				Graphics2D g = image.createGraphics();
				g.drawImage(temp, 0, 0, null);

				Point p = MouseInfo.getPointerInfo().getLocation();

				//CONTROLS THE MOUSE OMFG LEL!
//				if((previousX != p.x) || previousY != p.y) { //is different
//					image.createGraphics().drawImage(cursor, p.x, p.y, null);
//					previousX = p.x;
//					previousY = p.y;
//				}
//				Raster ras = ((BufferedImage) image).getData();

				Raster ras = image.getData();
				DataBufferInt db = (DataBufferInt)ras.getDataBuffer();
				int[] data = db.getData();

				int bytesSent = sendIncrementalRLE(data, prevData, out);
				if (bytesSent < 0)
					break; // error
				prevData = data;
				out.flush();
				long endTime = new Date().getTime();
//				System.out.println("Updated " + (bytesSent + 1023) / 1024
//						+ "kB, in " + (endTime - startTime) + " mSec");
				long timeToSleep = targetRefreshInterval - (endTime - startTime);
				if (timeToSleep > 0)
					Thread.sleep(timeToSleep);
			}
			out.writeInt(-1); // EOF code sent to client
			out.close();
		} catch (IOException e) {
			System.out.println("\nScreen viewing Server disconnected");
//			if (DefaultWebServer.exitOnDisconnect) {
//				System.out.println("Exiting.");
//				System.exit(0);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webcam.close();
		}
	}
	
	int sendIncrementalRLE(int[] data, int[] prevData, DataOutputStream out) {

		// Test for unchanged pixels, and just send how many they are, otherwize
		// find repeated int values in array - replace with 1 value + repeat count
		// 1st byte of ints are Alpha - not used here, so that's where
		// the count is stored.  Returns no of bytes sent, -1 for error.
		//
		// Output stream format is sequence of 1 integer records describing the
		// data array in natural order (element 0 ... data.length-1)
		//
		// Unchanged record (sequence of >=1 unchanged pixels): 
		// bit 0 is 1,
		// bits 8-31 are number of consecutive unchanged pixels.
		//
		// Changed record (sequence of >=1 identical changed pixels): 
		// bit 0 is 0,
		// bits 1-7 are number of consecutive pixels the same,
		// bits 8-31 are the 3 byte RGB values for these pixels.
		//
		// Skipping unchanged pixels is based on a transparent pixel idea
		// from DaniWeb user Clawsy.  Thanks Clawsy.

		try {
			int bytesSent = 0;
			int i = 0; // index into data array
			int equalCount = 0, dataValue = 0, dataCount = 0;
			while (i < data.length) {
				while (prevData != null && i < data.length
						&& data[i] == prevData[i]) {
					equalCount++;
					i++;
				}
				if (equalCount > 0) {
					out.writeInt(equalCount | 0x80000000);
					bytesSent += 4;
					equalCount = 0;
				}
				if (i >= data.length)
					break;
				dataValue = data[i];
				dataCount = 1;
				i++;
				while (i < data.length && data[i] == dataValue && dataCount < 127) {
					dataCount++;
					i++;
				}
				out.writeInt((dataValue & 0x00FFFFFF) | (dataCount << 24));
				bytesSent += 4;
			}
			return bytesSent;
		} catch (IOException e) {
			return -1; // error
		}
	}
}
