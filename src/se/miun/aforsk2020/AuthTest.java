package se.miun.aforsk2020;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class AuthTest {
	
	
	public static void main(String[] args) {
		AuthTest at = new AuthTest();
		at.run();		
	}
	
	public void run(){
		try {
			
			String data = "SECRETDATA";
			Signature signer = Signature.getInstance("SHA256withRSA");
			
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey privateKey = pair.getPrivate();
			PublicKey publicKey = pair.getPublic();
			
			signer.initSign(privateKey);			

			signer.update(data.getBytes());
			
			
			byte[] signature = signer.sign();
			
			String ss = new String(signature);
			System.out.println("BYTESSIGNATURE");
			System.out.println(ss);
			System.out.println("BYTESSIGNATURE");
			
			System.out.println("SIGNATURE");
			System.out.println(toHex(signature));
			System.out.println(asHex(signature));
			String hex = asHex(signature);					
			byte[] b = asBytes(hex);
			System.out.println(toHex(b));
			System.out.println(asHex(b));
			
			
			
			
			
			System.out.println("SIGNATURE");

			
			
			byte[] pubBytes = publicKey.getEncoded();
			
			
			
			PublicKey importedPubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubBytes));
			
			
			
			
			
			
			
			Signature verify = Signature.getInstance("SHA256withRSA");			
			verify.initVerify(importedPubKey);
			
			
			verify.update(data.getBytes());
			boolean check = verify.verify(signature);
			
			
			System.out.println("Verify: " + check);
			
			
			
			/*
			
			
			
			
			byte[] bytes = publicKey.getEncoded(); 
			
			
			
			
			PublicKey publicKey = 
				    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
			
			
			
			PrivateKey pk = PrivateKey.
			
			
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
	}
	
	
	
	String toHex(byte[] bytes){		
		StringBuffer result = new StringBuffer();
	    for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));			    			    			   
		return result.toString();
	}
	
	
	public static String asHex (byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
       return strbuf.toString();
    }
    public static byte[] asBytes (String s) {
        String s2;
        byte[] b = new byte[s.length() / 2];
        int i;
        for (i = 0; i < s.length() / 2; i++) {
            s2 = s.substring(i * 2, i * 2 + 2);
            b[i] = (byte)(Integer.parseInt(s2, 16) & 0xff);
        }
        return b;
    }
	
	

}
