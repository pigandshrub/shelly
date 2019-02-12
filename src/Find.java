import java.util.regex.*;

/*
 * Object that handles the find command
 */

public class Find {
	
	public Find() {
	}
	
	
	public void run(String[] cmd, FileSystem fs) {
		
		if (cmd.length == 1) {
			System.out.println(cmd[0] + ": Please provide at least one "
					+ "expression to evaluate");
		} else {
			// Loop through the list of expressions
			for (int i = 1; i < cmd.length; i += 1) {
				try {
					Pattern p = Pattern.compile(cmd[i]);
					if (!found(fs.getCurr(), p, false)) {
						System.out.println(cmd[0] + ": " + cmd[i] + 
								": No such file or directory found");
					}
				} catch (PatternSyntaxException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	
	
	public boolean found(Directory d, Pattern p, boolean f) {
		for (Directory dir:d.getDirs()) {
			Matcher m = p.matcher(dir.getName()); 
			if (m.find()) { 
				f = true;
				Pattern pat = Pattern.compile(".*\\/(" + dir.getName() +
						"\\/*.*)");
				Matcher mat = pat.matcher(dir.getPath());
				if (mat.find()) {
					System.out.println(mat.group(1));
					// Traverse through all the content inside this directory
					// and print out the paths
					this.traverse(dir, mat.group(1));
				}
				
			}
		}
		for (File file:d.getFiles()) {
			Matcher m = p.matcher(file.getName()); 
			if (m.find()) { 
				f = true;
				System.out.println(file.getName());
			}
		}
		return f;
	}
	
	
	
	public void traverse(Directory d, String p) {
		
		if (!d.getDirs().isEmpty()) { 
			for (Directory dir:d.getDirs()) {
				System.out.println(p + "/" + dir.getName());
				traverse(dir, p + "/" + dir.getName());
			}
		}
		
		if (!d.getFiles().isEmpty()) {
			for (File f:d.getFiles()) {
				System.out.println(p + "/" + f.getName());
			}
		}
	}

}
