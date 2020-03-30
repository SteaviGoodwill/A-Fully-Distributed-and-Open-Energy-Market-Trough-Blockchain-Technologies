package se.miun.aforsk.blockchain;

import java.security.MessageDigest;

public class Block {
	
	public final int index;	
	public final String prevHash;	
	public final long timestamp;	
	public final String data;
	public final String hash; 
	
	public Block(int index, String prevHash, long timestamp, String data) {
		this.index = index;
		this.prevHash = prevHash;
		this.timestamp = timestamp;
		this.data = data;		
		this.hash = Block.hash(index, prevHash, timestamp, data);
	}
	
	public Block(int index, String prevHash, long timestamp, String data, String hash) {
		this.index = index;
		this.prevHash = prevHash;
		this.timestamp = timestamp;
		this.data = data;		
		this.hash = hash;
	}
	
	public String toFileString() {
		StringBuffer sb = new StringBuffer();
		sb.append(index + "\n");
		sb.append(prevHash + "\n");
		sb.append(timestamp + "\n");
		sb.append(data + "\n");
		sb.append(hash + "\n");		
		return sb.toString();		
	}
	
	public synchronized static String hash(int index, String prevHash, long timestamp, String data) {					
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			String toHash = "" + index + prevHash + timestamp + data;
			
			byte[] rawHash = sha256.digest(toHash.getBytes());
			StringBuffer resultHash = new StringBuffer();
		    for (byte byt : rawHash) resultHash.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
			return resultHash.toString();			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
