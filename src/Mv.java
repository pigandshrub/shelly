import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * Object that handles the Mv command
 */

public class Mv extends Cp {
	
	public Directory curr;
	public int form;
	public Object tar;
	public List<Object> src;
	
	public Mv(Directory currDir) {
		this.curr = currDir;
		this.form = 0; 
		this.tar = null;
		this.src = new ArrayList<Object>();
	}
	
	public void remove(String[] cmd, FileSystem fs, Scanner scan) {
		
		Rm rm = new Rm(this.curr);
		
		// Copy sources to target object
		this.src = this.run(cmd, fs, scan, 1); 
		
		// If copy is successful, we can remove the sources
		if (this.src != null) {  
			for (Object o:this.src) {
				rm.removeObject(o, 1); 
			}
		} else {
			System.out.println("Error: One or more source paths invalid. "
					+ "Unable to move");
		}
		
	}
		
}
