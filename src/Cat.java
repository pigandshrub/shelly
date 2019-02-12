/*
 * Object that handles the cat command
 */

public class Cat {
	
	public Cat() {
	}
	
	public void run(String[] cmd, FileSystem fs) {
		String[] path;
		Mkdir m = new Mkdir(fs.getCurr());
		
		if (cmd.length < 2) {
			System.out.println(cmd[0] + ": Please provide a file path");
		} else {
			// Remember to skip the command argument by starting at index 1
			for (int i = 1; i < cmd.length; i += 1) {
				path = fs.splitPath(cmd[i]);
				System.out.println(cmd[i] + ": ");
				
				if (m.validatePath(path, fs)) {
					Object o = fs.findObj(path, m.getParent());
			
					if (o instanceof File) {
						for (String line:((File) o).getContent()) {
							System.out.println(line);
						}
						System.out.println("");
					} else if (o instanceof Directory) {
						System.out.println(((Directory) o).getName() + " is not"
								+ " a file");
					} else {
						System.out.println(cmd[0] + ": " + cmd[1] + ": No such "
								+ "file exists");
					}
				} else {
					System.out.println(cmd[0] + ": " + cmd[i] + " is not "
							+ "a valid path");
				}
			}
		}
	}
	
}
	