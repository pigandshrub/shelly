import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 
 * The Directory object.
 */

public class Directory {
	
	private String name;
	private String path;
	private Directory parent;
	private List<Directory> dirs;
	private List<File> files;
	
	public Directory(String name) {
		this.name = name;
		this.parent = null;
		this.dirs = new ArrayList<Directory>();
		this.files = new ArrayList<File>();
		this.path = "";
	}
	
	// Returns the name of the directory
	public String getName() {
		return this.name;
	}
	
	// Get list of directory children
	public List<Directory> getDirs() {
		return this.dirs;
	}
	
	// Get list of file children
	public List<File> getFiles() {
		return this.files;
	}
	
	// Get current parent
	public Directory getParent() {
		return this.parent;
	}
		
	// Add or replace parent
	public void setParent(Directory d) {
		this.parent = d;
	} 

	// Add directory child
	public Directory setChild(Directory d) {
		this.dirs.add(d);
		return this;
	}
	
	// Add file child
	public void setChild(File f) {
		this.files.add(f);
	}
	
	// Clear children directories
	public void emptyDirs() {
		this.dirs.clear();
	}
	
	// Clear children files
	public void emptyFiles() {
		this.files.clear();
	}
	
	// Get the full path of the directory
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
			this.path = this.parent.path + "/" + this.name;
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
	
	
	
	// Returns either a new file or the directory object that this method 
	// belongs to. Takes an input file or directory and creates a copy of 
	// it inside the directory object that this method belongs to.
	public Object traverseAndCopy(Object o) {
			
		if (o instanceof File) {
			File f = new File(((File) o).getName());
			for (String line:((File) o).getContent()) {
				f.addContent(line);
			}
			f.setParent(this);
			f.setPath();
			this.setChild(f);
			
		} else {	
			if ( ((Directory) o).dirs.isEmpty() && 
					((Directory) o).files.isEmpty() ) {
				return this;
			}
	
			for (Directory dir:((Directory) o).dirs) {
				Directory d = new Directory(((Directory) dir).name);
				d.setParent(this);
				d.setPath();
				this.setChild((Directory) d.traverseAndCopy((Object) dir));
			}
			
			for (File file:((Directory) o).getFiles()) {
				File f = new File(file.getName());
				for (String line:file.getContent()) {
					f.addContent(line);
				}
				f.setParent(this);
				f.setPath();
				this.setChild(f);
			}
		}
		return this;
	}
	
	
	
	// Removes everything in the directory object that this method belongs to.
	public void traverseAndRemove(String curr) {
	
		while (!this.files.isEmpty()) {
			File f = this.files.get(0);
			System.out.println(f.getLocalPath(curr) + ": Has been deleted");
			f.setParent(null);
			f.clearContent();
			f.setPath();
			this.files.remove(f);
		}
		
		while (!this.dirs.isEmpty()) {
			Directory d = this.dirs.get(0);
			d.traverseAndRemove(curr);
			System.out.println(d.getLocalPath(curr) + ": Has been deleted");
			d.setParent(null);
			d.setPath();
			this.dirs.remove(d);
		}
		
	}
}
