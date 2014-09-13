import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
//import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;
import javax.imageio.ImageIO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
//import java.nio.channels.FileChannel;
import java.net.URL;

/**
A basic File Browser.  Requires 1.6+ for the Desktop & SwingWorker
classes, amongst other minor things.

Includes support classes FileTableModel & FileTreeCellRenderer.

@TODO Bugs
<li>Fix keyboard focus issues - especially when functions like
rename/delete etc. are called that update nodes & file lists.
<li>Needs more testing in general.

@TODO Functionality
<li>Double clicking a directory in the table, should update the tree
<li>Move progress bar?
<li>Add other file display modes (besides table) in CardLayout?
<li>Menus + other cruft?
<li>Implement history/back
<li>Allow multiple selection
<li>Add file search

@author Andrew Thompson
@version 2011-06-08
@see http://stackoverflow.com/questions/6182110
@license LGPL
*/
public class FileBrowser extends JFrame{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//	private final JFrame officialFrame;
//	private final WindowAdapter windowEventClose;
//	private final ClientInfo requestedClient;
	private InputStream dataIn;
	private OutputStream dataOut;
	private OutputStream commandOut;
    private ObjectInputStream objectReader;
    
    private FileBrowser fb = this;


    /** Title of the application */
    public static final String APP_TITLE = "JWatch File Manager";
    /** Used to open/edit/print files. */
    private Desktop desktop;
    /** Provides nice icons and names for files. */
    private FileSystemView fileSystemView;

    /** currently selected File. */
    private File currentFile;
    /**CURRENTLY SELECTED CLIENTFILE*/
    private File clientCurrentFile;

    /** Main GUI container */
    private JPanel gui;

    /** File-system tree. Built Lazily */
    private JTree tree;
    private JTree clientTree;
    private DefaultTreeModel treeModel;
    private DefaultTreeModel clientTreeModel;
    
    /** Directory listing */
    private JTable table;
    private JProgressBar progressBar;
    /** Table model for File[]. */
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;
    
    /**Directory listing for client*/
    private JTable clientTable;
//    private JProgressBar clientProgressBar;
    /** Table model for File[]. */
    private ClientFileTableModel clientFileTableModel;
    private ListSelectionListener clientListSelectionListener;
    private boolean clientCellSizesSet = false;
    private int clientRowIconPadding = 6;
    
    /* File controls. */
    private JButton openFile;
    private JButton printFile;
    private JButton editFile;
    private JButton move;

    /* File details. */
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;
    
    /*CLIENT FILE CONTROLS AND DETAILS*/
    /* File controls. */
    private JButton clientOpenFile;
    private JButton clientPrintFile;
    private JButton clientEditFile;
    private JButton clientMove;

    /* File details. */
    
    private JLabel clientFileName;
    private JTextField clientPath;
    private JLabel clientDate;
    private JLabel clientSize;
    private JCheckBox clientReadable;
    private JCheckBox clientWritable;
    private JCheckBox clientExecutable;
    private JRadioButton clientIsDirectory;
    private JRadioButton clientIsFile;

    /* GUI options/containers for new File/Directory creation.  Created lazily. */
//    private JPanel newFilePanel;
//    private JRadioButton newTypeFile;
//    private JTextField name;

    public Container getGui() {
        if (gui==null) {
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5,5,5,5));

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            JPanel detailView = new JPanel(new BorderLayout(3,3));

            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                }
            };
            
            //FOR CLIENT//
            JPanel clientDetailView = new JPanel(new BorderLayout(3, 3));

            clientTable = new JTable();
            clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            clientTable.setAutoCreateRowSorter(true);
            clientTable.setShowVerticalLines(false);

            clientListSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = clientTable.getSelectionModel().getLeadSelectionIndex();
                    setClientFileDetails( ((FileTableModel)clientTable.getModel()).getFile(row) );
                }
                
            };
            clientTable.getSelectionModel().addListSelectionListener(clientListSelectionListener);
            JScrollPane clientTableScroll = new JScrollPane(clientTable);
            Dimension clientD = clientTableScroll.getPreferredSize();
            clientTableScroll.setPreferredSize(new Dimension((int)clientD.getWidth(), (int)clientD.getHeight()/2));
            clientDetailView.add(clientTableScroll, BorderLayout.CENTER);
            //FOR CLIENT//
            
            
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
            detailView.add(tableScroll, BorderLayout.CENTER);

            // the File tree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);
            
