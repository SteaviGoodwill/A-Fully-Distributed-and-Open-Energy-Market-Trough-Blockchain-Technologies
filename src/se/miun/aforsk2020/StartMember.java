package se.miun.aforsk2020;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class StartMember {

	public static void main(String[] args) {
		try {
			
			Random r = new Random(System.currentTimeMillis());
			int port = 7000 + r.nextInt(1000);
			ConsortMember cm = new ConsortMember(port);
			cm.start();
	
			
			//Run in background until closed down
			Thread.sleep(1000);
	        System.out.println("\nPress any key to shutdown Consortium Member");
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));    	
			in.readLine();
			
			cm.shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
