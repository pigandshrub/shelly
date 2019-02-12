
/*
 * Object that handles the Rm command
 */

public class Rm {
	
	public Directory curr;
	
	public Rm(Directory currDir) {
		this.curr = currDir;
	}
	
	public void run(String[] cmd, FileSystem fs) {
		// Only command name is given
		if (cmd.length == 1) {
			System.out.println(cmd[0] + ": Requires at least one "
					+ "file or directory path");
		
		// Command line ends with a -R
		} else if (cmd[cmd.length - 1].equals("-R")) { 
			System.out.println(cmd[0] + ": -R is not a valid file or dir path");
		
		} else {
			int flag = 0; // Track the -R
			for (int i = 1; i < cmd.length; i += 1) {
				
				// -R given and used correctly
				if (i == 1 && cmd[i].equals("-R")) {
					flag = 1;
					
				} else { // A path is given to remove
					String[] path = fs.splitPath(cmd[i]); 
					Object o = fs.findObj(path, fs.getCurr()); 
					this.removeObject(o, flag); 
				}
				
			}
		}
	}
	
	
	
	public Object removeObject(Object o, int flag) {
		
		if (o instanceof File) {
			
			// Remove file from the parent
			Directory p = ((File) o).getParent();
			if (p != null) {
				p.getFiles().remove(o);
			}
			// Remove parent and content from file
			((File) o).setParent(null);
			((File) o).clearContent();
			System.out.println("Original file '" + ((File) o).getName() + 
					"' has been deleted");
			// Set file to null and return it
			o = null;
			return o;
			
		} else if (o instanceof Directory) { 
			
			// -R was not given
			if (flag == 0) {
				System.out.println("rm: " + ((Directory) o).getName() +
						": is a directory. Use rm -R if you want to remove " 
						+ "this entire directory.");
			
			// We are trying to remove the current directory or its ancestors
			} else if (this.inside((Directory) o)) { 
				System.out.println("rm: " + "Cannot remove the directory " + 
						"because it is currently in use");
				
			
			} else { 
				Directory d = (Directory) o;
				Directory p = d.getParent();
				d.traverseAndRemove(d.getName());
				p.getDirs().remove(d);
				System.out.println("Original directory '" + 
						d.getLocalPath(d.getName()) + 
						"' has been successfully removed");
			}
			
		} else {
			System.out.println("Error: Unknown object path provided");
		}
		return this;
	}
	
	
	
	// Check if a directory is currently in use
	public boolean inside(Directory d) {
		
		// If d is the current directory you're in
		if (this.curr.equals(d)) {
			return true;
		}
		// If d is an ancestor of the current directory you're in
		while (this.curr.getParent() != null) {
			if (d.equals(this.curr)) {
				return true;
			}
			this.curr = this.curr.getParent();
		}
		return false;
	}

	
}