//            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
//            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse){
                	System.out.println("FUCK'NS HIT");

                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showChildren(node);
                    setFileDetails((File)node.getUserObject());
                }
            };

            // show the file system roots.
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
                //
            }

            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            JScrollPane treeScroll = new JScrollPane(tree);

            // as per trashgod tip
            tree.setVisibleRowCount(15);
            
            
            //EXPERIMENTO!!!
            DefaultMutableTreeNode clientRoot = new DefaultMutableTreeNode();
            clientTreeModel = new DefaultTreeModel(clientRoot);
            
            TreeSelectionListener clientTreeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse){
                	System.out.println("FUCK'NS HIT");

                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showClientChildren(node);
                    setClientFileDetails((File)node.getUserObject());
                }
            };

//            // show the CLIENT file system roots. NEED TO CHANGE THIS SHIT OMFG!
//            File[] clientRoots = fileSystemView.getRoots();
//            for (File fileSystemRoot : clientRoots) {
//                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
//                clientRoot.add( node );
//                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
//                for (File file : files) {
//                    if (file.isDirectory()) {
//                        node.add(new DefaultMutableTreeNode(file));
//                    }
//                }
//                //
//            }
            
            //ADDS ROOT AS WELL AS SYSTE MROOTS ! :)
            addClientRoots(clientRoot);
            System.out.println("STOP WHERE YOU GOING!");
            
            clientTree = new JTree(clientTreeModel);
            clientTree.setRootVisible(false);
            clientTree.addTreeSelectionListener(clientTreeSelectionListener);
            clientTree.setCellRenderer(new ClientFileTreeCellRenderer());
            clientTree.expandRow(0);
            JScrollPane treeScroll1 = new JScrollPane(clientTree);

            // as per trashgod tip
            clientTree.setVisibleRowCount(15);
            Dimension preferredSize1 = treeScroll1.getPreferredSize();
            Dimension widePreferred1 = new Dimension(
                200,
                (int)preferredSize1.getHeight());
            treeScroll1.setPreferredSize( widePreferred1 );
            //EXPERIMENTO!
            
            
            
            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(
                200,
                (int)preferredSize.getHeight());
            treeScroll.setPreferredSize( widePreferred );

            // details for a File
            JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

            fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
            path = new JTextField(5);
            path.setEditable(false);
            fileDetailsValues.add(path);
            fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
            date = new JLabel();
            fileDetailsValues.add(date);
            fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
            size = new JLabel();
            fileDetailsValues.add(size);
            fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

            JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING,4,0));

            isDirectory = new JRadioButton("Directory");
            flags.add(isDirectory);

            isFile = new JRadioButton("File");
            flags.add(isFile);
            fileDetailsValues.add(flags);

            JToolBar toolBar = new JToolBar();
            // mnemonics stop working in a floated toolbar
            toolBar.setFloatable(false);

            JButton locateFile = new JButton("Locate");
            locateFile.setMnemonic('l');

            locateFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Locate: " + currentFile.getParentFile());
                        desktop.open(currentFile.getParentFile());
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(locateFile);

            openFile = new JButton("Open");
            openFile.setMnemonic('o');

            openFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Open: " + currentFile);
                        desktop.open(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(openFile);

            editFile = new JButton("Edit");
            editFile.setMnemonic('e');
            editFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.edit(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            toolBar.add(editFile);

            printFile = new JButton("Print");
            printFile.setMnemonic('p');
            printFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.print(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            toolBar.add(printFile);
            
            move = new JButton("Move");
            move.setMnemonic('m');
            move.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        byte[] temp ={(byte)4}; //SEND COMMAND TO GET CHILDREN FOR THIS NODE
                		commandOut.write(temp, 0, 1); //SEND 3 WHICH IS GET NODES OMFG !
//                    	commandOut.
                        desktop.print(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            toolBar.add(move);
            
            

            // Check the actions are supported on this platform!
            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
            printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

            flags.add(new JLabel("::  Flags"));
            readable = new JCheckBox("Read  ");
            readable.setMnemonic('a');
            flags.add(readable);

            writable = new JCheckBox("Write  ");
            writable.setMnemonic('w');
            flags.add(writable);

            executable = new JCheckBox("Execute");
            executable.setMnemonic('x');
            flags.add(executable);

            int count = fileDetailsLabels.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                fileDetailsLabels.getComponent(ii).setEnabled(true);
            }

            count = flags.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                flags.getComponent(ii).setEnabled(false);
            }

            JPanel fileView = new JPanel(new BorderLayout(3,3));

            fileView.add(toolBar,BorderLayout.NORTH);
            fileView.add(fileMainDetails,BorderLayout.CENTER);

            detailView.add(fileView, BorderLayout.SOUTH);
            
            /*FOR THE CLIENT*/
         // details for a File
            JPanel clientFileMainDetails = new JPanel(new BorderLayout(4,2));
            clientFileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel clientFileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            clientFileMainDetails.add(clientFileDetailsLabels, BorderLayout.WEST);

            JPanel clientFileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
            clientFileMainDetails.add(clientFileDetailsValues, BorderLayout.CENTER);

            clientFileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            clientFileName = new JLabel();
            clientFileDetailsValues.add(clientFileName);
            clientFileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
            clientPath = new JTextField(5);
            clientPath.setEditable(false);
            clientFileDetailsValues.add(clientPath);
            clientFileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
            clientDate = new JLabel();
            clientFileDetailsValues.add(clientDate);
            clientFileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
            clientSize = new JLabel();
            clientFileDetailsValues.add(clientSize);
            clientFileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

            JPanel clientFlags = new JPanel(new FlowLayout(FlowLayout.LEADING,4,0));

            clientIsDirectory = new JRadioButton("Directory");
            clientFlags.add(clientIsDirectory);

            clientIsFile = new JRadioButton("File");
            clientFlags.add(clientIsFile);
            clientFileDetailsValues.add(clientFlags);

            JToolBar clientToolBar = new JToolBar();
            // mnemonics stop working in a floated toolbar
            clientToolBar.setFloatable(false);

            JButton clientLocateFile = new JButton("Locate");
            clientLocateFile.setMnemonic('l');

            clientLocateFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Locate: " + clientCurrentFile.getParentFile());
                        desktop.open(clientCurrentFile.getParentFile());
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            clientToolBar.add(clientLocateFile);

            clientOpenFile = new JButton("Open");
            clientOpenFile.setMnemonic('o');

            clientOpenFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Open: " + clientCurrentFile);
                        desktop.open(clientCurrentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            clientToolBar.add(clientOpenFile);

            clientEditFile = new JButton("Edit");
            clientEditFile.setMnemonic('e');
            clientEditFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.edit(clientCurrentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            clientToolBar.add(clientEditFile);

            clientPrintFile = new JButton("Print");
            clientPrintFile.setMnemonic('p');
            clientPrintFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.print(clientCurrentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            clientToolBar.add(clientPrintFile);
            
            clientMove = new JButton("Download"); //creates a download folder to download every shit there is there.
            
            clientMove.setMnemonic('d');
            clientMove.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.print(clientCurrentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            clientToolBar.add(clientMove);

            // Check the actions are supported on this platform!
            clientOpenFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            clientEditFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
            clientPrintFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

            clientFlags.add(new JLabel("::  Flags"));
            clientReadable = new JCheckBox("Read  ");
            clientReadable.setMnemonic('a');
            clientFlags.add(clientReadable);

            clientWritable = new JCheckBox("Write  ");
            clientWritable.setMnemonic('w');
            clientFlags.add(clientWritable);

            clientExecutable = new JCheckBox("Execute");
            clientExecutable.setMnemonic('x');
            clientFlags.add(clientExecutable);

            int clientCount = clientFileDetailsLabels.getComponentCount();
            for (int ii=0; ii<clientCount; ii++) {
                clientFileDetailsLabels.getComponent(ii).setEnabled(true);
            }

            clientCount = clientFlags.getComponentCount();
            for (int ii=0; ii<clientCount; ii++) {
                clientFlags.getComponent(ii).setEnabled(false);
            }

            JPanel clientFileView = new JPanel(new BorderLayout(3,3));

            clientFileView.add(clientToolBar,BorderLayout.NORTH);
            clientFileView.add(clientFileMainDetails,BorderLayout.CENTER);

            clientDetailView.add(clientFileView, BorderLayout.SOUTH);
            /*FOR THE CLIENT*/

//            JSplitPane splitPane = new JSplitPane(
//                JSplitPane.HORIZONTAL_SPLIT,
//                treeScroll,
//                detailView);
//            
//            JSplitPane splitPane1 = new JSplitPane(
//                    JSplitPane.HORIZONTAL_SPLIT,
//                    treeScroll1,
//                    splitPane);
            
            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    treeScroll,
                    treeScroll1);
                
                JSplitPane splitPane1 = new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        detailView,
                        clientDetailView);
            
            JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, splitPane1);
            gui.add(splitPane2, BorderLayout.CENTER);
//            gui.add(splitPane1, BorderLayout.CENTER);

//            gui.add(treeScroll, BorderLayout.WEST);

            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);

        }
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		JScrollPane scrollPane = new JScrollPane();		
//		scrollPane.setLocation(10, 10);
//        return gui;
		scrollPane.getViewport().add(gui);
		scrollPane.setSize((int)screenSize.getWidth(), gui.getHeight());
        return scrollPane;
    }
    
    public void addClientRoots(DefaultMutableTreeNode clientRoot) { 
    	System.out.println("ENTERED ADDCIENTROOTS!");
    	try{
		byte[] temp = {(byte)2}; //SEND COMMAND TO GET ROOT FILES !
		commandOut.write(temp, 0, 1);
		commandOut.flush();
    	System.out.println("MY DICK IS ON FIRE!444444");

//    	BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataIn));
//    	DataInputStream inputReader = new DataInputStream(dataIn);
//    	String line;
    	ArrayList<File> clientRoots = new ArrayList<File>();
    	System.out.println("MY DICK IS ON FIRE!");
//    	try{
//    		objectReader = new ObjectInputStream(dataIn);
//    	} catch(Exception e) {
////    		objectReader = new ObjectInputStream(dataIn);
//
//    	}
    	try {
		objectReader = new ObjectInputStream(dataIn);
    	} catch (Exception e) {
    		System.out.println("I FUCK HORSES!");
    		e.printStackTrace();
			closeWindow();
    	}
    	int numFiles;
    	if(objectReader != null) {
    		numFiles = objectReader.readInt(); //read number of files !
    	} else {
    		return;
    	}
//    	int numFiles = objectReader.readInt(); //read number of files !
        setVisible(true);

    	for(int i = 0; i < numFiles; i++) {
    		clientRoots.add((FilePlus)objectReader.readObject());
    	}
//		while(!((line = dataReader.readLine()).equals("end")))  {
//	    	System.out.println("TURN AROUND THUNDER!!!!");
//
//			System.out.println("FILE NAME: " + line);
//			clientRoots.add(new ClientFile(line, dataReader.readLine(), (dataReader.readLine().equals("1")) ? true : false));
//		}
		for(File fileSystemRoot : clientRoots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            clientRoot.add(node);
            //NOW ADD CHILDREN OF THESE NODES ! SEND ABSOLUTE FILE OBVIOUSLY !
            temp[0] = (byte)3; //SEND COMMAND TO GET CHILDREN FOR THIS NODE
    		commandOut.write(temp, 0, 1); //SEND 3 WHICH IS GET NODES OMFG !
    		commandOut.flush();
    		writeStringOutStream(dataOut, fileSystemRoot.getPath()); //WRITES PATH FILE TO THE NODE !
        	ArrayList<File> nodeChildren = new ArrayList<File>();
        	int numChildFiles = objectReader.readInt(); //RETURNS NUM CHILDSS :O
        	for(int i = 0; i < numChildFiles; i++) {
        		nodeChildren.add((File)objectReader.readObject());
        	}
//    		while(!((line = dataReader.readLine()).equals("end")))  {
//    			System.out.println("FILE NAME: " + line);
//    			nodeChildren.add(new ClientFile(line, dataReader.readLine(), (dataReader.readLine().equals("1")) ? true : false));
//    		}
    		for(File file : nodeChildren) {
    			if(file.isDirectory()) {
    				node.add(new DefaultMutableTreeNode(file));
    			}
    		}
		}
//        File[] clientRoots = fileSystemView.getRoots();
//        for (File fileSystemRoot : clientRoots) {
//            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
//            clientRoot.add( node );
//            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    node.add(new DefaultMutableTreeNode(file));
//                }
//            }
//            //
//        }
    	} catch(Exception e) {
    		e.printStackTrace();
//			officialFrame.dispatchEvent(new WindowEvent(officialFrame, WindowEvent.WINDOW_CLOSING));
//    		System.exit(0);
    	}
    }
    
    public void closeWindow()
    {
        if(this != null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    fb.dispatchEvent(new WindowEvent(fb, WindowEvent.WINDOW_CLOSING));
                }
            });
        }
    }
    
	//writes a string char by char to outputstream
