import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 
 * The File object.
 */

public class File {
	
	private String name;
	private String path;
	private Directory parent;
	private List<String> content;

	// Constructor
	public File(String name) {
		this.parent = null;
		this.name = name;
		this.path = "";
		this.content = new ArrayList<String>();
	}
	
	// Returns the name of the file
	public String getName() {
		return this.name;
	}
	
	// Get the current parent
	public Directory getParent() {
		return this.parent;
	}
	
	// Replace current parent
	public void setParent(Directory d) {
		this.parent = d;
	}
	
	// Get file content
	public List<String> getContent() {
		return this.content;
	}
	
	// Clear file content
	public void clearContent() {
		this.content = new ArrayList<String>();
	}
	
	// Add file content
	public void addContent(String line) {
		this.content.add(line);
	}
	
	// Get the full path of the file 
	public String getPath() {
		return this.path;
	}
	
	
	// Compile to make path for the directory.
	// The parent must be assigned first in order for this method to work 
	// properly.
	public void setPath() {
		if (this.parent == null) {
			this.path = "/";
		} else if (this.parent.getName().equals("/")) {
			this.path = "/" + this.name;
		} else {
			this.path = this.parent.getPath() + "/" + this.name;
		}
	}
	
	
	// Get local path of the directory
	public String getLocalPath(String curr) {
		Pattern pat = Pattern.compile(".*\\/(" + curr + "\\/*.*)");
		Matcher mat = pat.matcher(this.getPath());
		if (mat.find()) {
			return mat.group(1);
		}
		return this.getPath();
	}
	
	
}
