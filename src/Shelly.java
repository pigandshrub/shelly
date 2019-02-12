import java.util.Scanner;

/*
 * This is a basic simulation of a terminal window. 
 */

public class Shelly {

	// Constructor
	public Shelly() {
	}
	
	// If a terminal window is generated, prepare manual and file path system
	// that the terminal can use methods on.
	public static void main(String[] args) {
		
		// Build an empty file path system.
		FileSystem sys = new FileSystem();
		// Print line to indicate shell is ready to receive commands.
		System.out.println("*** Welcome to Shelly! ***");
		System.out.println("Shelly: Updated Version 2016.1  ");
		System.out.println("If this is your first time using a shell, "
				+ "type 'man' for assistance.\n");
		System.out.print( sys.getCurr().getPath() + " # ");
		
		try {
			// Get input and continue getting input until "exit" is entered.
			Scanner scan = new Scanner(System.in);

			String line = "";
			while (!((line = scan.nextLine()).trim().replaceAll("\\s+",
					"").equals("exit"))) 
			{
				
				String[] cmd = line.trim().replaceAll("\\s+",  
						" ").split(" ");
				
				// If the user just presses enter
				if (cmd[0].equals("")) {
					System.out.println();
					
				// Check if the command is in the manual
				} else if (sys.checkCommand(cmd)) { 
					// Process command, updating directory path if necessary
					sys.runCommand(cmd, scan);
					
				// If command is not recognized, let the user know.	
				} else { 
					System.out.println( cmd[0] + ": Command not found.");
				}
				
				System.out.print( sys.getCurr().getPath() + " # ");
				
			}
			// Exit program
			System.out.println( "*** Good-bye! ***");
			scan.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
