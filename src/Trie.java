import java.util.*;

public class Trie {
	TrieNode head = null;

	public Trie(){
		this.head = new TrieNode();
		head.value = ""; // as its used as the head, not value wanted
	}

	public void addWord(String word){
		if(word == null)
			return;

		word = word.toLowerCase();
		head.addChild(word, 0);
	}

	public String[] getWord(String word){
		ArrayList<String> allResults = new ArrayList<String>();
		allResults = head.getChild(allResults, word, 0);
		ArrayList<String> noDuplicateResults = new ArrayList<String>();
		for(String s : allResults){
			if(!noDuplicateResults.contains(s))
				noDuplicateResults.add(s);
		}
			
		String[] toReturn = new String[10];
		for(int i=0; i < Math.min(10, noDuplicateResults.size()); i++) // only want ten results
			toReturn[i] = noDuplicateResults.get(i);
		return toReturn;
			
	
	
	}
	
	public void word(){head.printWord(); }
}
