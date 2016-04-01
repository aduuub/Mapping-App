import java.util.*;

public class queueStruct implements Comparator<queueStruct>, Comparable<queueStruct> {
	public Node node;
	public Node from;
	public double costToHere;
	public double estimateTotal;
	
	public queueStruct(Node n1, Node n2, double cost, double estTotal){
		this.node = n1;
		this.from = n2;
		this.costToHere = cost;
		this.estimateTotal = estTotal;	
	}	
	
	
	// used for priority queue comparator
	@Override
	public int compareTo(queueStruct o) {
		return  (int) (this.estimateTotal - o.estimateTotal);
	}
	
	// used for priority queue comparator
	@Override
	public int compare(queueStruct a, queueStruct b) {
		return (int) (a.estimateTotal - b.estimateTotal);
	}

}
