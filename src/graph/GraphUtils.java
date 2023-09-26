package graph;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class GraphUtils {

 	public static void dumpGraph(FastGraph graph, String filename) {
 		
 		try {
 			CSVFormat csvFileFormat = CSVFormat.DEFAULT;
 	 		FileWriter writer = new FileWriter(filename);
 	 		CSVPrinter csvPrinter = new CSVPrinter(writer, csvFileFormat);

 	 		csvPrinter.printRecord(new Object[] {"from", "fromType", "to", "toType", "edgeLabel"});
 	 		
 	 		for ( int i = 0; i < graph.getNumberOfEdges(); i++ ) {
 	 			
 	 			int n1= graph.getEdgeNode1(i);
 	 			int n2= graph.getEdgeNode2(i);
 	 		
 	 			List<String> record = new ArrayList<String>();
 	 			record.add(graph.getNodeLabel(n1));
 	 			record.add(String.format("%d", graph.getNodeType(n1)));
 	 			record.add(graph.getNodeLabel(n2));
 	 			record.add(String.format("%d", graph.getNodeType(n2)));
 	 			record.add(String.format("%s", graph.getEdgeLabel(i)));
 	 			
 	 			csvPrinter.printRecord(record);
 	 		}
 	 		
 	 		writer.flush();
 	 		writer.close();
 	 		csvPrinter.close();
 		}
 		catch(Exception e) {
 			e.printStackTrace();;		
 		}
 	}

}
