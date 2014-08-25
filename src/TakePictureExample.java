import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;


/**
 * Example of how to take single picture.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class TakePictureExample extends JFrame{
	
	public TakePictureExample() {
		JLabel boy = new JLabel();
		add(boy);
		
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		// get default webcam and open it
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(new Dimension(640, 480));
//		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		// get image
//		BufferedImage image = webcam.getImage();
		
		JFrame troll = new JFrame();
		JLabel boy = new JLabel();
		troll.add(boy);
		boy.setPreferredSize(new Dimension(640, 480));
		troll.pack();
		troll.setResizable(false);
		troll.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		troll.setVisible(true);
		
		
		while(true) {
			boy.setIcon(new ImageIcon(webcam.getImage()));
//			Thread.sleep(200);
		}

//		// save image to PNG file
//		ImageIO.write(image, "PNG", new File("test.png"));
//		image = webcam.getImage();
//		ImageIO.write(image, "PNG", new File("test1.png"));
//		image = webcam.getImage();
//		ImageIO.write(image, "PNG", new File("test2.png"));
//		image = webcam.getImage();
//		ImageIO.write(image, "PNG", new File("test3.png"));
//		
		

	}
}
