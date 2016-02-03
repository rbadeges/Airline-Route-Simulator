import java.io.*;
import java.util.*;

/**
 *A class used to read in flight information and plan trips based on distance and price between cities
 *Files containing flight data must be formatted with number of cities first, followed by the name of each city, followed by
 *	the routes represented as pairs of integers (cities), the distance between them, and the price between them.
 */
public class Airline
{
	static aListNode aList[]; //adjacency list to store graph
	static Scanner scanner;
	static String cities[]; //cities in graph
	static int numCities;   //number of cities
	static int numRoutes;   //number of routes
	public static void main(String args[]) throws FileNotFoundException
	{
		scanner = new Scanner(System.in);
		System.out.println("Enter name of file containing route data");
		String filename = scanner.nextLine();
		File input = new File(filename);
		Scanner fileScanner = new Scanner(input);
		numCities = 0;
		numRoutes = 0;
		String thisLine = fileScanner.nextLine();
		numCities = Integer.parseInt(thisLine);
		cities = new String[numCities];
		aList = new aListNode[numCities];
		int from, to;
		//init cities array
		for (int i = 0; i < numCities; i++)
		{
			thisLine = fileScanner.nextLine();
			cities[i] = thisLine;
			aList[i] = null;
		}
		//init adjacency list
		while(fileScanner.hasNext())
		{
			int src = fileScanner.nextInt();
			int dest = fileScanner.nextInt();
			int miles = fileScanner.nextInt();
			double price = fileScanner.nextDouble();
			aListNode edge = new aListNode(cities[dest-1], dest -1, miles, price);
			if (aList[src-1] == null)
				aList[src-1] = edge;
			else
			{
				aListNode tmp = aList[src-1];
				while(tmp.next != null)
					tmp = tmp.next;
				tmp.next = edge;
			}
			edge = new aListNode(cities[src-1], src-1, miles, price);
			if (aList[dest-1] == null)
				aList[dest-1] = edge;
			else
			{
				aListNode tmp = aList[dest-1];
				while (tmp.next != null)
					tmp = tmp.next;
				tmp.next = edge;
			}
			numRoutes++;
		}
		boolean quit = false;
		//main menu loop
		while(!quit)
		{
			System.out.println("You are at the Main Menu. Your options are:\n" +
							"(1): show all routes\n(2): show minimum spanning tree\n" +
							"(3): find shortest path by miles, price, or hops\n" +
							"(4): find all trips available for under given price\n" +
							"(5): add or remove a route\n(6): quit\n");
			String in = scanner.nextLine();
			switch (Integer.parseInt(in))
			{
				//print all routes
				case 1:
					for (int i = 0; i < numCities; i++)
					{
						aListNode edge = aList[i];
						while (edge != null)
						{
							System.out.format("%-15.15s",cities[i]);
							System.out.print(" to ");
							System.out.format("%-15.15s",edge.dest+": ");
							System.out.format("%3d", edge.miles);
							System.out.print(" miles, $");
							System.out.println(edge.price);
							edge = edge.next;
						}
					}
					System.out.println();
				break;
				//get minimum spanning tree based on distance b/w cities
				case 2:
					ArrayList<ArrayList<edge>> MSF = mst(); //get minimum spanning forest, a collection of minimum spanning trees
					//if more than one tree in forest, graph is not connected
					if (MSF.size() > 1 )
					{
						int component = 1;
						System.out.println("Graph is not connected");
						for (ArrayList<edge> MST: MSF)
						{
							System.out.println("Component " + component+": ");
							component++;
							for ( edge e: MST )
							{
								System.out.format("%-15.15s",cities[e.src]);
								System.out.print(" to ");
								System.out.format("%-15.15s",cities[e.dest]+": ");
								System.out.format("%3d", e.miles);
								System.out.print(" miles, $");
								System.out.println(e.price);
							}
						}
					}
					else
					{
						for (edge e: MSF.get(0))
						{
							System.out.format("%-15.15s",cities[e.src]);
							System.out.print(" to ");
							System.out.format("%-15.15s",cities[e.dest]+": ");
							System.out.format("%3d", e.miles);
							System.out.print(" miles, $");
							System.out.println(e.price);
						}
					}
					System.out.println();
				break;
				//find shortest path b/w two cities, either by price, distance, or number of hops
				case 3:
					from = getCity(scanner, true);
					to = getCity(scanner, false);
					if (from == to) System.out.println("SOURCE AND DESTINATION ARE THE SAME.  SHORTEST PATH IS TO JUST STAY HOME");
					else
					{
						ArrayList<edge> path = new ArrayList<edge>(); //a list of edges representing path between cities
						System.out.println("Base shortest path on (d)istance, (p)rice, or (n)umber of flights?");
						String c = scanner.nextLine();
						switch(c.charAt(0))
						{
							//base path on distance
							case 'd':
								path = shortestPathMiles(from, to);
								int totalDist = 0;
								for (edge e: path)
								{
									totalDist += e.miles;
									System.out.format("%s -> %s (%d miles) %n", cities[e.src], cities[e.dest], e.miles);
								}
								System.out.println("Total distance: "+totalDist+" miles");
								System.out.println();
							break;
							//base path on price
							case 'p':
								path = shortestPathPrice(from, to);
								double totalPrice = 0;
								for (edge e: path)
								{
									totalPrice += e.price;
									System.out.format("%s -> %s ($%.2f)%n", cities[e.src], cities[e.dest], e.price);
								}
								System.out.format("Total cost: $%.2f%n",totalPrice);
								System.out.println();
							break;
							//base path on number of hops
							case 'n':
								path = shortestPathHops(from, to);
								int totalFlights = path.size();
								if (totalFlights == 0)
								{
									System.out.println("Sorry, path does not exist");
									break;
								}
								for (edge e: path)
									System.out.format("%s -> %s %n", cities[e.src], cities[e.dest]);
								System.out.format("Total number of flights: %d%n",totalFlights);
								System.out.println();
							break;
							default: System.out.println("INVALID INPUT");
						}
					}
				break;
				//find all paths through graph available for <= given price
				case 4:
					System.out.println("Enter Budget:");
					double budg = scanner.nextDouble();
					for (int i = 0; i < numCities; i++)
						findAllTrips(budg, i, new int[numCities], 0, 0);
					scanner.nextLine();
				break;
				//add or remove a route b/w two cities
				case  5:
					System.out.println("(a)dd or (r)emove a route?");
					in = scanner.nextLine();
					from = getCity(scanner, true);
					to = getCity(scanner, false);
					switch(in.charAt(0))
					{	
						//add a route
						case 'a':
							System.out.println("Enter distance in miles for new route");
							int newM = scanner.nextInt();
							System.out.println("Enter cost for new route");
							double newP = scanner.nextDouble();
							add(from, to, newM, newP);
							scanner.nextLine();
						break;
						//remove a route
						case 'r':
							remove(from, to);
							remove(to, from);
						break;
						default:
							System.out.println("INVALID INPUT");
					}
				break;
				//quit
				case 6:
					quit = true;
					save(input);
				break;
				default:
					System.out.println("INVALID INPUT");
			}
		}
	}
	
