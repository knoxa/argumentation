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

    
 	public static Set<String> getConclusions(FastGraph aif) {

		Set<String> labels = new HashSet<String>();
		
		IntStream.range(0, aif.getNumberOfNodes())
		.filter(x -> (aif.getNodeType(x) == FastArgumentMap.I_NODE || aif.getNodeType(x) == FastArgumentMap.L_NODE) && aif.getNodeOutDegree(x) == 0)
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

 
    public static boolean hasArguments(FastGraph framework) {
    	
    	return IntStream.range(0, framework.getNumberOfNodes()).anyMatch(x -> (framework.getNodeType(x) == FastArgumentMap.CA_NODE && framework.getNodeType(x) == FastArgumentMap.RA_NODE));
    }


    public static double entropy(FastGraph aif, Set<String> extension) {
 		
		double total = Double.valueOf(extension.size());
 		if ( total == 0.0 ) return 0.0;
 		
 		double ca = 0.0, ra = 0.0;		
		ca += IntStream.range(0, aif.getNumberOfNodes())
				.filter(x -> extension.contains(aif.getNodeLabel(x)))
				.filter(x -> (aif.getNodeType(x) == FastArgumentMap.CA_NODE)).count();
		ra += IntStream.range(0, aif.getNumberOfNodes())
				.filter(x -> extension.contains(aif.getNodeLabel(x)))
				.filter(x -> (aif.getNodeType(x) == FastArgumentMap.RA_NODE)).count();

 		ra = ra/total; ca = ca/total;
 		
 		double result = ra > 0.0 ? ra*Math.log(ra)/Math.log(2) : 0.0;
 		result += ca > 0.0 ? ca*Math.log(ca)/Math.log(2) : 0.0;
 		result = result < 0.0 ? -result : result;
 		return result;
 	}
    
}
