import java.util.*;
import java.lang.*;

public class TrieNode {
	public String value;
	public boolean isWord;
	public Map<Character, TrieNode> children;

	public TrieNode(){
		this.value = null;
		isWord = false;
		children = new HashMap<Character, TrieNode>();
	}

	public boolean addChild(String word, int charAt){
		TrieNode node = null; // node used to add child to if required
		if(charAt >= word.length())
			return false;

		char character = word.charAt(charAt);

		for(Map.Entry<Character, TrieNode> child : children.entrySet()){
			if(child.getKey().equals(character)){ // if there is a child with the next char
				node = child.getValue();
				break;
			}
		}
		if(node == null){   // If a new child TrieNode needs to be created
			node = new TrieNode();
			children.put(character, node);	

			if(charAt + 1 == word.length()){
				node.isWord = true;
				node.value = word;
				return true;
			}
		}

		if(charAt++ == word.length()){ // if the word has been added
			this.isWord = true;
			this.value = word;
			return true;
		}
		return node.addChild(word, charAt);
	}

	public void printWord(){
		if(isWord)
			System.out.println(value);
		for(Map.Entry<Character, TrieNode> child : children.entrySet()){
			child.getValue().printWord();
		}
	}


	public ArrayList<String> getChild(ArrayList<String> returnWords,  String word, int charAt){ 
		if(charAt < word.length() ){
			char character = word.charAt(charAt);

			if(this.value != null){
				if(this.value.equals(word)){ // if this is the word we are looking for
					returnWords = new ArrayList<String>();
					returnWords.add(word);
					return returnWords; // clear array and return this singular word
				}
			}
			if(this.isWord) 
				returnWords.add(this.value); // if this is a proper word, add it to the list we are returning

			for(Map.Entry<Character, TrieNode> child : children.entrySet()){
				if(child.getKey().equals(character)){ // if there is an existing child with the next char
					return child.getValue().getChild(returnWords, word, charAt+1);
				}
			}		
		}
		else{
			if(isWord)
				returnWords.add(value);
			for(Map.Entry<Character, TrieNode> child : children.entrySet()){
				ArrayList<String> predictions = new ArrayList<String>();
				predictions = child.getValue().getChild(returnWords,word, charAt+1);
				for(String p : predictions){
					if(!returnWords.contains(p))
						returnWords.add(p);
				}
			}
		}
		return returnWords;

	}

	public Map<Character, TrieNode> getChildren(){ return this.children; }
	public String getValue(){return this.value; }
}
