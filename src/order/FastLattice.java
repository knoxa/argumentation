package order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class FastLattice {

	public static FastGraph getGraph(Map<Integer, Set<Integer>> lattice, int numNodes) {
		
		List<NodeStructure> nodes = makeNodes(numNodes);
		List<EdgeStructure> edges = makeEdges(lattice);

		nodes.sort(new Comparator<NodeStructure>() {
			public int compare(NodeStructure n1, NodeStructure n2) {
				return n1.getId() - n2.getId();
			}
		});

		return FastGraph.structureFactory("FCA", (byte) 0, nodes, edges, true);
	}
	
	private static List<NodeStructure> makeNodes(int num) {
	
		List<NodeStructure> nodeStructures = new ArrayList<NodeStructure>();

		for ( int n = 0; n < num; n++ ) {
			nodeStructures.add(new NodeStructure(n, String.valueOf(n), 0, (byte) 0, (byte) 0));
		}
		
		return nodeStructures;
	}
	
	private static List<EdgeStructure> makeEdges(Map<Integer, Set<Integer>> lattice) {
	
		List<EdgeStructure> edgeStructures = new ArrayList<EdgeStructure>();
		 int eid = 0;
		for (Integer fm: lattice.keySet() ) {
			
			for ( Integer to: lattice.get(fm) ) {
				
				edgeStructures.add( new EdgeStructure(eid++, "", 0, (byte) 0, (byte) 0, fm, to) );
			}
		}
		return edgeStructures;
	}
}
