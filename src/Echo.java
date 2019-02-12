import java.util.Scanner;

/*
 * Object that handles the echo command
 */

public class Echo {
	
	public String fname; 
	public String line;
	public int form;
	// Identifies the index where the pathname starts in the command line
	public int pathIndex; 
	
	// Constructor
	public Echo() {
		this.fname = "";
		this.line = "";
		this.form = 0;
		this.pathIndex = 0;
	}
	
	public boolean validateRequest(String[] cmd) {
		
		if (cmd.length == 1) {
			System.out.println(""); 
		
		// Check if a file pathname was given	
		} else if (cmd[cmd.length-1].matches(">*>$")) { 
			this.form = -1;
			System.out.println(cmd[0] + ": Missing file name");
			
		} else {
			for (String s:cmd) {
				if (s.equals(">")) {
					this.form = 1;
					// 2 = 1 for ">" and 1 for "echo"
					if (this.line.isEmpty()) {
						this.pathIndex = (this.line.split(" ")).length + 1;
					} else {
						this.pathIndex = (this.line.split(" ")).length + 2;
					}
					
				} else if (s.equals(">>")) {
					this.form = 2;
					// 2 = 1 for ">" and 1 for "echo"
					if (this.line.isEmpty()) {
						this.pathIndex = (this.line.split(" ")).length + 1;
					} else {
						this.pathIndex = (this.line.split(" ")).length + 2;
					}

				} else if ((!s.equals("echo")) && (this.form == 0)) {
					// Store whatever comes after echo into a string
					this.line += s + " ";
				} else if (this.form == 1 || this.form == 2) {
					break;
				} 
			}
			return true;
		}
		
		return false;
	}
	
	public void processRequest(File f) {
		if (this.form == 1) {
			f.clearContent();
		} 
		f.addContent(line);
	}

	public void run(String[] cmd, FileSystem fs, Scanner scan) {
		String[] path;
		Directory curr = fs.getCurr();
		if (cmd.length == 1) {
			System.out.println("");
		} else {
			Mkdir mkd = new Mkdir(curr);
			
			// The following will return true iff echo is being 
			// used with ">" or ">>"
			if (this.validateRequest(cmd) && 
					(this.form == 1 || this.form == 2)) {
				
				path = fs.splitPath(cmd[this.pathIndex]); 
				
				// Check if other pathnames given and notify user that those
				// will be ignored
				if (cmd.length > this.pathIndex + 1) {
					System.out.println("Warning: Only the first target file "
							+ "has been processed");
				}
				
				if (mkd.validatePath(path, fs)) {
					Object o = fs.findObj(path, curr);
					
					if (o instanceof File) {
						this.processRequest((File) o);
					} else if (o instanceof Directory) {
						System.out.println(((Directory) o).getName() + 
								" is a directory. Objects in the same " +
								"directory cannot share the same name. ");
					} else { 
						// File does not exist so we can create the file
						Directory p = mkd.getParent();
						int index = Math.max(0, path.length-1);
						
						File f = new File(path[index]);
						p.setChild(f);
						f.setParent(p);
						f.setPath();
						
						// Add content to file
						f.addContent(this.line);
					}
				} else {
					System.out.println(cmd[0] + ": Invalid target path given");
				}
			} else if (this.form == 0) { // Print the string that was given
				for (int i = 1; i < cmd.length; i += 1) {
					System.out.print(cmd[i] + " ");
				}
				System.out.println("");
			}
		}
		
	}
}
