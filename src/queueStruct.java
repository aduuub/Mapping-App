import java.util.*;

public class queueStruct implements Comparator<queueStruct>, Comparable<queueStruct> {
	public Node node;
	public queueStruct structFrom;
	public Segment segmentFrom;
	public Node from;

	public double costToHere;
	public double estimateTotal;
	
	public queueStruct(Node node, Node from, double cost, double estTotal, queueStruct structFrom, Segment segmentFrom){
		this.node = node;
		this.from = from;
		this.structFrom = structFrom;
		this.segmentFrom = segmentFrom;
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