	/**
	 *mst uses a minHeap and lazy prim's to generate minimum spanning forest based on distance
	 */
	private static ArrayList<ArrayList<edge>> mst()
	{
		ArrayList<ArrayList<edge>> MSF = new ArrayList<ArrayList<edge>>(); 
		minHeap pq = new minHeap(numRoutes);
		boolean marked[] = new boolean[numCities]; //tracks which cities have been visited
		ArrayList<Integer> unseen = new ArrayList<Integer>();
		MSF.add(prim(0, new ArrayList<edge>(), pq, marked)); //generate mst from first city
		//check if any cities unreachable from first
		for (int x = 0; x < numCities; x++)
			if (!marked[x]) unseen.add(new Integer(x));
		while (unseen.size() > 0)
		{
			MSF.add(prim(unseen.remove(0), new ArrayList<edge>(), pq, marked));
			for (int x = 0; x < unseen.size(); x++)
				if (marked[unseen.get(x)]) unseen.remove(x);
		}
		return MSF;
	}
	/**
	 *prim generates a minimum spanning tree from a given source node
	 *@param v the source node
	 *@param MST a list of edges that will represent the mst
	 *@param pq a minHeap used to order edges
	 *@param marked tracks which cities have already been visited in the mst
	 */
	private static ArrayList<edge> prim(int v, ArrayList<edge> MST, minHeap pq, boolean marked[])
	{
		//add all edges from source to pq
		scan(pq, v, marked);
		//while edges out of source exist
		while (pq.n > 0)
		{
			edge thisEdge = pq.get(); //dequeue next edge
			if ((marked[thisEdge.src] || marked[thisEdge.dest]) && !(marked[thisEdge.src] && marked[thisEdge.dest])) //if one endpoint not yet seen
			{
				//add edge to MST
				MST.add(thisEdge);
				if (!marked[thisEdge.src]) scan(pq, thisEdge.src, marked);
				if (!marked[thisEdge.dest]) scan(pq, thisEdge.dest, marked);
			}
		}
		return MST;
	}
	
