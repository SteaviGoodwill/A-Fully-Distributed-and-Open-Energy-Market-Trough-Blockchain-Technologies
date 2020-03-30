package se.miun.aforsk.blockchain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BootstrapBlockchain extends Blockchain implements Runnable{
	
	boolean runBoostrap = true;
	int port = 9090;	
	ServerSocket ss;
	
	ArrayList<String> peerList = new ArrayList<>();
	
	public BootstrapBlockchain(String filename) {
		super(filename);
		startBoostrap();
	}
	
	public BootstrapBlockchain() {
		super();
		startBoostrap();
	}
	
	private void startBoostrap() {
		Thread t = new Thread(this);
		t.start();	
	}
	
	@Override
	public void run() {			
		try {				
			String ip = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Listening on: " + ip + " : " + port);			
			ss = new ServerSocket(9090);
						
			while(runBoostrap) {

				Socket s = ss.accept();
				
				System.out.println("Incoming connection from: " + s.getRemoteSocketAddress().toString());

				ConnectionThread ct = new ConnectionThread(s);
				ct.start();
			}			
		} catch (Exception e) {
			//e.printStackTrace();
		}				
	}
	
	public void shutdown() {
		runBoostrap = false;
		try {
			ss.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	
	private class ConnectionThread extends Thread{
		
		InputStream is;
		OutputStream os;
		BufferedReader in;
		
		public ConnectionThread(Socket s) {
			try {
				is = s.getInputStream();			
				os = s.getOutputStream();
				in = new BufferedReader(new InputStreamReader(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				String command = in.readLine();
				
				if(command.equalsIgnoreCase("INIT")) {
					System.out.println("COMMAND: INIT");
					String peer = in.readLine();
					if(!peerList.contains(peer)) {
						peerList.add(peer);											
					}
					
					String s = BootstrapBlockchain.super.createFileString();					
					os.write(s.getBytes());
				}
								
				if(command.equalsIgnoreCase("PUBLISH")) {	
					System.out.println("COMMAND: PUBLISH");
					String s = in.readLine();
					Block b = BootstrapBlockchain.super.saveData(s);					
					os.write("OK".getBytes());
				
					//Broadcast					
					for(int i = 0; i != peerList.size(); i++) {
						try {
							String peer = peerList.get(i);
							
							String[] split = peer.split(":");
							String peerIp = split[0];
							int peerPort = Integer.parseInt(split[1]);						
							Socket peerSocket = new Socket(peerIp, peerPort);
							
							OutputStream peerOut = peerSocket.getOutputStream();
							peerOut.write("BROADCAST\n".getBytes());
							peerOut.write(b.toFileString().getBytes());
							peerSocket.close();							
						} catch (Exception e) {
							System.out.println("BAD PEER: " + peerList.get(i));
							peerList.remove(i);
							i--;
							e.printStackTrace();
						}
					}
				}	
				
				if(command.equalsIgnoreCase("HEARTBEAT")) {					
					System.out.println("COMMAND: HEARTBEAT");
					String peer = in.readLine();
					if(!peerList.contains(peer)) {
						peerList.add(peer);											
					}
					
					String peerHash = in.readLine();
					String bootstrapHash = BootstrapBlockchain.super.getHash();										
					if(peerHash.equalsIgnoreCase(bootstrapHash)) {						
						os.write("OK\n".getBytes());						
					} else {
						os.write("BAD\n".getBytes());												
					}
										
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
}

