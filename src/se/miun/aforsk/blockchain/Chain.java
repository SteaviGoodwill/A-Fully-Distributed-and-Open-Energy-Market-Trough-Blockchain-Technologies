package se.miun.aforsk.blockchain;

import java.util.ArrayList;

public class Chain {
	
	private ArrayList<Block> chain = new ArrayList<>();

	public Chain() {
		
	}

	public synchronized void addBlock(Block block) {				
		chain.add(block);		
	}

	public synchronized boolean validate() {
		
		Block prev = chain.get(0);		
		String hash = null;
		String prevHash = Block.hash(prev.index, prev.prevHash, prev.timestamp, prev.data);
		if(prev.index != 0) {			
			System.out.println("Genesis index failed!");
			System.out.println(prev.index + " vs " + 0);
			return false;
		}
		if(prevHash.compareToIgnoreCase(prev.hash) != 0) {
			System.out.println("Genesis hash failed!");
			System.out.println(prevHash + " vs " + prev.hash);
			return false;			
		}		
		for(int i = 1; i != chain.size(); i++) {
			Block b = chain.get(i);						
			if(i != b.index) {
				System.out.println("Index failed");
				System.out.println( i + " vs " + b.index);
				return false;
			}
			if(prevHash.compareToIgnoreCase(b.prevHash) != 0) {
				System.out.println("Previous hash failed in block " + i);
				System.out.println(prevHash + " vs " + b.prevHash);
				return false;
			}						
			hash = Block.hash(b.index, b.prevHash, b.timestamp, b.data);
			if(hash.compareToIgnoreCase(b.hash) != 0) {
				System.out.println("Self hash failed in block " + i);
				System.out.println(hash + " vs " + b.hash);
				return false;			
			}
			prevHash = hash;						
		}				
		return true;
	}

	public Block getLastBlock() {
		return chain.get(chain.size()-1);
	}

	public int getLength() {
		return chain.size();
	}

	public Block getBlock(int index) {		
		return chain.get(index);
	}
	
}
