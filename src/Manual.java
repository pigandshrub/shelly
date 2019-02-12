import java.util.HashMap;
import java.util.Scanner;
import java.io.FileReader;

/*
 * The manual object (handles the man command) 
 */

public class Manual {
	
	public HashMap<String, String> manual;
	
	// Constructor
	public Manual(){
		this.manual = new HashMap<String, String>();
		manual.put("man", "man.txt");
		manual.put("mkdir", "mkdir.txt");
		manual.put("cd", "cd.txt");
		manual.put("ls",  "ls.txt");
		manual.put("pwd", "pwd.txt");
		manual.put("cat", "cat.txt");
		manual.put("mv", "mv.txt");
		manual.put("cp", "cp.txt");
		manual.put("echo", "echo.txt");
		manual.put("find", "find.txt");
		manual.put("get", "get.txt");
		manual.put("grep", "grep.txt");
		manual.put("rm", "rm.txt");
		manual.put("exit", "exit.txt");
	}

	public boolean find(String cmd) {
		return this.manual.containsKey(cmd);
	}
	
	public void pull(String[] cmd) {
		
		String currentDirectory = System.getProperty("user.dir");
		String fname;
		
		// Determine if cmd is formatted correctly to use manual
		if (cmd.length == 2) {
			if (!(this.manual.get(cmd[1]) == null)) {
				fname = currentDirectory + "/../manual/" + this.manual.get(cmd[1]);
			} else {
				fname = "No such manual page on " + cmd[1];
			}
		} else if (cmd.length == 1) {
			fname = currentDirectory + "/../manual/" + this.manual.get(cmd[0]);
		} else {
			System.out.println(cmd[0] + ": Invalid number of arguments given");
			return;
		}
		try {
			FileReader f = new FileReader(fname);
			Scanner scan = new Scanner(f);
			while (scan.hasNext()) {
				System.out.println(scan.nextLine());
			}
			System.out.println("");
			scan.close();
			f.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}