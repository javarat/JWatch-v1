import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

	public class FilePlus extends File {
/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
		private boolean isDirectory;
		private ImageIcon icon;
		private String displayName;
		private long date;
		private long length;
		private boolean canRead;
		private boolean canWrite;
		private boolean canExecute;
		private boolean isFile;
//		private String absolutePath;
//		private String path;
		
		public FilePlus(File file) {
			super(file.getAbsolutePath());
			isDirectory = super.isDirectory();
			displayName = FileSystemView.getFileSystemView().getSystemDisplayName(file);
			icon = new ImageIcon(iconToImage(FileSystemView.getFileSystemView().getSystemIcon(file)));
			date = super.lastModified();
			length = super.length();
			canRead = super.canRead();
			canWrite = super.canWrite();
			canExecute = super.canExecute();
			isFile = super.isFile();
//			absolutePath = super.getAbsolutePath();
//			path = super.getPath();
//			setHasChildren();
//			hasChildren = hasChildren();
		}
		
//		public String getPath() {
//			return path;
//		}
//		
//		public String getAbsolutePath() {
//			return absolutePath;
//		}
		
		public boolean isFile() {
			return isFile;
		}
		
		public boolean canRead() {
			return canRead;
		}
		
		public boolean canWrite() {
			return canWrite;
		}
		
		public boolean canExecute() {
			return canExecute;
		}
		
		public long lastModified() {
			return date;
		}
		
		public long length() {
			return length;
		}
		
		public boolean isDirectory() {
			return isDirectory;
		}
		
		public Icon getIcon() {
			return icon;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		 static Image iconToImage(Icon icon) {
			          if (icon instanceof ImageIcon) {
		               return ((ImageIcon)icon).getImage();
			           } else {
			               int w = icon.getIconWidth();
			               int h = icon.getIconHeight();
			               GraphicsEnvironment ge = 
			                 GraphicsEnvironment.getLocalGraphicsEnvironment();
			               GraphicsDevice gd = ge.getDefaultScreenDevice();
			               GraphicsConfiguration gc = gd.getDefaultConfiguration();
			               BufferedImage image = gc.createCompatibleImage(w, h);
			               Graphics2D g = image.createGraphics();
			               icon.paintIcon(null, g, 0, 0);
			               g.dispose();
			               return image;
			           }
			       }
		
	}