	/**
	 *scan enqueues all edges with destination not yet visited to pq used by prim function
	 *@param pq used by prim function
	 *@param v current node being considered
	 *@param marked tracks which cities have been visited
	 */
	private static void scan(minHeap pq, int v, boolean marked[])
	{
		marked[v] = true;
		aListNode source = aList[v];
		while (source != null) //add all edges from v to pq
		{
			if (!marked[source.destID]) //if endpoint not seen yet
			{
				edge thisEdge = new edge(v, source.destID, source.miles, source.price);
				pq.add(thisEdge);
			}
			source = source.next;
		}
	}
	
	/**
	 *findAllTrips recursively prints all trips available for <= given budget
	 *@param price the remaining budget
	 *@param src the current city in trips
	 *@param route the full current trip
	 *@param numHops the number of flights in trip
	 *@param totalPrice the total cost of the trip
	 */
	private static void findAllTrips(double price, int src, int route[], int numHops, double totalPrice)
	{
		boolean visited;
		route[numHops++] = src;
		if (numHops > 1)
		{
			for (int x = 0; x < numHops; x++)
			{
				System.out.print(cities[route[x]]+", ");
			}
			System.out.println("Total Price: $"+totalPrice);
		}
		aListNode s = aList[src];
		while (s!=null)
		{
			visited = false;
			for (int x = 0; x < numHops; x++)
				if (route[x] == s.destID) //skip edge if seen that city already
				{	
					visited = true;
					break;
				}
			price -= s.price;
			if (price >= 0 && !visited) findAllTrips(price, s.destID, route, numHops, totalPrice + s.price);
			price += s.price;
			s = s.next;
		}
	}
	
	/**
	 *shortestPathMiles finds shortest path b/w two cities based on distance (Djikstra's algorithm)
	 *@param src the source city
	 *@param dest the destination city
	 */
	private static ArrayList<edge> shortestPathMiles(int src, int dest)
	{
		edge parents[] = new edge[numCities]; //the last edge in path to each city
		int distTo[] = new int[numCities];	  //the distance to each city
		int v = src;
		int nextV = v;
		boolean seen[] = new boolean[numCities]; //cities considered for next hop
		boolean found = false;					 //true when path has been found
		aListNode tmp;
		//init all distances to infinite 
		for (int i = 0; i < numCities; i++)
		{
			parents[i] = null;
			distTo[i] = Integer.MAX_VALUE;
		}
		distTo[src] = 0;
		while (!found)
		{
			seen[v] = true;
			tmp = aList[v];
			while (tmp != null) //for every edge out of v
			{
				if (distTo[tmp.destID] > distTo[v] + tmp.miles) //if this edge is shortest path to that node
				{
					distTo[tmp.destID] = distTo[v] + tmp.miles; 
					parents[tmp.destID] = new edge(v, tmp.destID, tmp.miles, tmp.price);
				}
				tmp = tmp.next;
			}
			//find minimum path to unseen node
			for (int i = 0; i < numCities; i++)
			{
				if (!seen[i] && (distTo[i] < distTo[nextV] || seen[nextV]))
					nextV = i;
			}
			if (nextV == dest)
				break;
			v = nextV;
		}
		ArrayList<edge> path = new ArrayList<edge>();
		while (nextV != src)
		{
			path.add(0, parents[nextV]);
			nextV = parents[nextV].src;
		}
		
		return path;
	}
	/**
	 *shortestPathPrice finds minimum weight path b/w two cities based on price (Djikstra's algorithm)
	 *@param src the source city
	 *@param dest the dest city
	 */
	private static ArrayList<edge> shortestPathPrice(int src, int dest)
	{
		edge parents[] = new edge[numCities];
		double distTo[] = new double[numCities];
		int v = src;
		int nextV = v;
		boolean seen[] = new boolean[numCities];
		boolean found = false;
		aListNode tmp;
		for (int i = 0; i < numCities; i++)
		{
			parents[i] = null;
			distTo[i] = Integer.MAX_VALUE;
		}
		distTo[src] = 0;
		while (!found)
		{
			seen[v] = true;
			tmp = aList[v];
			while (tmp != null) //for every edge out of v
			{
				if (distTo[tmp.destID] > distTo[v] + tmp.price) //if this edge is shortest path to that node
				{
					distTo[tmp.destID] = distTo[v] + tmp.price; 
					parents[tmp.destID] = new edge(v, tmp.destID, tmp.miles, tmp.price);
				}
				tmp = tmp.next;
			}
			//find minimum path to unseen node
			for (int i = 0; i < numCities; i++)
			{
				if (!seen[i] && (distTo[i] < distTo[nextV] || seen[nextV]))
					nextV = i;
			}
			if (nextV == dest)
				break;
			v = nextV;
		}
		ArrayList<edge> path = new ArrayList<edge>();
		while (nextV != src)
		{
			path.add(0, parents[nextV]);
			nextV = parents[nextV].src;
		}
		
		return path;
	}
	
