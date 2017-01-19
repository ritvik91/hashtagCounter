/**
 * The class acts as a bean class for fibonacci heap node
 * @author RITVIK
 *
 */
public class HeapNode {
	Boolean childCut;
	HeapNode leftSibling;
	HeapNode rightSibling;
	HeapNode child;
	HeapNode parent;
	String hashTag;
	int count;
	int degree;
	
	public HeapNode(String hashTag,int count) {
		super();
		this.hashTag = hashTag;
		this.count = count;
	}


}
