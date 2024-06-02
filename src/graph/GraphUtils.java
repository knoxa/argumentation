package graph;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class GraphUtils {
	
	public static final String GRAPHML_NAMESPACE = "http://graphml.graphdrawing.org/xmlns";

	public static void induceGraph(FastGraph g, HashSet<Integer> nodes, HashSet<Integer> edges) {
		
		// Induce a subgraph of g from the given nodes and/or edges. This is done by adding 'missing'
		// nodes and edges to the supplied sets. FastGraph.generateGraphFromSubgraph() can then be used to
		// create a FastGraph object from the results.
		
		// Ensure that the node set has the nodes at each end of every supplied edge
		
		for (int e: edges) {
			
			nodes.add(g.getEdgeNode1(e));
			nodes.add(g.getEdgeNode2(e));			
		}
		
		// Add edges between any pair of nodes in the node set.
		
		boolean[] visitedEdges = new boolean[g.getNumberOfEdges()];
		
		//for every node in the graph
		for(int n : nodes) {
			//find what edges it connects to
			int[] edgeConnections = g.getNodeConnectingEdges(n);
			//for each of these connecting edges
			for (int ce : edgeConnections) {
				//find the other node
				int cn = g.oppositeEnd(ce,n);
				//if that node is also in the graph and this edge hasn't been visited already, then add the edge
				if (nodes.contains(cn) && !visitedEdges[ce]) {
					edges.add(ce);
					visitedEdges[ce] = true;
				}
			}			
		}

		return;
	}


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

 	
 	public static void serializeAsGraphML(FastGraph graph, ContentHandler ch) throws SAXException {
 		
 		ch.startDocument();
		ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "graphml", "graphml", new AttributesImpl());
		
		AttributesImpl idlAttr = new AttributesImpl();
		idlAttr.addAttribute("", "id",  "id", "String",  "id");
		idlAttr.addAttribute("", "for",  "for", "String",  "node");
		idlAttr.addAttribute("", "attr.name",  "attr.name", "String",  "id");
		idlAttr.addAttribute("", "attr.type",  "attr.type", "String",  "string");
		ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key", idlAttr);
		ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key");
		
		AttributesImpl labelAttr = new AttributesImpl();
		labelAttr.addAttribute("", "id",  "id", "String",  "label-node");
		labelAttr.addAttribute("", "for",  "for", "String",  "node");
		labelAttr.addAttribute("", "attr.name",  "attr.name", "String",  "label");
		labelAttr.addAttribute("", "attr.type",  "attr.type", "String",  "string");
		ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key", labelAttr);
		ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key");
		
		labelAttr = new AttributesImpl();
		labelAttr.addAttribute("", "id",  "id", "String",  "label-edge");
		labelAttr.addAttribute("", "for",  "for", "String",  "edge");
		labelAttr.addAttribute("", "attr.name",  "attr.name", "String",  "label");
		labelAttr.addAttribute("", "attr.type",  "attr.type", "String",  "string");
		ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key", labelAttr);
		ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "key", "key");
		
		ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "graph", "graph", new AttributesImpl());

		for ( int i = 0; i < graph.getNumberOfNodes(); i++ ) {
			
			String id = String.valueOf(i);

			AttributesImpl attr = new AttributesImpl();
			attr.addAttribute("", "id",  "id", "String",  id);
			ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "node", "node", attr);
			
			attr = new AttributesImpl();
			attr.addAttribute("", "key",  "key", "String",  "id");
			ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data", attr);
			ch.characters(id.toCharArray(), 0, id.length());
			ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data");

			String label = graph.getNodeLabel(i);
			attr = new AttributesImpl();
			attr.addAttribute("", "key",  "key", "String",  "label-node");
			ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data", attr);
			ch.characters(label.toCharArray(), 0, label.length());
			ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data");
			
			ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "node", "node");
		}
 		
 		for ( int i = 0; i < graph.getNumberOfEdges(); i++ ) {
		
 			int n1= graph.getEdgeNode1(i);
 			int n2= graph.getEdgeNode2(i);
 			String label = graph.getEdgeLabel(i);
 			
			AttributesImpl attr = new AttributesImpl();
			attr.addAttribute("", "source",  "source", "String",  String.valueOf(n1));
			attr.addAttribute("", "target",  "target", "String",  String.valueOf(n2));
			
			ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "edge", "edge", attr);

			attr = new AttributesImpl();
			attr.addAttribute("", "key",  "key", "String",  "label-edge");
			ch.startElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data", attr);
			ch.characters(label.toCharArray(), 0, label.length());
			ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "data", "data");

			ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "edge", "edge");
 		}
 		
 		ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "graph", "graph");

		ch.endElement(GraphUtils.GRAPHML_NAMESPACE, "graphml", "graphml");
 		ch.endDocument();
 	}
}
