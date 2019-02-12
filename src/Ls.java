import java.util.ArrayList;
import java.util.List;

/*
 * Object that handles the ls command
 */
public class Ls {

	public List<Directory> dirs;
	public List<File> files;
	public List<Object> others;
	
	// simple constructor
	public Ls() {
	}
	
	// For variable objects
	public Ls(List<Object> objs) {
		this.dirs = new ArrayList<Directory>();
		this.files = new ArrayList<File>();
		this.others = new ArrayList<Object>();
		
		for (Object o:objs) {
			if (o instanceof Directory) {
				this.dirs.add((Directory) o);
			} else if (o instanceof File) {
				this.files.add((File) o);
			} else {
				this.others.add(o);
			}
		}
	}
	
	// Print out a list of directories
	public void listDir(List<Directory> dirs) {		
		for (Directory d:dirs) {
			System.out.println(" " + d.getName());
		}	
	}
	
	// Print out a list of files
	public void listFiles(List<File> files) {
		for (File f:files) {
			System.out.println(" " + f.getName());
		}
	}
	
	// Print out a list of others
	public void listOthers(List<Object> others) {
		for (Object o:others) {
			System.out.println("ls: " + o + ": No such file or "
					+ "directory");
		}
	}
	
	public void run(String[] cmd, FileSystem fs) {
		String[] path;
		Directory curr = fs.getCurr();
		
		if (cmd.length == 1) { // No arguments provided
			System.out.println(curr.getName() + ":");
			this.listDir(curr.getDirs());
			this.listFiles(curr.getFiles());
		} else {
			// Collect the objects linked to each given argument
			List<Object> objs = new ArrayList<Object>();
			for (int i = 1; i < cmd.length; i += 1) {
				path = fs.splitPath(cmd[i]);
				Object o = fs.findObj(path, curr);
				objs.add(o);
			}
			
			// Process argument objects
			Ls L = new Ls(objs);
			L.listOthers(L.others);
			for (Directory d:L.dirs) {
				System.out.println(d.getName() + ":");
				L.listDir(d.getDirs());
				L.listFiles(d.getFiles());
			}
			L.listFiles(L.files);
		}
		System.out.println();
		
	}

	
}
