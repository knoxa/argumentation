package argumentation.explain;

import java.io.PrintWriter;
import java.util.Set;

import argumentation.presumptive.Analysis;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Report {

	public static void describeExtensions(Set<Set<Integer>> extensions, FastGraph framework, FastGraph aif, PrintWriter out) {
		
        out.printf("There are %d extensions...\n\n", extensions.size());
        int n = 0;
        
        for (Set<Integer> extension: extensions ) {
        	
 	 	   out.printf("Extension %d is: %s\n", ++n, extension);
 	 	   
 	 	   Set<String> acceptable = Analysis.getAcceptableInformation(aif, framework, extension);
	       out.println("The acceptable I-nodes are: " + acceptable);
	       out.println("We don't believe: " + Analysis.getDisbelievedInformation(aif, extension));
        }
	}
	
}