//	public static void writeStringOutStream(ObjectOutputStream out, String line) {
	public static void writeStringOutStream(OutputStream out, String line) {
//		if(line == null) {
//			line = "null";
//		}
		byte[] convert = stringToByteArray(line);
		try {
			out.write(convert, 0, line.length() + 1);
			out.flush();
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

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0,0);
    }

    @SuppressWarnings("unused")
	private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile==find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    @SuppressWarnings("unused")
	private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
            gui,
            errorMessage,
            errorTitle,
            JOptionPane.ERROR_MESSAGE
            );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
            gui,
            t.toString(),
            t.getMessage(),
            JOptionPane.ERROR_MESSAGE
            );
        gui.repaint();
    }

    /** Update the table on the EDT */
    private void setTableData(final File[] files) {
    	final FileBrowser fb = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel==null) {
                    fileTableModel = new FileTableModel(fb);
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );

                    setColumnWidth(0,-1);
                    setColumnWidth(3,60);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4,-1);
                    setColumnWidth(5,-1);
                    setColumnWidth(6,-1);
                    setColumnWidth(7,-1);
                    setColumnWidth(8,-1);
                    setColumnWidth(9,-1);

                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }
    
    /** Update the table on the EDT */
    private void setClientTableData(final File[] files) {
    	final FileBrowser fb = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (clientFileTableModel==null) {
                    clientFileTableModel = new ClientFileTableModel(fb);
                    clientTable.setModel(clientFileTableModel);
                }
                clientTable.getSelectionModel().removeListSelectionListener(clientListSelectionListener);
                clientFileTableModel.setFiles(files);
                clientTable.getSelectionModel().addListSelectionListener(clientListSelectionListener);
                if (!clientCellSizesSet) {
                	FilePlus temp = (FilePlus)files[0];
                	Icon icon = temp.getIcon();
//                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    clientTable.setRowHeight( icon.getIconHeight() + clientRowIconPadding);

                    setClientColumnWidth(0,-1);
                    setClientColumnWidth(3,60);
                    clientTable.getColumnModel().getColumn(3).setMaxWidth(120);
                    setClientColumnWidth(4,-1);
                    setClientColumnWidth(5,-1);
                    setClientColumnWidth(6,-1);
                    setClientColumnWidth(7,-1);
                    setClientColumnWidth(8,-1);
                    setClientColumnWidth(9,-1);

                    clientCellSizesSet = true;
                }
            }
        });
    }

    private void setClientColumnWidth(int column, int width) {
        TableColumn tableColumn = clientTable.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels for the SwingWorker fix. */
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
//                    if (node.isLeaf()) { //MAYBE NEED TO NOT HAVE CHILDREN?
//                    System.out.println("\n\n\n");
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
//                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
            	System.out.println("cool story bro !");
            	node.removeAllChildren();
//            	int i = 0;
                for (File child : chunks) {
                	System.out.println("FILE NAME: " + child.getName());
                    node.add(new DefaultMutableTreeNode(child));
//                    treeModel.insertNodeInto(new DefaultMutableTreeNode(child), node, i);
//                    i++;
                }
                treeModel.nodeStructureChanged(node);
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }
    
	
    private void showClientChildren(final DefaultMutableTreeNode node) {
        clientTree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
            	System.out.println("I AM CRISTIANO RONALDO!");
//            	BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataIn));
//            	ObjectInputStream objectReader = null;
            	System.out.println("I AM messi!");
//				try {
//	            	System.out.println("I AM DWIGHT HOWRD!");
//
////					objectReader = new ObjectInputStream(dataIn);
//	            	System.out.println("I AM SO BUFF!!");
//
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//	            	System.out.println("CUM ALL OVER!");
//
//					e1.printStackTrace();
//				}
            	System.out.println("MY HORSE DANCED ON MY CHAIR!");
//            	String line;
            	byte[] temp = {(byte)3};
                FilePlus file = (FilePlus) node.getUserObject();
                if (file.isDirectory()) {
//Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
//                if (file.isDirectory() & file.hasChildren()) {

//                if (file.isDirectory() & hasChildren(file)) {
                	ArrayList<File> nodeChildren = null;
                	try {
            		commandOut.write(temp, 0, 1); //SEND 3 WHICH IS GET NODES OMFG !
            		commandOut.flush();
            		writeStringOutStream(dataOut, file.getAbsolutePath()); //WRITES PATH FILE TO THE NODE !
                	nodeChildren = new ArrayList<File>();
                	System.out.println("STROLL AROUND TOWN");
//        	    	objectReader = new ObjectInputStream(dataIn);
                	int numFiles = objectReader.readInt();
                	System.out.println("GAS STATION");

                	System.out.println("NUM FILES: " + numFiles);
                	for(int i = 0; i < numFiles; i++) {
                		nodeChildren.add((File)objectReader.readObject());
                	}
//            		while(!((line = dataReader.readLine()).equals("end")))  {
//            			System.out.println("BOOB JOB FILE NAME: " + line);
//            			nodeChildren.add(new ClientFile(line, dataReader.readLine(), (dataReader.readLine().equals("1")) ? true : false));
//            		}
            		for(File clientFile : nodeChildren) {
            			if(clientFile.isDirectory()) {
            				publish(clientFile);
            			}
            		}
                	} catch(Exception e) {

                		e.printStackTrace();
//            			officialFrame.dispatchEvent(new WindowEvent(officialFrame, WindowEvent.WINDOW_CLOSING));

                	}
                   setClientTableData(nodeChildren.toArray(new File[nodeChildren.size()]));

                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
            	if(chunks == null) {
            		return;
            	}
            	node.removeAllChildren();
//            	int i = 0;
                for (File child : chunks) {
//                	System.out.println("FILE NAME: " + child.getName());
                    node.add(new DefaultMutableTreeNode(child));
//                    clientTreeModel.insertNodeInto(new DefaultMutableTreeNode(child), node, i);
//                    i++;
                }
                clientTreeModel.nodeStructureChanged(node);
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                clientTree.setEnabled(true);
            }
        };
        worker.execute();
    }
    
    //checks if directory has children or not. 
    //true if yes false if no
    public boolean hasChildren(File directory) {
    	String[] directories = directory.list();
    	return directories.length > 0;
    }

    //IMPORTANTE!
    /** Update the File details view with the details of this File. */
    protected void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                APP_TITLE +
                " :: " +
                fileSystemView.getSystemDisplayName(file) );
        }

        gui.repaint();
    }
    
    //IMPORTANTE!
    /** Update the File details view with the details of this File. */
    protected void setClientFileDetails(File file) {
        clientCurrentFile = file;
        FilePlus temp = (FilePlus) file;
        Icon icon = temp.getIcon();
        clientFileName.setIcon(icon);
        clientFileName.setText(temp.getDisplayName());
        clientPath.setText(temp.getPath());
        clientDate.setText(new Date(temp.lastModified()).toString());
        clientSize.setText(temp.length() + " bytes");
        clientReadable.setSelected(temp.canRead());
        clientWritable.setSelected(temp.canWrite());
        clientExecutable.setSelected(temp.canExecute());
        clientIsDirectory.setSelected(temp.isDirectory());

        clientIsFile.setSelected(temp.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                APP_TITLE +
                " :: " +
                temp.getDisplayName() );
        }

        gui.repaint();
    }
    
    //CONSTRUCTOR!
    public FileBrowser(ClientInfo requestedClient, WindowAdapter windowEventClose) {
    	
    	super(APP_TITLE);
    	System.out.println("WE ALL GOTTA CHILL BRUTHA");
    	try {
			this.commandOut = requestedClient.getSubSocketInfo("fileManagerCommand").getSocket().getOutputStream();
	    	this.dataIn = requestedClient.getSubSocketInfo("fileManagerData").getSocket().getInputStream();
	    	this.dataOut = requestedClient.getSubSocketInfo("fileManagerData").getSocket().getOutputStream();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
            // Significantly improves the look of the output in
            // terms of the file names returned by FileSystemView!
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception weTried) {
//        	return;
        }
    	
    	addWindowListener(windowEventClose);
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(0, 500);
        setContentPane(getGui());
//        f.setLocation(0, 500);
    	System.out.println("home homes HOMES HOMES ");

        
//        f.setResizable(true);

        try {
            URL urlBig = this.getClass().getResource("fb-icon-32x32.png");
            URL urlSmall = this.getClass().getResource("fb-icon-16x16.png");
            ArrayList<Image> images = new ArrayList<Image>();
            images.add( ImageIO.read(urlBig) );
            images.add( ImageIO.read(urlSmall) );
            setIconImages(images);
        } catch(Exception weTried) {
//        	return;
        }

        pack();
        try{
        	setLocationByPlatform(true);
        } catch(Exception e) {
        	System.out.println("HUEHUEHUE !");
        	e.printStackTrace();
        }
//      f.setMinimumSize(f.getSize());
//        setVisible(true);

        showRootFile();
        System.out.println("THERE GOES THE WIND!");
    	
//    	this.officialFrame = officialFrame;
//    	this.windowEventClose = windowEventClose;
//    	this.requestedClient = requestedClient 	
    }
    
    public FileBrowser() {
    	
    }

