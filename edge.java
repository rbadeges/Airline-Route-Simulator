/**
 *edge a class representing an edge in a directed graph
 *Needs getters/setters
 */

public class edge
	{
		int src, dest;
		int miles;
		double price;
		
		/**
		 *Constructor
		 *@param s integer ID of the source city
		 *@param d integer ID of the destination city
		 *@param m distance (miles) between cities
		 *@param p price (dollars) between cities
		 */
		public edge(int s, int d, int m, double p)
		{
			src = s;
			dest = d;
			miles = m;
			price = p;
		}
	}