package se.miun.aforsk2020;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import se.miun.aforsk.blockchain.Blockchain;

public class ConsortClient extends Thread {
	
	private int bootstrapPort = 9999;
	
	Blockchain bc = null;
	
	public ConsortClient() {
		try {
			Socket inits = new Socket("127.0.0.1", bootstrapPort);
			InputStream initis = inits.getInputStream();
			OutputStream initos = inits.getOutputStream();
			
			String initString = "INIT_CLIENT";
			initos.write(initString.getBytes());
			initos.flush();
			
			byte[] initBuffer = new byte[65536];
			initis.read(initBuffer);
			
			String bcString = new String(initBuffer).trim();

			FileWriter fw = new FileWriter("temp.txt", false);
			fw.write(bcString);
			fw.close();
			
			bc = new Blockchain("temp.txt");
			 
			inits.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void run() {

	}

	public void printChain() {
		bc.printChain();
		
	}
	
	public void getData(int blockNr) {
		
	}	
}
