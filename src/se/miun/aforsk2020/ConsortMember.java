package se.miun.aforsk2020;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import se.miun.aforsk.blockchain.Blockchain;

public class ConsortMember extends Thread {

	private int bootstrapPort = 9999;
	
	private int port;
	boolean runMember = true;
	
	ServerSocket ss = null;
	
	PrivateKey privateKey;
	PublicKey publicKey;
	Signature signer;
	
	public ConsortMember(int port) {		
		try {			
			ss = new ServerSocket(port);		
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			
			KeyPair pair = keyGen.generateKeyPair();
			privateKey = pair.getPrivate();
			publicKey = pair.getPublic();
			signer = Signature.getInstance("SHA256withRSA");
			signer.initSign(privateKey);
									
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void run() {
		try {
		
			Socket inits = new Socket("127.0.0.1", bootstrapPort);
			InputStream initis = inits.getInputStream();
			OutputStream initos = inits.getOutputStream();
			
			String initString = "INIT_MEMBER;127.0.0.1;" + port + ";" + new String(publicKey.getEncoded());
			initos.write(initString.getBytes());
			initos.flush();
			
			inits.close();
					
			while(runMember) {
				try {
					
					Socket s = ss.accept();				
					InputStream is = s.getInputStream();
					OutputStream os = s.getOutputStream();
									
					
					byte[] sbuffer = new byte[65536];
					is.read(sbuffer);
					
					String data = new String(sbuffer).trim();
					
					signer.update(data.getBytes());
					byte[] signature = signer.sign();
					os.write(signature);
					os.flush();
					s.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		try {
			runMember = false;
			this.interrupt();			
		} catch (Exception e) {

		}
	}
}
