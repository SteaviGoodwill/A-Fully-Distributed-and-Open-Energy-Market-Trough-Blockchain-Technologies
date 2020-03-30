package se.miun.aforsk2020;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StartClient {

	public static void main(String[] args) {
		try {		
			ConsortClient cc = new ConsortClient();
			
			cc.start();
			
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));    
			boolean runClient = true;			
			while(runClient) {
				//COMMAND LINE Interface
				System.out.println("\nConsortium Client Interface");
				System.out.println("1. Print Chain");
				System.out.println("Choose Command: ");
				 	
				String command = in.readLine();
				
				
				if(command.equalsIgnoreCase("1")) {
					cc.printChain();
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
