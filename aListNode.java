/**
 *aListNode a class representing a node in an adjacency list
 *Needs getters/setters
 */

public class aListNode
{
	double price;
	int miles;
	aListNode next;
	String dest;
	int destID;
	/**
	 *Constructor 
	 *@param d string representation of the destination city
	 *@param dID integer representation of the destination city
	 *@param m distance in miles to destination city
	 *@param p price in dollars to destination city
	 */
	public aListNode(String d, int dID, int m, double p)
	{
		price = p;
		miles = m;
		dest = d;
		destID = dID;
		next = null;
	}
}