//    public void run() {
//        SwingUtilities.invokeLater(new Runnable() { //RUNS ON THE EVENDISPATCHTHREAD NOW ! :o
//            public void run() {
//                try {
//                    // Significantly improves the look of the output in
//                    // terms of the file names returned by FileSystemView!
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch(Exception weTried) {
////                	return;
//                }
//                JFrame f = new JFrame(APP_TITLE);
//
//                f.addWindowListener(windowEventClose);
//                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                f.setLocation(0, 500);
//                
//                FileBrowser fileBrowser = new FileBrowser(requestedClient, windowEventClose, f);
//                f.setContentPane(fileBrowser.getGui());
////                f.setLocation(0, 500);
//
//                
////                f.setResizable(true);
//
//                try {
//                    URL urlBig = fileBrowser.getClass().getResource("fb-icon-32x32.png");
//                    URL urlSmall = fileBrowser.getClass().getResource("fb-icon-16x16.png");
//                    ArrayList<Image> images = new ArrayList<Image>();
//                    images.add( ImageIO.read(urlBig) );
//                    images.add( ImageIO.read(urlSmall) );
//                    f.setIconImages(images);
//                } catch(Exception weTried) {
////                	return;
//                }
//
//                f.pack();
//                f.setLocationByPlatform(true);
////                f.setMinimumSize(f.getSize());
//                f.setVisible(true);
//
//                fileBrowser.showRootFile();
//                System.out.println("I'M DONE MOTHERFUCKER!");
//            }
//        });
//    }
}

