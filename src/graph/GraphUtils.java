package graph;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class GraphUtils {

	public static void induceGraph(FastGraph g, HashSet<Integer> nodes, HashSet<Integer> edges) {
		
		// Induce a subgraph of g from the given nodes and/or edges. This is done by adding 'missing'
		// nodes and edges to the supplied sets. FastGraph.generateGraphFromSubgraph() can then be used to
		// create a FastGraph object from the results.
		
		// Ensure that the node set has the nodes at each end of every supplied edge
		
		for (int e: edges) {
			
			nodes.add(g.getEdgeNode1(e));
			nodes.add(g.getEdgeNode2(e));			
		}
		
		// Add edges between any pair of nodes in the node set.
		
		boolean[] visitedEdges = new boolean[g.getNumberOfEdges()];
		
		//for every node in the graph
		for(int n : nodes) {
			//find what edges it connects to
			int[] edgeConnections = g.getNodeConnectingEdges(n);
			//for each of these connecting edges
			for (int ce : edgeConnections) {
				//find the other node
				int cn = g.oppositeEnd(ce,n);
				//if that node is also in the graph and this edge hasn't been visited already, then add the edge
				if (nodes.contains(cn) && !visitedEdges[ce]) {
					edges.add(ce);
					visitedEdges[ce] = true;
				}
			}			
		}

		return;
	}


 	public static void dumpGraph(FastGraph graph, String filename) {
 		
 		try {
 			CSVFormat csvFileFormat = CSVFormat.DEFAULT;
 	 		FileWriter writer = new FileWriter(filename);
 	 		CSVPrinter csvPrinter = new CSVPrinter(writer, csvFileFormat);

 	 		csvPrinter.printRecord(new Object[] {"from", "fromType", "to", "toType", "edgeLabel"});
 	 		
 	 		for ( int i = 0; i < graph.getNumberOfEdges(); i++ ) {
 	 			
 	 			int n1= graph.getEdgeNode1(i);
 	 			int n2= graph.getEdgeNode2(i);
 	 		
 	 			List<String> record = new ArrayList<String>();
 	 			record.add(graph.getNodeLabel(n1));
 	 			record.add(String.format("%d", graph.getNodeType(n1)));
 	 			record.add(graph.getNodeLabel(n2));
 	 			record.add(String.format("%d", graph.getNodeType(n2)));
 	 			record.add(String.format("%s", graph.getEdgeLabel(i)));
 	 			
 	 			csvPrinter.printRecord(record);
 	 		}
 	 		
 	 		writer.flush();
 	 		writer.close();
 	 		csvPrinter.close();
 		}
 		catch(Exception e) {
 			e.printStackTrace();;		
 		}
 	}

}
