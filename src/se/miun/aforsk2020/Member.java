package se.miun.aforsk2020;

public class Member {
	
	public String host = null;	
	public int port = 0;
	public byte[] publicKey = null;
	
	public Member(String host, int port, byte[] publicKey) {
		this.host = host;
		this.port = port;
		this.publicKey = publicKey;		
	}
}