	/**
	 *shortestPathHops finds the shortest path between two cities based on number of cities traversed (Breadth First Search)
	 *@param src the source city
	 *@param dest the destination city
	 */
	private static ArrayList<edge> shortestPathHops(int src, int dest)
	{
		ArrayList<Integer> cityQ = new ArrayList<Integer>(); //queue of cities seen during graph traversal
		ArrayList<edge> path = new ArrayList<edge>();
		aListNode tmp = aList[src];
		boolean seen[] = new boolean[numCities]; //seen[x] == true if path to that city found
		int parents[] = new int[numCities];		//parents[x] == last node in path to that city
		int curr;								//curr == current city in path
		
		cityQ.add(new Integer(src));
		boolean found = false;
		seen[src] = true;
		
		while (!found && cityQ.size() > 0)
		{
			//dequeue next node
			curr = cityQ.remove(0);
			tmp = aList[curr];
			//enqueue all unseen neighbors
			while (tmp != null)
			{
				if (!seen[tmp.destID])
				{
					seen[tmp.destID] = true;
					parents[tmp.destID] = curr;
					if (tmp.destID == dest)
					{
						found = true;
						break;
					}
					cityQ.add(new Integer(tmp.destID));
				}
				if (found) break;
				tmp = tmp.next;
			}	
		}
		int i = dest;
		if (!seen[i]) return path; //path does not exist
		while (i != src)
		{
			path.add(0, new edge(parents[i], i, tmp.miles, tmp.price));
			i = parents[i];
		}
		return path;
	}
	/**
	 *add adds a new route between cities
	 *@param src the source city
	 *@param dest the destination city
	 *@param miles the distance between the cities
	 *@param price the price of the route
	 */
	private static void add(int src, int dest, int miles, double price)
	{
		aListNode tmp = aList[src];
		while (tmp.next!=null)
		{
			if (tmp.destID == dest)
			{
				System.out.println("Route already exists...updating distance and price data");
				tmp.price = price;
				tmp.miles = miles;
				break;
			}
			tmp = tmp.next;
		}
		if (tmp.next == null)
		{
			aListNode newRoute = new aListNode(cities[dest], dest, miles, price);
			tmp.next = newRoute;
		}
	}
	
	/**
	 *remove removes a route from the graph
	 *@param src the source city
	 *@param dest the destination city
	 */
	private static void remove(int src, int dest)
	{
		aListNode tmp = aList[src];
		if (tmp.destID == dest)
		{
			aList[src] = tmp.next;
			return;
		}
		while (tmp.next.destID != dest)
			tmp = tmp.next;
		tmp.next = tmp.next.next;
	}
	
	/**
	 *getCity prompts user for city name and converts to city IDLEntity
	 *@param scanner a Scanner used to parse user input
	 *@param isSrc denotes whether the user is entering the source or destination city
	 */
	private static int getCity(Scanner scanner, boolean isSrc)
	{
		int i = -1;
		while (i == -1)
		{
			if (!isSrc) System.out.println("Enter destination city");
			else System.out.println("Enter source city");
			String s = scanner.nextLine();
			for (int x = 0; x < numCities; x++)
			{
				if (cities[x].equals(s))
				{
					i = x;
					break;
				}
			}
			if (i == -1)
				System.out.println("ERROR: CITY NOT FOUND");
		}
		return i;
	}
	
	/**
	 *save writes the graph data back to original airline data file
	 *@param out the File object representing the data file
	 */
	private static void save(File out)
	{
		out.delete();
		try
		{
			out.createNewFile();
			FileWriter writer = new FileWriter(out);
			aListNode tmp;
			String thisLine = "";
			thisLine += numCities;
			writer.write(thisLine+"\n", 0, thisLine.length()+1);
			for (int curr = 0; curr < numCities; curr++)	
				writer.write(cities[curr]+"\n", 0, cities[curr].length()+1);
			for (int curr = 1; curr <= numCities; curr++)
			{	
				tmp = aList[curr-1];
				while(tmp != null)
				{
					if (!(tmp.destID < curr))
					{
						thisLine = curr + " " + (tmp.destID+1) +" "+ tmp.miles +" "+ tmp.price + "\n";
						writer.write(thisLine, 0, thisLine.length());
					}
					tmp = tmp.next;
				}
			}
			writer.close();
		}
		catch (IOException e)
		{
			System.err.format("IOException: %s%n", e);
		}
		
	}
		
}