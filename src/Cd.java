/*
 * Object that handles the cd command
 */

public class Cd {
	
	public Cd() {
	}
	
	public Directory run(String[] cmd, FileSystem fs) {
		String[] path;
		
		// Take only the first directory path provided and
		// ignore any other arguments provided.
		
		// If only "cd" given or "/" given as the directory path, set path 
		// to "/"
		if (cmd.length == 1 || cmd[1].equals("/")) {
			path = new String[1];
			path[0] = "/";
		} else { // Otherwise, separate path into its diff destinations
			path = fs.splitPath(cmd[1]);
		}
		
		Object o = fs.findObj(path, fs.getCurr());
		
		if (o instanceof File) {
			System.out.println(((File) o).getName() + " is not a "
					+ "directory");
		} else if (o instanceof Directory) {
			fs.setCurr((Directory) o);
		} else {
			System.out.println(cmd[0] + " " + cmd[1] + ": No such "
					+ "file or directory");
		}
		
		return fs.getCurr();
	}
}