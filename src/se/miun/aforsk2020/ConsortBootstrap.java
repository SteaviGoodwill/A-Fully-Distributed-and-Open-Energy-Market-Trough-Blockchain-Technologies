package se.miun.aforsk2020;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import se.miun.aforsk.blockchain.Blockchain;

public class ConsortBootstrap extends Thread {

	private int bootstrapPort = 9999;
	private int datagramSocketPort = 9999;
	DatagramSocket datagramSocket = null;	
	boolean runBootastrap = true;
	
	ServerSocket ss = null;
	
	ArrayList<Member> members = new ArrayList<Member>();
	
	Blockchain bc = new Blockchain();

	public ConsortBootstrap() {		
		try {
			
			ss = new ServerSocket(bootstrapPort);			
			datagramSocket = new DatagramSocket(datagramSocketPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		
		while(runBootastrap) {
			try {
				
				Socket s = ss.accept();				
				InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream();
												
				byte[] sbuffer = new byte[65536];
				is.read(sbuffer);
				
				String incdata = new String(sbuffer).trim();				
				System.out.println(incdata);
				
				String split[] = incdata.split(";");
				if(split[0].equalsIgnoreCase("INIT_MEMBER")) {					
										
					//Init new member
					int beginIndex = incdata.indexOf(split[3]);
					String keyString = incdata.substring(beginIndex);					
					Member m = new Member(split[1], Integer.parseInt(split[2]), keyString.getBytes());
					members.add(m);

					String ok = "OK";
					os.write(ok.getBytes());
					os.flush();
									
				} else if(split[0].equalsIgnoreCase("REMOVE_MEMBER")) {
					//Not supported yet...
					
				} else if(split[0].equalsIgnoreCase("INIT_CLIENT")) {					
					//Send back the blockchain					
					String bcString = bc.createFileString();					
					os.write(bcString.getBytes());
					os.flush();
					
					//Send back all public keys
					for(int i = 0; i != members.size(); i++) {
						os.write(members.get(i).publicKey);
						os.write(";".getBytes());
					}
				} else if(split[0].equalsIgnoreCase("SAVE_DATA")) {
					
					String saveData = split[1];					
					String signatures = "";
					
					//GET IT SIGNED BY the consort
					for(int i = 0; i != members.size(); i++) {						
						Member m = members.get(i);
						
						Socket signSocket = new Socket(m.host, m.port);						
						
						OutputStream signOs = signSocket.getOutputStream();						
						InputStream signIs = signSocket.getInputStream();
												
						signOs.write(saveData.getBytes());
						signOs.flush();
						
						byte[] signBuffer = new byte[65536];
						signIs.read(signBuffer);						
						signatures = signatures + ":" + new String(signBuffer).trim();	
						signSocket.close();						
					}
					
					String[] signsplit = signatures.split(":");
					for(int i = 0; i != members.size(); i++) {						
						//Verify the signatures?						
						byte[] pubKey = members.get(i).publicKey;
						PublicKey importedPubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKey));
						Signature verify = Signature.getInstance("SHA256withRSA");			
						verify.initVerify(importedPubKey);
						verify.update(saveData.getBytes());
						boolean check = verify.verify(signsplit[i].getBytes());
						System.out.println("Check: " + check);
					}
					
					//Save the Data
					bc.saveData(saveData);

					//Broadcast it
					//Block;Signature:Signature:Signature									
					String sendString = saveData + ";" + signatures;
					byte[] sendData = sendString.getBytes();
					
					DatagramPacket bcPacket = new DatagramPacket(sendData, sendData.length,
							InetAddress.getByName("255.255.255.255"), datagramSocketPort);
					datagramSocket.send(bcPacket);
					
					
				}
				
				s.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		try {
			runBootastrap = false;
			this.interrupt();			
		} catch (Exception e) {

		}
	}
}