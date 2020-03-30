package se.miun.aforsk2020;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StartBootstrap {

	public static void main(String[] args) {
		try {
			ConsortBootstrap cb = new ConsortBootstrap();
			cb.start();	
			
			//Run in background until closed down
			Thread.sleep(1000);
	        System.out.println("\nPress any key to shutdown Consortium Bootstrap");
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));    	
			in.readLine();
			
			cb.shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
