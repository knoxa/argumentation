package argumentation.aif;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class FastMapUtils {

    public static Set<Set<String>> extensionSetLabels(FastGraph framework, Set<Set<Integer>> extensions) {
    	
    	Set<Set<String>> labelExtensions = new HashSet<Set<String>>();
    	
    	for ( Set<Integer> extension: extensions ) {
    		
    		labelExtensions.add(extensionLabels(framework, extension));
    	}
    	
		return labelExtensions;
    }

    
    public static Set<String> extensionLabels(FastGraph framework, Set<Integer> extension) {

     	return extension.stream().map(x -> framework.getNodeLabel(x)).collect(Collectors.toSet());  	
    }

    
    public static Map<String, Integer> getLabelToNodeNumber(FastGraph aif) {
    	
    	Map<String, Integer> map = new HashMap<String, Integer>();
		IntStream.range(0, aif.getNumberOfNodes()).forEach( x -> map.put(aif.getNodeLabel(x), x) );
    	return map;
    }

    
 	public static Set<String> getEvidence(FastGraph aif) {

		Set<String> labels = new HashSet<String>();
		
		IntStream.range(0, aif.getNumberOfNodes())
		.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE || aif.getNodeType(x) == FastArgumentMap.L_NODE) && aif.getNodeInDegree(x) == 0)
		.forEach(x -> labels.add(aif.getNodeLabel(x)));

		return labels;
 	}

 	
 	public static Set<String> getInformation(FastGraph aif) {

		Set<String> labels = new HashSet<String>();
		
		IntStream.range(0, aif.getNumberOfNodes())
		.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE) || (aif.getNodeType(x) == FastArgumentMap.L_NODE))
		.forEach(x -> labels.add(aif.getNodeLabel(x)));

		return labels;
 	}

}
