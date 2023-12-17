package argumentation.presumptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import argumentation.aif.FastArgumentMap;
import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class PresumptiveAAF {

	public static FastGraph makeArgumentFramework(FastGraph aif) {
	
		return makeArgumentFramework(aif, new PreferenceDefault());
	}

	public static FastGraph makeArgumentFramework(FastGraph aif, Comparator<Integer> preference) {
		
		// The arguments are the RA-Nodes and CA-Nodes
		int[] arguments = IntStream.range(0, aif.getNumberOfNodes()).filter(x -> (aif.getNodeType(x) == FastArgumentMap.RA_NODE) || (aif.getNodeType(x) == FastArgumentMap.CA_NODE) || (aif.getNodeType(x) == FastArgumentMap.MA_NODE)).toArray();

		// The node labels in the Dung AAF are the same as the corresponding AIF S-Nodes
		List<String> labels = Arrays.stream(arguments)
				.mapToObj(x -> aif.getNodeLabel(x))
				.collect(Collectors.toList());
				
		List<NodeStructure> nodes = new ArrayList<NodeStructure>();
		List<EdgeStructure> edges = new ArrayList<EdgeStructure>();
		
		Map<String, Integer> nodeMap = new HashMap<String, Integer>();
		int nodeIndex = 0;
		
		for ( String label: labels ) {
			
            nodes.add(new NodeStructure(nodeIndex, label, 0, (byte) 0, (byte) 0));
            nodeMap.put(label, nodeIndex);
            nodeIndex++;
		}
		
		int edgeIndex = 0;
		
		for ( int attacker: arguments ) {
			
			Set<Integer> attackedSet = getAttacks(aif, attacker);

			int attackerNode = nodeMap.get(aif.getNodeLabel(attacker));
			
			for ( int attacked: attackedSet ) {

				int attackedNode = nodeMap.get(aif.getNodeLabel(attacked));
				
				if (preference.compare(attacker, attacked) >= 0) {
					
					edges.add(new EdgeStructure(edgeIndex++, "", 0, (byte) 0, (byte) 0, attackerNode, attackedNode));
				}				
			}
		}
				
		nodes.sort(new Comparator<NodeStructure>() { public int compare(NodeStructure n1, NodeStructure n2) {return n1.getId() - n2.getId();} });
		return FastGraph.structureFactory("AAF", (byte) 0, nodes, edges, true);
	}
	
	
	static Set<Integer> getAttacks(FastGraph aif, int attacker) {
		
		Set<Integer> attacked = new HashSet<Integer>();
		
		byte attackerType = aif.getNodeType(attacker);		
		if ( attackerType != FastArgumentMap.RA_NODE && attackerType != FastArgumentMap.CA_NODE ) return attacked;
		
		// Get attacks between S-nodes. A CA-node attacks: an S-node that has the CA-node conclusion as the S-node premise (undermine); the S-node that is 
		// its conclusion (undercut); any RA-node that shares its conclusion (rebut). A RA-node attacks any CA-node that shares its conclusion (rebut),
		// and any CA-node that undercuts it.
		
		int [] attackOut = aif.getNodeConnectingOutNodes(attacker);		
		assert( attackOut.length == 1 ); // AIF allows only one conclusion
		
		int conclusion = attackOut[0];
		
		if ( attackerType == FastArgumentMap.CA_NODE ) {
						
			if ( (aif.getNodeType(conclusion) != FastArgumentMap.I_NODE) && (aif.getNodeType(conclusion) != FastArgumentMap.L_NODE) ) attacked.add(conclusion); // undercut
			
			else {
				int [] conclusionOut = aif.getNodeConnectingOutNodes(conclusion);		
				attacked.addAll(
						Arrays.stream(conclusionOut).mapToObj(x-> new Integer(x)).collect(Collectors.toSet())
				); // undermine
			}
		}
		
		if ( attackerType == FastArgumentMap.RA_NODE ) {
			
			// An RA-node attacks a CA-node that attacks it (undercut)

			int [] attackIn = aif.getNodeConnectingInNodes(attacker);
			
			attacked.addAll(
			Arrays.stream(attackIn)
				.filter( x -> aif.getNodeType(x) == FastArgumentMap.CA_NODE )
				.mapToObj(x -> new Integer(x))
				.collect(Collectors.toSet())
			);
		}
		
		// rebut...
		
		int[] sharingConclusion = aif.getNodeConnectingInNodes(conclusion);

		attacked.addAll(
		Arrays.stream(sharingConclusion)
			.filter( x -> (aif.getNodeType(x) != FastArgumentMap.I_NODE) && (aif.getNodeType(x) != FastArgumentMap.L_NODE) )
			.filter( x -> aif.getNodeType(x) != attackerType )
			.mapToObj(x -> new Integer(x))
			.collect(Collectors.toSet())
		);
		
		return attacked;
	}

}
