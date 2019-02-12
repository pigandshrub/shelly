import java.util.Scanner;

/* 
 * This is a file system that represents a tree of directories and files
 * and checks, validates, and runs the commands.
 */

public class FileSystem {
	
	private Directory curr;
	public Manual man;

	// Constructor
	public FileSystem() {
		this.man = new Manual();
		this.curr = new Directory("/"); // Tracks current path
		this.curr.setPath();
	}
	
	public boolean checkCommand(String[] cmd) {
		return this.man.find(cmd[0]);
	}
	
	public void runCommand(String[] cmd, Scanner scan) {
		
		switch (cmd[0]) {
		case "man":
			this.man.pull(cmd);
			break;
		case "cat":
			Cat cat = new Cat();
			cat.run(cmd, this);
			break;
		case "cd":
			Cd cd = new Cd();
			this.setCurr(cd.run(cmd, this));
			break;
		case "cp":
			Cp cp = new Cp();
			cp.run(cmd, this, scan, 0);
			break;
		case "echo":
			Echo ech = new Echo();
			ech.run(cmd, this, scan);
			break;
		case "find":
			Find find = new Find();
			find.run(cmd, this);
			break;
		case "get":
			Get get = new Get();
			get.run(cmd, this);
			break;
		case "grep":
			Grep grep = new Grep();
			grep.run(cmd, this);
			break;
		case "ls":
			Ls L = new Ls();
			L.run(cmd, this);
			break;
		case "mkdir":
			Mkdir mkd = new Mkdir(this.curr);
			mkd.run(cmd, this);
			break;
		case "mv":
			Mv mv = new Mv(this.curr);
			mv.remove(cmd, this, scan);
			break;
		case "pwd":
			// Note: ignores any additional arguments following this command
			System.out.println(this.curr.getPath());
			break;
		case "rm":
			Rm rm = new Rm(this.curr);
			rm.run(cmd, this);
			break;
		default:
			System.out.println( cmd[0] + ": Command not found.");
			break;
		} 
	}
	
	public Directory getCurr() {
		return this.curr;
	}
	
	public void setCurr(Directory newCurr) {
		this.curr = newCurr;
	}
	
	// Splits a full path into its subdirectory elements
	public String[] splitPath(String path) {
		String[] temp = path.split("/");
		
		// If the root "/" was typed in, we want to keep the slash
		if (temp.length == 0) {
			return path.split(" ");
		}
		return path.split("/");
	}
	
	// Find the object referenced by a validated path and return the object
	public Object findObj(String[] path, Directory dir) {
		
		// To differentiate between a directory that contains a file with the 
		// same name
		boolean different = false;
		
		for (String p:path) {
			
			if (p.equals(".")) {
				continue;
			} else if (p.equals("..")) {
				if (!(dir.getParent() == null)) {
					dir = dir.getParent();
				}
			} else if (p.equals("/")) { 
				while (!(dir.getParent() == null)) {
					dir = dir.getParent();
				}
			} else {
				different = true;
				// Check if path leads to a directory
				for (Directory d:dir.getDirs()) {
					if (d.getName().equals(p)) {
						different = false;
						dir = d;
					}
				}
				// Check if path leads to a file
				for (File f:dir.getFiles()) {
					if (f.getName().equals(p)) {
						return (Object) f;
					}
				}
			}
		}
		
		// Check if the final destination name matches path name.
		// Needed to confirm if given directory/file exists
		Object o = path[path.length-1];
		if (((o.equals(dir.getName()) && !different) || o.equals("..") || 
				o.equals("."))) {
			return (Object) dir;
		} else {
			return o;
		}
		
	}

}

