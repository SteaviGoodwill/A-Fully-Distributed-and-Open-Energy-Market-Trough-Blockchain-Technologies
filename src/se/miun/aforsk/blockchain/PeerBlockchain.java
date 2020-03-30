package se.miun.aforsk.blockchain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerBlockchain extends Blockchain implements Runnable{	

	String bootstrapIp = "127.0.0.1";
	int bootstrapPort = 9090;
	int heartbeatInterval = 10000;
	
	ServerSocket ss;
	
	boolean runPeer = true;
	
	public PeerBlockchain(String bootstrapIp) {
		this.bootstrapIp = bootstrapIp;		
		startPeer();
	}
	
	public PeerBlockchain() {
		startPeer();
	}
	
	private void startPeer() {		
		init();	
		
		Thread t = new Thread(this);
		t.start();	
		
		HeartbeatThread ht = new HeartbeatThread();
		ht.start();
	}
	
	void init(){
		try {
			//INIT!			
			Socket s = new Socket(bootstrapIp, bootstrapPort);
			InputStream is = s.getInputStream();			
			OutputStream os = s.getOutputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			
			os.write("INIT\n".getBytes());	
			
			String peer = s.getLocalSocketAddress().toString() + "\n";
			System.out.println(peer);
			os.write(peer.getBytes());		
						
			chain = new Chain();			
			int chainLength = Integer.parseInt(in.readLine());
			
			for(int i = 0; i != chainLength; i++) {
				int index = Integer.parseInt(in.readLine());
				String prevHash = in.readLine();
				long timestamp = Long.parseLong(in.readLine());
				String data = in.readLine();
				String hash = in.readLine();				
				Block block = new Block(index, prevHash, timestamp, data, hash);
				joinBlock(block);
			}
			s.close();
			
			//printChain();
		} catch (Exception e) {
			//e.printStackTrace();
		}		
	}
	
	@Override
	public void run() {			
		try {				
			String ip = InetAddress.getLocalHost().getHostAddress();					
			ss = new ServerSocket(0);
			System.out.println("Listening on: " + ip + " : " + ss.getLocalPort());
								
			while(runPeer) {
				Socket s = ss.accept();
				ConnectionThread ct = new ConnectionThread(s);
				ct.start();
			}			
		} catch (Exception e) {
			//e.printStackTrace();
		}				
	}
	
	public void shutdown() {
		runPeer = false;
		try {
			ss.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	
	private class ConnectionThread extends Thread{
		
		InputStream is;
		BufferedReader in;
		
		public ConnectionThread(Socket s) {
			try {
				is = s.getInputStream();			
				in = new BufferedReader(new InputStreamReader(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				String command = in.readLine();
								
				if(command.equalsIgnoreCase("BROADCAST")) {
					int index = Integer.parseInt(in.readLine());
					String prevHash = in.readLine();
					long timestamp = Long.parseLong(in.readLine());
					String data = in.readLine();
					String hash = in.readLine();				
					Block block = new Block(index, prevHash, timestamp, data, hash);
					PeerBlockchain.super.joinBlock(block);
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}	
	
	private class HeartbeatThread extends Thread {
		@Override
		public void run() {
			try {
				while(runPeer) {		
					Thread.sleep(heartbeatInterval);
					String selfHash = getHash() + "\n";
					Socket s = new Socket(bootstrapIp, bootstrapPort);
					InputStream is = s.getInputStream();			
					OutputStream os = s.getOutputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(is));					
					os.write("HEARTBEAT\n".getBytes());	
					
					
					String peer = s.getLocalSocketAddress().toString() + "\n";
					os.write(peer.getBytes());		
									
					os.write(selfHash.getBytes());									
					String response = in.readLine();					
					if(response.equalsIgnoreCase("BAD")) {
						System.out.println("Heartbeat BAD!");						
						init();						
					} else {
						//System.out.println("Heartbeat OK!");						
					}					
					s.close();										
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	public void publishData(String data) {		
		try {
			Socket s = new Socket(bootstrapIp, bootstrapPort);
			OutputStream os = s.getOutputStream();
			
			os.write("PUBLISH\n".getBytes());
			os.write(data.getBytes());
			
			s.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
}

