import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

//executes commands key mouse wise sent from the server
public class KeyMouseExecutor extends Thread{
	
	DataInputStream in;
	
	public KeyMouseExecutor(InputStream in) {
		if(in == null) {
			System.out.println("44444444444444444");
		}
		this.in = new DataInputStream(new BufferedInputStream(in));
		this.start();
	}
	
	@Override
	public void run() {
		boolean keepRunning = true;
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
//			while (keepRunning && !Thread.interrupted()) {

			while (keepRunning) {
				System.out.println("LOVE IS ON ME!");
				int messageType, x, y, keyCode;
				if(in == null) {
					System.out.println("BUILDINGS BUILDINGS BUILDINGS");
					System.exit(0);
				}
				messageType = in.readInt();
				switch (messageType) {
				case MouseEvent.MOUSE_PRESSED:
					int button = in.readInt();
					x = in.readInt();
					y = in.readInt();
					System.out.println("Mouse button " + button + " press at " + x + "," + y);
					robot.mouseMove(x, y);
					robot.mousePress(convertButtonCode(button));
					break;
				case MouseEvent.MOUSE_RELEASED:
					button = in.readInt();
					x = in.readInt();
					y = in.readInt();
					System.out.println("Mouse button " + button + " release at " + x + "," + y);
					robot.mouseMove(x, y);
					robot.mouseRelease(convertButtonCode(button));
					break;
				case (MouseEvent.MOUSE_LAST + 1):  // private code for key pressed
					keyCode = in.readInt();
					System.out.println("Key " + keyCode + " pressed");
					robot.keyPress(keyCode);
					break;
				case (MouseEvent.MOUSE_LAST + 2): // private code for key released
					keyCode = in.readInt();
					System.out.println("Key " + keyCode + " released");
					robot.keyRelease(keyCode);
					break;
				default:
					System.out.println("Unknown remote control code " + messageType);
				}
			}
		} catch (IOException e) {
//			e.printStackTrace();
		}

	}
	
	int convertButtonCode(int keyCode) {
		// converts MouseEvent button codes to Robot button masks
		switch (keyCode) {
		case MouseEvent.BUTTON1:
			return MouseEvent.BUTTON1_MASK;
		case MouseEvent.BUTTON2:
			return MouseEvent.BUTTON2_MASK;
		case MouseEvent.BUTTON3:
			return MouseEvent.BUTTON3_MASK;
		}
		return 0;
	}

}