/** A TableModel to hold File[]. */
class FileTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FileBrowser fb;
    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Icon",
        "File",
        "Path/name",
        "Size",
        "Last Modified",
        "R",
        "W",
        "E",
        "D",
        "F",
    };

    FileTableModel() {
//        this(new File[0]);
    }
    
    FileTableModel(FileBrowser fb) {
        this(new File[0]);
        this.fb = fb;
    }

    FileTableModel(File[] files) {
        this.files = files;
    }
    
    public boolean isCellEditable(int row, int col)
    { if(col == 1 || (col >= 5 && col <= 7)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
    public void setValueAt(Object value, int row, int column) {
        File file = files[row];
        switch (column) {
            case 1:
        		File newFile =new File(file.getParent(), (String)value);
        		files[row] = newFile;
        		if(file.renameTo(newFile)) {
            		System.out.println("SUCCESSFULLY RENAMED!");
        		} else {
            		System.out.println("FAILED RENAME!");
        		}
        		fb.setFileDetails(files[row]);
        		break;
            case 5:
            	file.setReadable((boolean)value);
        		fb.setFileDetails(files[row]);
            	break;
            case 6:
            	file.setWritable((boolean)value);
        		fb.setFileDetails(files[row]);
            	break;
            case 7:
            	file.setExecutable((boolean)value);
        		fb.setFileDetails(files[row]);
            	break;
            default:
                System.err.println("Logic Error");
        }
//        fireTableCellUpdated(row, column); //NOTIFIES ALL LISTENERS AND I MEAN ALL LISTENERS!
      }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}

/** A TableModel to hold File[]. */
class ClientFileTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FileBrowser fb;
    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Icon",
        "File",
        "Path/name",
        "Size",
        "Last Modified",
        "R",
        "W",
        "E",
        "D",
        "F",
    };

    ClientFileTableModel() {
//        this(new File[0]);
    }
    
    ClientFileTableModel(FileBrowser fb) {
        this(new File[0]);
        this.fb = fb;
    }

    ClientFileTableModel(File[] files) {
        this.files = files;
    }
    
    public boolean isCellEditable(int row, int col)
    { if(col == 1 || (col >= 5 && col <= 7)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
    public void setValueAt(Object value, int row, int column) {
        File file = files[row];
        switch (column) {
            case 1:
        		File newFile =new File(file.getParent(), (String)value);
        		files[row] = newFile;
        		if(file.renameTo(newFile)) {
            		System.out.println("SUCCESSFULLY RENAMED!");
        		} else {
            		System.out.println("FAILED RENAME!");
        		}
        		fb.setClientFileDetails(files[row]);
        		break;
            case 5:
            	file.setReadable((boolean)value);
        		fb.setClientFileDetails(files[row]);
            	break;
            case 6:
            	file.setWritable((boolean)value);
        		fb.setClientFileDetails(files[row]);
            	break;
            case 7:
            	file.setExecutable((boolean)value);
        		fb.setClientFileDetails(files[row]);
            	break;
            default:
                System.err.println("Logic Error");
        }
//        fireTableCellUpdated(row, column); //NOTIFIES ALL LISTENERS AND I MEAN ALL LISTENERS!
      }

    public Object getValueAt(int row, int column) {
        File file = files[row];

    	try {
        FilePlus temp = (FilePlus)file;
        switch (column) {
            case 0:
                return temp.getIcon();
            case 1:
                return temp.getName();
            case 2:
                return temp.getPath();
            case 3:
                return temp.length();
            case 4:
                return temp.lastModified();
            case 5:
                return temp.canRead();
            case 6:
                return temp.canWrite();
            case 7:
                return temp.canExecute();
            case 8:
                return temp.isDirectory();
            case 9:
                return temp.isFile();
            default:
                System.err.println("Logic Error");
        } 
    	}catch(ClassCastException e) {
    	       switch (column) {
               case 0:
                   return fileSystemView.getSystemIcon(file);
               case 1:
                   return fileSystemView.getSystemDisplayName(file);
               case 2:
                   return file.getPath();
               case 3:
                   return file.length();
               case 4:
                   return file.lastModified();
               case 5:
                   return file.canRead();
               case 6:
                   return file.canWrite();
               case 7:
                   return file.canExecute();
               case 8:
                   return file.isDirectory();
               case 9:
                   return file.isFile();
               default:
                   System.err.println("Logic Error");
           }
        	
        } catch(Exception e) {
        	System.out.println("shut the front door !");
        }
        
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}

/** A TreeCellRenderer for a File. */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FileSystemView fileSystemView;

    private JLabel label;

    FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        File file = (File)node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
//        System.out.println("WE'RE HERE OOPS ! NAME: " + fileSystemView.getSystemDisplayName(file));

        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}

/** A TreeCellRenderer for a File. */
class ClientFileTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private FileSystemView fileSystemView;

    private JLabel label;

    ClientFileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        FilePlus file = (FilePlus)node.getUserObject();
//        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setIcon(file.getIcon());
        label.setText(file.getDisplayName());
//        System.out.println("WE'RE HERE OOPS ! NAME: " + fileSystemView.getSystemDisplayName(file));

        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}
