package argumentation.explain;

import java.io.PrintWriter;
import java.util.Set;

import argumentation.aif.FastMapUtils;
import argumentation.presumptive.Analysis;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Report {

	public static void describeExtensions(Set<Set<Integer>> extensions, FastGraph framework, FastGraph aif, PrintWriter out) {
		
		Set<Set<String>> extensionLabels = FastMapUtils.extensionSetLabels(framework, extensions);
		
        out.printf("There are %d extensions...\n\n", extensions.size());
        int n = 0;
        
        for (Set<String> extension: extensionLabels ) {
        	
 	 	   out.printf("Extension %d is: %s\n", ++n, extension);
 	 	   
 	 	   Set<String> acceptable = Analysis.getAcceptableInformation(aif, extension);
	       out.println("The acceptable I-nodes are: " + acceptable);
	       out.println("We don't believe: " + Analysis.getDisbelievedInformation(aif, extension));
        }
	}
	
}
