/**
 *minHeap an implementation of a priority queue
 *built on an array representation of a binary tree
 *Only works with edge objects :(
 *Edges sorted based on mile weight, not price.
 */

public class minHeap
	{
		edge heap[];
		int size;
		int n;
		
		/**
		 *Constructor
		 *@param s the original size of the heap (will be adjusted if heap is full)
		 */
		public minHeap(int s)
		{
			n = 0;
			size = s;
			heap = new edge[size];
		}
		
		/**
		 *add adds a new edge to the heap
		 *@param newEdge the edge to be added
		 */
		public void add(edge newEdge)
		{
			//add new entry to heaps
			heap[n++] = newEdge;
			int i = n-1;
			swim(i);
			//check if heap is full
			if (n == size)
			{
				size*=2;
				edge[] newHeap = new edge[size];
				for (int x = 0; x < n; x++)
					newHeap[x] = heap[x];
				heap = newHeap;
			}
		}
		
		/**
		 *get returns the minimum edge in the heap
		 */
		public edge get()
		{
			edge minEdge = heap[0];
			heap[0] = heap[n-1];
			sink(0);
			n--;
			return minEdge;
		}
		
		/**
		 *swim restores heap order
		 *@param i the index of the edge in the heap that needs to be moved
		 */
		private void swim(int i)
		{
			edge curr = heap[i];
			if (n > 1)
			{
				edge parent = heap[(i-1)/2];
				while (curr.miles < parent.miles)
				{
					heap[i] = parent;
					heap[(i-1)/2] = curr;
					i = (i-1)/2;
					parent = heap[(i-1)/2];
				}
			}
		}
		
		/**
		 *sink restores heap order
		 *@param i the index of the edge in the heap that needs to be moved
		 */
		private void sink(int i)
		{
			edge curr = heap[i];
			edge lChild = null, rChild = null;
			if ((i*2+1) < n)
				lChild = heap[i*2+1];
			if ((i*2+2) < n)
				rChild = heap[i*2+2];
			while ((lChild!=null && rChild!=null) && (curr.miles > lChild.miles || curr.miles > rChild.miles))
			{
				if (curr.miles > lChild.miles && curr.miles > rChild.miles)
				{
					if (lChild.miles < rChild.miles)
					{
						heap[i*2+1] = curr;
						heap[i] = lChild;
						i = i*2+1;
					}
					else
					{
						heap[i*2+2] = curr;
						heap[i] = rChild;
						i = i*2+2;
					}
				}
				else if (curr.miles > lChild.miles)
				{
					heap[i*2+1] = curr;
					heap[i] = lChild;
					i = i*2+1;
				}
				else
				{
					heap[i*2+2] = curr;
					heap[i] = rChild;
					i = i*2+2;
				}
				//update child references
				if (i*2+1 < n) lChild = heap[i*2+1];
				else lChild = null;
				if (i*2+2 < n) rChild = heap[i*2+2];
				else rChild = null;
			}
			if (lChild!=null && curr.miles > lChild.miles)
			{
				heap[i] = lChild;
				heap[i*2+1] = curr;
				i = i*2+1;
			}
		}
	}