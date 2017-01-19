

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The class handles all the operations of fibonacci heap like
 * Insert, increaseKey, removeMax, pairwiseCombine
 * @author RITVIK
 *
 */
public class FibonacciHeapManager {

	static HeapNode root;
	static HashMap<String, HeapNode> hashTagMap;

	public FibonacciHeapManager() {
		root = null;
		hashTagMap = new HashMap<String, HeapNode>();
	}

	/**
	 * Inserts new node to Fibonacci heap or calls to increment existing
	 * The heap is implemented with the help of hashMap that stores the 
	 * value of each node in key-value pairs of hashtag-node
	 * @param hashTag
	 * @param count
	 */
	public void insert(String hashTag,int count){
		if(hashTagMap.containsKey(hashTag)){
			increaseKey(hashTag, count);
		}
		else{
			HeapNode node = new HeapNode(hashTag, count);

			if(root != null){
				node = addToRootList(node);
				if (count > root.count) 
					root = node;            
			}
			else {
				root = node;
				root.rightSibling = root;
				root.leftSibling = root;
			}
			hashTagMap.put(hashTag, node);
		}
	}

	/**
	 * Increases the key value of a node by the given value
	 * @param hashTag
	 * @param count
	 */
	private void increaseKey(String hashTag,int count){
		HeapNode node = hashTagMap.get(hashTag);
		HeapNode parentNode = node.parent;
		
		node.count+=count;
		if(null!=parentNode && node.count > parentNode.count){
			cut(hashTag);
			cascadeCut(parentNode.hashTag);
		}
		if(node.count > root.count){
			root = node;
		}
	}

	/**
	 * Performs the cut operation on a node taking hashtag
	 * of the node to cut as argument and retrieves the respective node 
	 * from the hashMap through the hashtag
	 * @param hashTag
	 */
	private void cut(String hashTag){
		HeapNode node = hashTagMap.get(hashTag);
		HeapNode parentNode = node.parent;

		node.leftSibling.rightSibling = node.rightSibling;
		node.rightSibling.leftSibling = node.leftSibling;
		parentNode.degree--;

		if(parentNode.child == node){
			parentNode.child = node.rightSibling;
		}

		if(parentNode.degree==0){
			parentNode.child = null;
		}
		node = addToRootList(node);
		node.childCut = false;
	}

	/**
	 * Performs the cascade cut operation on the parent node
	 * of the node cut previously
	 * @param parentHashTag
	 */
	private void cascadeCut(String parentHashTag){

		HeapNode parentNode = hashTagMap.get(parentHashTag);
		HeapNode grandParent = parentNode.parent;

		if(null!=grandParent){
			if(parentNode.childCut==false){
				parentNode.childCut=true;
			}
			else{
				cut(parentHashTag);
				cascadeCut(grandParent.hashTag);
			}
		}
	}

	/**
	 * The method removes from the heap the maxNode.
	 * It takes as argument the number of times max 
	 * node is to be removed from the heap.
	 * @param numberofRemoves
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> removeMax(int numberofRemoves) throws Exception{
		if(root==null)
			throw new Exception("Heap is empty");

		HeapNode maxNode;
		HashMap<String,Integer> removedItems = new HashMap<String,Integer>();
		ArrayList<String> removeOutput = new ArrayList<String>(numberofRemoves);

		for(int j=0;j<numberofRemoves;j++){
			maxNode = hashTagMap.get(root.hashTag);

			//remove childs of maxNode from parent and add to root list
			if(maxNode.child!=null){
				HeapNode firstChild = maxNode.child;
				HeapNode lastChild = maxNode.child.leftSibling;

				do{
					firstChild.parent = null;
					firstChild.childCut = false;
					firstChild=firstChild.rightSibling;
				}while(firstChild!=lastChild.rightSibling);

				firstChild = maxNode.child;
				lastChild.rightSibling = maxNode.rightSibling;
				maxNode.rightSibling = firstChild;
				firstChild.leftSibling = maxNode;
				lastChild.rightSibling.leftSibling = lastChild;

				maxNode.child = null;
				maxNode.degree = 0;
			}

			//temp allocation to to act as starting point of meld
			if(root == root.rightSibling){
				root = null;
			}else{
				root.leftSibling.rightSibling = root.rightSibling;
				root.rightSibling.leftSibling = root.leftSibling;
				root=root.rightSibling; 			
			}

			//add max node removed by removeMax operation to arrayList
			removedItems.put(maxNode.hashTag,maxNode.count);
			removeOutput.add(maxNode.hashTag); 
			hashTagMap.remove(maxNode.hashTag);
			meld(hashTagMap.get(root.hashTag));

		}

		for(Map.Entry<String,Integer> entry : removedItems.entrySet()){
			insert(entry.getKey(),entry.getValue());
		}
		return removeOutput;
	}

	/**
	 * The method takes the current root and pairwise combines the heap 
	 * @param currPointer
	 */
	private void meld(HeapNode currPointer){
		HeapNode temp;
		HeapNode nextPointer;
		int currDegree=0;
		HashMap<Integer, HeapNode> pairWiseTable = new HashMap<Integer,HeapNode>();
		int rootCount = getRootCount();
		
		//Pairwise combine the heap for all the nodes in root list
		while(rootCount>0){
			currDegree = currPointer.degree;
			nextPointer = currPointer.rightSibling;

			while(pairWiseTable.get(currDegree)!=null){
				temp = pairWiseTable.get(currDegree);

				if(temp.count > currPointer.count){
					HeapNode swap = currPointer;
					currPointer = temp;
					temp = swap;
				}
				currPointer = makeChild(currPointer,temp);
				pairWiseTable.remove(currDegree);
				currDegree++;
			}
			pairWiseTable.put(currPointer.degree, currPointer);
			currPointer = nextPointer;
			rootCount--;
		}

		//calibrate root
		root = null;
		for(Map.Entry<Integer, HeapNode> entry : pairWiseTable.entrySet()) {
			if(entry.getKey()!=null){
				HeapNode curr = entry.getValue();
				if(root!=null){
					if(curr.count > root.count){
						root = hashTagMap.get(curr.hashTag);
					}
				}else{
					root = hashTagMap.get(curr.hashTag);
				}
			}
		}
	}


	/**
	 * The function makes the second argument the child of the first arg
	 * @param parent
	 * @param child
	 */
	private HeapNode makeChild(HeapNode parent, HeapNode child){
		child.leftSibling.rightSibling = child.rightSibling;
		child.rightSibling.leftSibling = child.leftSibling;

		if(null==parent.child){
			parent.child=child;
			child.rightSibling = child;
			child.leftSibling = child;
		}else{
			child.leftSibling = parent.child;
			child.rightSibling = parent.child.rightSibling;
			parent.child.rightSibling = child;
			child.rightSibling.leftSibling = child;
		}
		child.parent = parent;
		child.childCut = false;
		parent.degree++;	
		return parent;
	}

	/**
	 * places the node in root list of heap
	 * @param node
	 * @return
	 */
	private HeapNode addToRootList(HeapNode node){
		node.leftSibling = root;
		node.rightSibling = root.rightSibling;
		root.rightSibling = node;
		node.rightSibling.leftSibling = node;

		node.parent = null;
		node.childCut = false;
		return node;
	}

	/**
	 * returns count of nodes in root list
	 * @return
	 */
	private int getRootCount(){
		int rootCount = 0;
		HeapNode pointer = root;
		
		if( pointer != null ){
			rootCount++;
			pointer = pointer.rightSibling;
			while( pointer != root ){
				rootCount++;
				pointer = pointer.rightSibling;
			}
		}
		return rootCount;
	}
}
