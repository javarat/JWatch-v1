
public class ClientFile {
	
	private String name;
	private String path; //string of absolute path of file
	private boolean isDirectory;
	
	public ClientFile(String name, String path, boolean isDirectory) {
		this.name = name;
		this.path = path;
		this.isDirectory = isDirectory;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public String toString() {
		return name;
	}
}
