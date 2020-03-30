package se.miun.aforsk.blockchain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Date;

public class Blockchain {

	Chain chain = new Chain();
	
	public Blockchain(String filename) {				
		loadFile(filename);
	}
	
	public Blockchain() {
		loadGenesis();
		//saveData("START");
	}
	
	void loadGenesis() {	
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			String hashString = "" + 0;
			byte[] rawHash = sha256.digest(hashString.getBytes());
			
			StringBuffer resultHash = new StringBuffer();
		    for (byte byt : rawHash) resultHash.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
			String prevHash = resultHash.toString();			
						
			Block genesis = new Block(0, prevHash, System.currentTimeMillis(), "GENESIS");			
			chain.addBlock(genesis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	void loadFile(String filename) {
		try {
			InputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			int chainLength = Integer.parseInt(br.readLine());
			
			for(int i = 0; i != chainLength; i++) {
				int index = Integer.parseInt(br.readLine());
				String prevHash = br.readLine();
				long timestamp = Long.parseLong(br.readLine());
				String data = br.readLine();
				String hash = br.readLine();				
				Block block = new Block(index, prevHash, timestamp, data, hash);
				chain.addBlock(block);
			}
			br.close();		 
			
			if(!chain.validate()) {
				System.out.println("Load Failed");
				System.exit(-1);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String createFileString() {
		try {
			StringBuffer sb = new StringBuffer();
			
			int chainLength = chain.getLength();
			sb.append(chainLength + "\n");
			for(int i = 0; i != chainLength; i++) {
				Block b = chain.getBlock(i);
				sb.append(b.index + "\n");
				sb.append(b.prevHash + "\n");
				sb.append(b.timestamp + "\n");
				sb.append(b.data + "\n");
				sb.append(b.hash + "\n");			
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public void writeFile(String filename) {
		try {
			FileWriter fw = new FileWriter(filename, false);
			
			
			String s = createFileString();
			fw.write(s);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Block saveData(String data) {		
		Block last = chain.getLastBlock();
		int index = last.index + 1;
		String prevHash = last.hash;
		Block block = new Block(index, prevHash, System.currentTimeMillis(), data);
		chain.addBlock(block);
		return block;
	}
	
	
	
	public void printChain(){
		
		System.out.println("--== BLOCKCHAIN ==--");
		
		for(int i = 0; i!= chain.getLength(); i++) {
			Block b = chain.getBlock(i);
			System.out.println("--== Block " + b.index + " ==--");
			System.out.println("Data: " + b.data);			
			System.out.println("Timestamp: " + new Date(b.timestamp) + " - " + b.timestamp);
			System.out.println("Hash: " + b.hash);			
			System.out.println("PrevHash: " + b.prevHash);

		}
		System.out.println("--== ======== ==--");
		
		
		if(chain.validate()) {
			System.out.println("Blockchain VALIDATED!");
		} else {
			System.out.println("Blockchain is INVALID!");
		}
	}
	
	public boolean validateChain() {		
		return chain.validate();		
	}
	
	public void printChainRaw(){
		
		System.out.println("--== RAW BLOCKCHAIN ==--");
		
		for(int i = 0; i!= chain.getLength(); i++) {
			Block b = chain.getBlock(i);
			System.out.println("--== Block " + b.index + " ==--");
			System.out.println("Data: " + b.data);			
			System.out.println("Timestamp: " + b.timestamp);			
			System.out.println("Hash: " + b.hash);
			System.out.println("PrevHash: " + b.prevHash);
		}
		System.out.println("--== ======== ==--");
	}

	public Block getBlock(int index) {		
		return chain.getBlock(index);
	}
	
	public int getLength() {
		return chain.getLength();
	}
		
	public String getHash() {	
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			String toHash = createFileString();		
			byte[] rawHash = sha256.digest(toHash.getBytes());
			StringBuffer resultHash = new StringBuffer();
		    for (byte byt : rawHash) resultHash.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		    return resultHash.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void joinBlock(Block block) {
		chain.addBlock(block);
	}
}
