package argumentation.presumptive;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import argumentation.aif.FastArgumentMap;
import argumentation.aif.FastMapUtils;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Analysis {

 	public static Set<String> getAcceptableInformation(FastGraph aif, FastGraph framework, Set<Integer> extension) {

		Set<String> labels = new HashSet<String>();
		Map<String, Integer> map = FastMapUtils.getLabelToNodeNumber(aif);
		
		Set<String> extensionLabels = FastMapUtils.extensionLabels(framework, extension);
	
 		for ( String label: extensionLabels ) {
		   
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


	public static Set<String> getDisbelievedInformation(FastGraph aif, FastGraph framework, Set<Integer> extension) {

		Set<String> labels = new HashSet<String>();
		Map<String, Integer> map = FastMapUtils.getLabelToNodeNumber(aif);
		
		Set<String> extensionLabels = FastMapUtils.extensionLabels(framework, extension);
		
 		for ( String label: extensionLabels ) {
		   
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


	public static Map<Set<String>, Set<Set<Integer>>> partitionExtensions(FastGraph aif, FastGraph framework, Set<Set<Integer>> extensions, Set<String> hypotheses) {
		
        // Partition extensions according to the hypotheses they contain.
 		// Each partition is a set of extensions (with each extension a set of integers).
		// The key for each a partition is the intersection of hypotheses (resources) with the acceptable information from some extension.

		Map<Set<String>, Set<Set<Integer>>> partitions = new HashMap<Set<String>, Set<Set<Integer>>>();
		
 		for ( Set<Integer> extension: extensions ) {
 			
 			Set<String> acceptableInfo = getAcceptableInformation(aif, framework, extension);
 			acceptableInfo.retainAll(hypotheses);
 			
 			Set<Set<Integer>> partition = partitions.get(acceptableInfo);
 			if (partition == null )  partition = new HashSet<Set<Integer>>();
 			partition.add(extension);
 			partitions.put(acceptableInfo, partition);
 		}
		
		return partitions;		
	}

	
	public static Map<String, Set<Set<Integer>>>  getInformationObjects(FastGraph aif, FastGraph framework, Set<Set<Integer>> extensions) {
		
		// Treat each AIF information node as an intent, with the extensions that contain it (i.e. for which it is acceptable information)
		// as its extent. The result is a Map of extensions keyed by information node label, suitable for Formal Concept Analysis.

		Map<String, Set<Set<Integer>>> map = new HashMap<String, Set<Set<Integer>>>();
        
        for ( Set<Integer> extension: extensions ) {
        	
         	Set<String> acceptableInfo = getAcceptableInformation(aif, framework, extension);
        	
        	for ( String info: acceptableInfo ) {
        		
        		Set<Set<Integer>> extents = map.get(info);
        		if ( extents == null )   extents = new HashSet<Set<Integer>>();
        		extents.add(extension);
        		map.put(info, extents);
        	}
        }
		
		return map;
	}

	
	public static Set<String> findConclusions(FastGraph aif, Set<String> labels) {
		
		// Given a set of AIF information node labels, return those where the node is only a conclusion (i.e. no out edges)
		
		Set<String> conclusions = FastMapUtils.getConclusions(aif);
		conclusions.retainAll(labels);
		return conclusions;
	}

}
