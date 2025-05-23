package argumentation.explain;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import argumentation.aif.FastArgumentMap;
import argumentation.aif.FastMapUtils;
import argumentation.engine.Backtrack;
import argumentation.presumptive.Analysis;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Report {

	public static void describeExtensions(Set<Set<Integer>> extensions, FastArgumentMap aif, FastGraph framework, PrintWriter out) {
		
		FastGraph graph = aif.getGraph();
		
        out.printf("There are %d extensions...\n\n", extensions.size());
        int n = 0;
        
        for (Set<Integer> extension: extensions ) {
        	
 	 	   out.printf("\nExtension %d is: %s\n", ++n, extension);
 	 	   out.printf("... with labels: %s\n", FastMapUtils.extensionLabels(framework, extension));
 	 	   
 	 	   Set<String> acceptable = Analysis.getAcceptableInformation(graph, framework, extension);
	       out.println("The acceptable I-nodes are: " + acceptable);
	       out.println("... and we don't believe: " + Analysis.getDisbelievedInformation(graph, framework, extension));
	       out.println();
	       
	       out.println("The acceptable conclusions are: " + Analysis.findConclusions(aif.getGraph(), acceptable));
        }
	}

	
	public static void printClaims(FastArgumentMap aif, PrintWriter out) {
		
		Map<String, String> claims = aif.getClaimText();
		List<String> labels = new ArrayList<String>(); labels.addAll(claims.keySet());
		Collections.sort(labels);
		
		for ( String label: labels ) {
			
			out.printf("%5s: %s\n", label, claims.get(label));
		}
	}

	
	public static void printArguments(FastGraph graph, PrintWriter out) {
		
		for (int i = 0; i < graph.getNumberOfNodes(); i++ ) {
			
			int type = graph.getNodeType(i);
			
			if ( type != FastArgumentMap.I_NODE && type != FastArgumentMap.L_NODE ) {
				
				String label = graph.getNodeLabel(i);
				String premises   = Arrays.stream(graph.getNodeConnectingInNodes(i)).filter(x -> (graph.getNodeType(x) == FastArgumentMap.I_NODE || graph.getNodeType(x) == FastArgumentMap.L_NODE)).mapToObj(x -> graph.getNodeLabel(x)).collect(Collectors.joining( ", " ));
				String conclusion = Arrays.stream(graph.getNodeConnectingOutNodes(i)).mapToObj(x -> graph.getNodeLabel(x)).collect(Collectors.joining( ", " ));
				
				out.printf("%6s: %s => %s\n", label, premises, conclusion);
			}
		}
	}

	
	public static void printArguments(FastArgumentMap aif, PrintWriter out) {

		printArguments(aif.getGraph(), out);
	}
	
	
	public static void printCertainty(FastArgumentMap aif, FastGraph framework, PrintWriter out) {
		
	 	   Set<Integer> grounded = Backtrack.grounded(framework);
	 	   
	       if ( grounded.size() == 0 ) {
	    	   
	     	   out.println("The grounded extension is empty. Nothing is certain.");
	     	   return;
	       }
	       else {
	    	   
	    	   Set<String> groundedLabels = FastMapUtils.extensionLabels(framework, grounded);	 	  
	    	   out.println("The grounded extension is: " + groundedLabels);
	 	 	   Set<String> acceptable = Analysis.getAcceptableInformation(aif.getGraph(), framework, grounded);
		       out.println("The acceptable I-nodes are: " + acceptable);
	       }
	}
	

	public static void printPartitions(Map<Set<String>, Set<Set<Integer>>> partitions, PrintWriter report) {
 		
 		report.println("Number of partitions: " + partitions.size());
 		
 		for ( Set<String> key: partitions.keySet() ) {
 			
 			report.printf("partition: %s (%d extensions)\n", key, partitions.get(key).size());
 		}
 		
 	}
	
}
