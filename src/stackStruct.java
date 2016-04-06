import java.util.*;

public class stackStruct {
	public Node node;
	public int reach;
	public stackStruct parent;
	public int depth;
	public Queue<Node> children;

	
	public stackStruct(Node node, int reach, stackStruct parent){
		this.node = node;
		this.reach = reach;
		this.parent = parent;
	}	
	


}
