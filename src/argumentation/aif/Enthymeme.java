package argumentation.aif;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class Enthymeme {
	
	// A set of flags for the status of nodes in the supplied AIF:
	private static int WANTED = 1, UNWANTED = -1, UNKNOWN = 0; 

	public static FastGraph pruneArgumentMap(FastGraph aif, Set<String> evidence) {

		// Make a subgraph of the given AIF that only takes account of premises grounded in evidence.
		// Leaf I-nodes are iteratively pruned from the AIF argument map unless they also appear in the evidence set.
		// Node labels in the returned FastGraph (but not node numbers) will match those in the parent argument map.
		
		int numNodes = aif.getNumberOfNodes();
		int[] labels = new int[numNodes];
		Arrays.fill(labels, UNKNOWN);

		// We want all "evidence" nodes in the AIF. These are I-nodes with no incoming
		// edges that can be found in the evidence model.
		
		IntStream.range(0, numNodes)
				.filter(x -> aif.getNodeType(x) == FastArgumentMap.I_NODE && aif.getNodeConnectingInEdges(x).length == 0)
				.forEach(x -> labels[x] = evidence.contains(aif.getNodeLabel(x)) ? WANTED : UNWANTED);

		// Iterate through the AIF, propagating decisions about whether nodes are wanted
		// or not, until all nodes have been labelled either WANTED or UNWANTED.

		while (hasUndecided(labels)) {

			for (int n = 0; n < numNodes; n++) {

				if (aif.getNodeType(n) != FastArgumentMap.I_NODE && labels[n] == UNKNOWN) {

					// An S-node is UNWANTED if any of its parent I-nodes are UNWANTED
					// An S-node is WANTED if all its parent I-nodes WANTED

					int[] inNodes = aif.getNodeConnectingInNodes(n);
					
					if (IntStream.range(0, inNodes.length)
							.filter(x -> aif.getNodeType(inNodes[x]) == FastArgumentMap.I_NODE)
							.anyMatch(x -> labels[inNodes[x]] == UNWANTED))
						labels[n] = UNWANTED;
					
					if (IntStream.range(0, inNodes.length)
							.filter(x -> aif.getNodeType(inNodes[x]) == FastArgumentMap.I_NODE)
							.allMatch(x -> labels[inNodes[x]] == WANTED))
						labels[n] = WANTED;
				}
			}

			for (int n = 0; n < numNodes; n++) {

				if (aif.getNodeType(n) == FastArgumentMap.I_NODE && labels[n] == UNKNOWN) {

					// An I-node is UNWANTED if all its parents are UNWANTED
					// An I-node is WANTED if any of its parents are WANTED

					int[] inNodes = aif.getNodeConnectingInNodes(n);
					
					if (IntStream.range(0, inNodes.length).allMatch(x -> labels[inNodes[x]] == UNWANTED))  labels[n] = UNWANTED;				
					if (IntStream.range(0, inNodes.length).anyMatch(x -> labels[inNodes[x]] == WANTED))    labels[n] = WANTED;
				}
			}
		}
		
		Set<Integer> wantedNodes = IntStream.range(0, numNodes)
				.filter(x -> labels[x] == WANTED)
				.boxed().collect(Collectors.toSet());

		Set<Integer> wantedEdges = IntStream.range(0, aif.getNumberOfEdges())
				.filter(x -> wantedNodes.contains(aif.getEdgeNode1(x)))
				.filter(x -> wantedNodes.contains(aif.getEdgeNode2(x)))
				.boxed().collect(Collectors.toSet());
		
		int[] nodes = wantedNodes.stream().mapToInt(Integer::intValue).toArray();
		int[] edges = wantedEdges.stream().mapToInt(Integer::intValue).toArray();
		
		FastGraph subgraph = aif.generateGraphFromSubgraph(nodes, edges);

		return subgraph;
	}

	
	private static boolean hasUndecided(int[] labels) {

		return Arrays.stream(labels).anyMatch(x -> x == UNKNOWN);
	}

}
