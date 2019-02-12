
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

/*
 * Object that handles the grep command
 */

public class Grep extends Cp {
	
	public int indexStart;
	public int indexEnd;
	public List<Object> srcs;
	
	public Grep() {
		this.indexStart = -1;
		this.indexEnd = -1;
		this.srcs = new ArrayList<Object>();
	}
	
	public void run(String[] cmd, FileSystem fs) {
		
		if (this.validateCommand(cmd)) {
			
			int flag = 0; // Track the -R
			
			for (int i = 1; i < cmd.length; i += 1) {

				// -R given in proper position in command line
				if (i == 1 && cmd[i].equals("-R")) {
					flag = 1;
				
				} else {
					
					// Found where the string starts
					if ((this.indexStart < 0) && (cmd[i].matches("^\".*"))) {
						this.indexStart = i;
					}
					// Found where the string ends
					if ((this.indexEnd < 0) && (cmd[i].matches(".*\"$"))) {
						this.indexEnd = i;
					} 
				}
			}
			
			
			// Get files or directory
			this.src = this.validateSources(
					this.subArray(this.indexEnd + 1, cmd.length - 1, cmd), fs);
			
			if (this.checkClass(Directory.class) && flag == 0) {
				System.out.println(cmd[0] + ": Directory path found. " + 
					"Use rm -R if you want to search a directory. "
					+ "Process stopped");
				return;
			}
			// Get pattern items
			List<Pattern> patterns = this.getPatterns(
					this.subArray(this.indexStart, this.indexEnd, cmd));
			
			// Process request
			this.processCommand(patterns, fs.getCurr());
		}
		
	}
	
	
	
	public void processCommand(List<Pattern> patterns, Directory d) {
		for (Pattern p:patterns) {
			for (Object o:this.src) {
				this.search(o, p, d.getName());
			}
		}
	}
	
	
	
	public void search(Object o, Pattern p, String dname) {
		
		if (o instanceof File) {
			for (String line:((File) o).getContent()) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					System.out.println(((File) o).getLocalPath(dname) + 
							": " + line);
				}
			}
		}
		
		if (o instanceof Directory) {
			for (Directory d:((Directory) o).getDirs()) {
				this.search(d, p, dname);
			}
			
			for (File f:((Directory) o).getFiles()) {
				this.search(f, p, dname);
			}
		}
	}
	
	
	
	public <T> boolean checkClass(Class<T> type) {
		for (Object o:this.src) {
			if (type.isInstance(o)) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean validateCommand(String[] cmd) {
		
		if (cmd.length < 2) {
			System.out.println(cmd[0] + ": Requires at least one string (given "
					+ "within single or double quotes) and at least one "
					+ " file or directory name");
			return false;
			
		// Command line ends with a -R
		} else if (cmd[cmd.length - 1].equals("-R")) { 
			System.out.println(cmd[0] + ": -R is not a valid file or dir path");
			return false;
		
		// Command line ends with quotations "
		} else if (cmd[cmd.length - 1].matches(".*\"$")) {
			System.out.println(cmd[0] + ": Missing at least one file or"
					+ " directory name");
			return false;
			
		} else {
		
			return true;
		}
	}
	
	
	
	public String[] subArray(int start, int end, String[] cmd) {
		int length = end - start + 1 + 2; // 1 for real length, 2 for fillers
		String[] s = new String[length]; 
		
		for (int i = 0; i < length; i += 1) {
			// First and last index are fillers because the validateSources 
			// method does not look at the first or last elements of an array
			if (i == 0) {
				s[i] = "grep";
			} else if (i == length - 1) {
				s[i] = " ";
			} else {
				s[i] = cmd[start + i - 1];
			}

			// Remove the boundary quotes if necessary
			if (this.indexStart == start + i - 1) {
				s[i] = s[i].substring(1);
			}
			if (this.indexEnd == start + i - 1) {
				s[i] = s[i].substring(0, s[i].length()-1);
			}
		}
		
		return s;
	}
	
	
	
	public List<Pattern> getPatterns(String[] s) {
		
		String line = "";
		List<Pattern> patterns = new ArrayList<Pattern>();
		
		Pattern p = Pattern.compile(".*"); // Set pattern to match anything
		
		if (s.length == 2) {  // No string was provided to match grep
			patterns.add(p);
			return patterns;
		}
		
		// Remember to account for the fillers in the subArray method
		for (int i = 1; i < s.length - 1; i += 1) {
			line = s[i];
			// Set up pattern based on the line
			p = Pattern.compile(line);
			patterns.add(p);
		}
		
		return patterns;
	}

}
