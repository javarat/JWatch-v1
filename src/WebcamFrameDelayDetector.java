import java.io.IOException;
import java.io.InputStream;

//thread that runs continously detecting delay changes for the server side remote control inspector
public class WebcamFrameDelayDetector extends Thread{
	private InputStream in;
//	private int frameRate;
	private ClientWebcam webcam;
	
	public WebcamFrameDelayDetector(InputStream in, ClientWebcam webcam) {
		this.in = in;
		this.webcam = webcam;
		start();
	}
	
	public void run() {
		try {
			while(true) {
				if(in.available() > 0) {
					webcam.targetRefreshInterval = in.read();
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
