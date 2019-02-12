import java.util.List;

/*
 * Object that handles the mkdir command
 * 
 * Important notes concerning the constructor variables:
 * 
 * this.curr -- used as a temporary variable to store 
 * each directory we reach as we step through a given path.
 * This variable is very important for the traverse method.
 * 
 * this.parent -- is not always the parent of "this.curr".
 * It is the parent when we want to check if it can hold a potential
 * file or directory name without duplication.
 * It is the current directory itself in the rare case we want to 
 * make the current directory a child of itself.
 * It is this.curr's parent directory in the rare case we want to make 
 * the parent directory a child of this.curr.
 * This variable is not really relevant to the traverse method.
 * It is important for the hasDuplication method. 
 * 
 */

public class Mkdir {
	
	public Directory curr;
	public Directory parent;
	
	// Constructor
	public Mkdir(Directory currDir) {
		
		this.curr = currDir;
		this.parent = null;  
	}
	
	// Check if path is valid to create a new directory
	public boolean validatePath(String[] cmd, FileSystem fs) {
		
		int valid = 0; 
		// Corner case: Check if root symbol given
		if (cmd.length == 1 && cmd[0].equals("/")) {
			this.curr = null;
			return true;
		}
		
		this.parent = this.curr.getParent();
		valid = this.traverse(this.curr, valid, cmd);
		
		this.curr = fs.getCurr(); // Reset for the next path
		
		if (valid < cmd.length) {
			return false;
		}
		return true;
	}

	
	// Recursive function that returns the value of valid after traversing the 
	// given path up to but excluding the last subdirectory.
	// Each existing subdirectory adds 1 to the valid variable.
	public int traverse(Directory d, int valid, String[] cmd) {
		
		// Base case 1: We've reached the last subdirectory in path
		if (valid == cmd.length) {
			return valid;
		// Base case 2: Reached an empty directory and it's the root
		} else if ((d.getParent() == null) && d.getDirs().isEmpty() && 
				d.getFiles().isEmpty()) {
			this.parent = d;
			return valid + 1;
			
		// Base case 3: Reached an empty directory, not at the root and has 
		// already traversed at least once
		} else if ((valid > 0) && d.getDirs().isEmpty() && 
				d.getFiles().isEmpty()) {
			this.parent = d;
			return valid + 1;
			
		// Base case 4: Found no existing directory with the given pathname
		// and has already traversed at least once
		} else if ((valid > 0) && (d.equals(this.parent))) {
			return valid;
	
		} else {
			if (cmd[valid].equals(".")) { 
				// Allows the hasDuplication method to correctly identify the 
				// parent of the new dir
				this.parent = d; 
			} else if (cmd[valid].equals("..")) { 
				// Do nothing
			} else {
				this.parent = d; // Remember current directory
				// Look into current directory, see if we can go farther 
				// down path
				List<Directory> dir = d.getDirs();
				for (Directory item : dir) {
					String n = item.getName();
					if (n.equals(cmd[valid])) {
						d = item; // Update current directory
					}
				}
			}
		}
		return this.traverse(d, valid + 1, cmd);
	}
	
	
	// Return the parent directory that contains the verified file or 
	// subdirectory identified in the validatePath call.
	public Directory getParent() {
		return this.parent;
	}
	
	// Check to make sure item does not already exist in directory and 
	// notify user of duplication if flag is set to 1
	public boolean hasDuplication(String name, Directory tracker, int flag) {
		
		// Corner cases: Special symbols provided
		if ((tracker == null)) {
			if (name.equals("/")) {
				System.out.println(name + ": Reserved for the root directory"); 
				return true;
			} else {
				System.out.println("/ : Already exists");
				return true;
			}
		}
		
		if (name.equals(".") || name.equals("..")) {
			System.out.println(tracker.getName() + ": Already exists");
			return true;
		}
		
		// Other cases
		for (Directory d:tracker.getDirs()) {
			if (d.getName().equals(name)) {
				if (flag == 1) {
					System.out.println(name + ": Already exists");
				}
				return true;
			}	 
		}
	
		for (File f:tracker.getFiles()) {
			if (f.getName().equals(name)) {
				if (flag == 1) {
					System.out.println(name + ": Already exists");
				}
				return true;
			} 
		}
	
		return false;
	}
	
	
	public void run(String[] cmd, FileSystem fs) {
		String[] path;
		
		if (cmd.length == 1) { // Missing method arguments
			System.out.println(cmd[0] + ": Please provide directory path(s)"
					+ " ");
		}
		
		// Check if each argument is valid path and set up directory
		for (int i = 1; i < cmd.length; i += 1) {
			path = fs.splitPath(cmd[i]);
			if (this.validatePath(path, fs)) {
				Directory p = this.getParent();
					
				int index = Math.max(0, path.length-1);
				if (this.hasDuplication(path[index], p, 1)) {
					continue;
				} else {
					Directory d = new Directory(path[index]);
					p.setChild(d);
					d.setParent(p);
					d.setPath();
				}	
			} else {
				System.out.println(cmd[i] + ": Need an existing path to the "
						+ "new directory");
			}
		}
	}
}
