import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * Object that handles the cp command
 */

public class Cp {
	
	public int form;
	public Object tar;
	public List<Object> src;
	
	// Constructor that keeps count of how many files or directories can be 
	// copied, and which usage form of the "cp" method should be used.
	
	// Possible values of this.form:
	// 1 - cp source_file target_file
    // 2 - cp source_file_or_dir_1 [source_file_or_dir_2] [...] target_directory 
    // 3 - cp source_directory target_directory
	public Cp() {
		this.form = 0; 
		this.tar = null;
		this.src = new ArrayList<Object>();
	}
	
	public List<Object> run(String[] cmd, FileSystem fs, Scanner scan, int m) {
		
		if (cmd.length < 3) { // Missing method arguments
			System.out.println(cmd[0] + ": Missing arguments. Requires at "
					+ "least one source path and exactly one target path");
			return null;
			
		} else {
			
			// Check if all source paths exist and are valid.
			// This process also assigns preliminary "this.form" values
			List<Object> srcs = this.validateSrcAndForm(cmd, fs);
			
			// Identify the target path and check if it's valid
			String target = cmd[cmd.length - 1];
			String[] path = fs.splitPath(target);
			Mkdir targetDir = new Mkdir(fs.getCurr());

			if (targetDir.validatePath(path, fs)) { // Target path is valid
				
				this.tar = fs.findObj(path, fs.getCurr()); // Get target
				
				// Case where the target needs to be built
				if (!(this.tar instanceof Directory) && 
						!(this.tar instanceof File)) {  
					
					Directory p = targetDir.getParent();
					int index = Math.max(0, path.length-1);
					target = this.buildTarget(p, index, form, path);
					
				// Case where target already exists
				} else {
					
					// Check whether existing target should be overwritten
					if (!(this.processExistingTarget(scan))) {
						return null;
					}
				}
				
				this.processCopy(m);
				System.out.println("Finished copying over to " + target);
		
			} else {
				System.out.println(cmd[0] + ": " + this.tar + ": Invalid "
						+ "target path");
			}
			
			return srcs;
		}
	}
	
	
	// Creates target file or directory given the command form
	public String buildTarget(Directory p, int index, int form, String[] path) {
		
		if (form == 1) {  // Build a file
			File f = new File(path[index]);
			p.setChild(f);
			f.setParent(p);
			f.setPath();
			this.tar = f;
			return f.getName();
			
		} else { // Build a directory
			Directory d = new Directory(path[index]);
			p.setChild(d);
			d.setParent(p);
			d.setPath();
			this.tar = d;
			return d.getName();
		}
	}
	
	
	// Clears any existing content from target file/directory
	// and copies sources to target
	public void processCopy(int m) {
		// Clear any existing content from target file/directory
		if (this.form == 1) {
			((File) this.tar).clearContent();
		} else {
			((Directory) this.tar).emptyDirs();
			((Directory) this.tar).emptyFiles();
		}
		// Copy sources to target
		for (Object o:this.src) {
			this.copyOver(o, this.form, m);
		}
	}
	
	
	
	// Handles the user's input concerning overwriting an existing target
	public boolean processExistingTarget(Scanner scan) {
		
		String line = "";
		
		if ((this.tar instanceof Directory)) {
			
			// Correct preliminary guess at command usage
			if (this.form == 1) {
				this.form = 2;
			}
			
			System.out.print("Do you want to overwrite existing "
					+ "directory " + ((Directory) this.tar).getName() +
					"? ");
			
		} else { // target is a file
			System.out.print("Do you want to overwrite existing "
					+ "file " + ((File) this.tar).getName() + "? ");			
		}
		
		line = this.overwrite(scan);
		
		if (line.matches("n|N|No|no")) {
			System.out.println("Okay. Nothing has been copied over.");
			return false;
		}
		
		return true;
	}
	
	
	

	// Confirms with user if an existing directory or file should be overwritten
	// and returns response
	public String overwrite(Scanner scan) {
		
		String line = "";
		try {
			while (!((line = scan.nextLine()).trim().replaceAll("\\s+",
					"").matches("y|Y|Yes|yes|n|N|No|no"))) 
			{
				System.out.print("Please enter y or n: ");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return line;
	}
	
	
	
	// Identifies the source paths, determine the command form being used, and 
	// returns a list of validated sources
	public List<Object> validateSrcAndForm(String[] cmd, FileSystem fs) {
		List<Object> firstSrc = this.validateSources(cmd, fs);
		
		if (firstSrc != null) {	
			
			Object obj = firstSrc.get(0);
			if ((obj instanceof Directory) && (cmd.length == 3)) {
				this.form = 3;
			} else if ((obj instanceof File) && (cmd.length == 3)) {
				this.form = 1;
			} else {
				this.form = 2;
			}
		}
		
		return firstSrc;
	}
	
	
	
	// Checks if each source object exists and if all sources exists then 
	// returns a list of the source objects, otherwise returns null;
	public List<Object> validateSources(String[] cmd, FileSystem fs) {
		
		// Loop through each given path
		for (int i = 1; i < cmd.length - 1; i += 1) {  
			Mkdir source = new Mkdir(fs.getCurr());
			String[] path = fs.splitPath(cmd[i]);
		
			// Check if the source path is valid
			if (!source.validatePath(path, fs)) {
				System.out.println(cmd[0] + ": " + cmd[i] + ": Not valid");
				return null;
			}
				
			// Check if the source path exists
			Directory p = source.getParent();
			int index = Math.max(0, path.length-1);
			if (!source.hasDuplication(path[index], p, 0)) {
				System.out.println(cmd[0] + ": Source object: " + cmd[i] 
						+ ": Does not exist");
				return null;
			}
			
			this.src.add(fs.findObj(path, fs.getCurr()));
		}
		
		return this.src;
	}
	
	
	
	// Overwrites content to new file or directory
	public void copyOver(Object o, int form, int m) {
		
		if ((form == 1)) { // o should be a file
			List<String> content = ((File) o).getContent();
			for (String line:content) {
				((File) this.tar).addContent(line);
			}
		
		} else if ((form == 2) || (form == 3)) { // o can be a file or directory
			
			// Special case: Mv command: if "moving" a directory
			if ((m == 1) && (o instanceof Directory)) {
				Directory d = new Directory(((Directory) o).getName());
				d.setParent((Directory) this.tar);
				d.setPath();
				d.traverseAndCopy(o);
				((Directory) this.tar).setChild(d);
				
			// All other cases	
			} else { 
				((Directory) this.tar).traverseAndCopy(o);
			}
			
		} else {
			System.out.println("Error: Unknown usage of command");
		}
		
	}
	
}
