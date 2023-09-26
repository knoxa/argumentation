package argumentation.presumptive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import argumentation.aif.FastArgumentMap;
import argumentation.aif.FastMapUtils;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Analysis {

 	public static Set<String> getAcceptableInformation(FastGraph aif, Set<String> extension) {

		Set<String> labels = new HashSet<String>();
		Map<String, Integer> map = FastMapUtils.getLabelToNodeNumber(aif);
		
 		for ( String label: extension ) {
		   
			int aifNode = map.get(label);
			
			if ( aif.getNodeType(aifNode) == FastArgumentMap.CA_NODE ) {
			// premises are acceptable if they are "evidence" (no incoming edges) ???
				
				int[] inodes = aif.getNodeConnectingInNodes(aifNode);

				Arrays.stream(inodes)
					.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE) || (aif.getNodeType(x) == FastArgumentMap.L_NODE))
					//.filter(x -> aif.getNodeInDegree(x) == 0)
					.forEach(x -> labels.add(aif.getNodeLabel(x)));
			}
			else { // RA-node: premises and conclusions are acceptable
				
				int[] inodes = aif.getNodeConnectingNodes(aifNode);

				Arrays.stream(inodes)
					.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE) || (aif.getNodeType(x) == FastArgumentMap.L_NODE))
					.forEach(x -> labels.add(aif.getNodeLabel(x)));
				
			}
 		}
  	
		return labels;
 	}


	public static Set<String> getDisbelievedInformation(FastGraph aif, Set<String> extension) {

		Set<String> labels = new HashSet<String>();
		Map<String, Integer> map = FastMapUtils.getLabelToNodeNumber(aif);
		
 		for ( String label: extension ) {
		   
			int aifNode = map.get(label);
			
			if ( aif.getNodeType(aifNode) == FastArgumentMap.CA_NODE ) {

				// we don't believe the conclusions of acceptable CA-nodes
				int[] inodes = aif.getNodeConnectingOutNodes(aifNode);

				Arrays.stream(inodes)
					.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE) || (aif.getNodeType(x) == FastArgumentMap.L_NODE))
					.forEach(x -> labels.add(aif.getNodeLabel(x)));
				
				// Maybe we believe the conclusions of successfully attacked RA-nodes?
				// (would mean analysing AIF in conjunction with DUng Framework)
				// ...
			}
 		}
  	
		return labels;
 	}

}
