import java.net.*;
import java.io.*;

/*
 * Object that handles the get command
 */

public class Get {
	
	public Get() {
	}
	
	public void run(String[] cmd, FileSystem fs) {
		if (cmd.length != 2) {
			System.out.println(cmd[0] + ": Requires exactly one URL address");
		} else {
			String[] p = fs.splitPath(cmd[1]);
			
			try {
				// Prepare URL connection
				URI uri = new URI(cmd[1]);
				if (!uri.isAbsolute()) {
					uri = new URI("http://" + cmd[1]);
				}
				URL url = uri.toURL();
				URLConnection myURLConnection = url.openConnection();
				myURLConnection.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(
                        myURLConnection.getInputStream()));
				
				String name = this.handleDuplication(p, fs);
				
				// Create and prepare file to retrieve content
				String inputLine;
				File f = new File(name);
				f.setParent(fs.getCurr());
				fs.getCurr().setChild(f);
				
				// Read URL content and put into the new file
		        while ((inputLine = in.readLine()) != null) {
		            f.addContent(inputLine);
		        }
		        in.close();
		        
		        System.out.println("Content from URL retrieved successfully");
		       
		        
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				System.out.println(cmd[0] + ": Invalid URL format");
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println(cmd[0] + ": Invalid URL provided.");
			} catch (URISyntaxException e) {
				//e.printStackTrace();
				System.out.println(cmd[0] + ": Invalid URL format");
			}
		}
		
	}
	
	public String handleDuplication(String[] p, FileSystem fs) {
		Mkdir m = new Mkdir(fs.getCurr());
		String name = p[p.length-1];
		if (m.hasDuplication(name, m.curr, 0)) {
			name += "_copy";
		}
		return name;
	}
}
