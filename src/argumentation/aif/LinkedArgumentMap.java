package argumentation.aif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class LinkedArgumentMap implements FastArgumentMap {

    private Model model;
   
    private List<Resource> information;
    private List<Resource> locution;
    private List<Resource> rules;
    private List<Resource> conflicts;
    private List<Resource> rephrases;
    private List<Resource> transitions;
    
    private Map<Resource, String> resourceToLabel = new HashMap<Resource, String>();
    private Map<String, Resource> labelToResource = new HashMap<String, Resource>();
    private Map<String, Byte> labelToType = new HashMap<String, Byte>();
    private Map<String, Integer> labelToNodeNumber = new HashMap<String, Integer>();

    @Override
	public FastGraph getGraph() {
	
		List<NodeStructure> nodes = makeNodes(resourceToLabel, labelToType, labelToNodeNumber);
		List<EdgeStructure> edges = makeEdges(model, resourceToLabel, labelToNodeNumber);

		nodes.sort(new Comparator<NodeStructure>() {
			public int compare(NodeStructure n1, NodeStructure n2) {
				return n1.getId() - n2.getId();
			}
		});

		return FastGraph.structureFactory("AIF", (byte) 0, nodes, edges, true);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		initialize();
	}
	
	
    	
	private void initialize() {
		
		information = model.listStatements(null, RDF.type,  AIF.iNode).mapWith(x -> x.getSubject()).toList();
		locution    = model.listStatements(null, RDF.type,  AIF.lNode).mapWith(x -> x.getSubject()).toList();
        rules       = model.listStatements(null, RDF.type, AIF.raNode).mapWith(x -> x.getSubject()).toList();
        conflicts   = model.listStatements(null, RDF.type, AIF.caNode).mapWith(x -> x.getSubject()).toList();
        rephrases   = model.listStatements(null, RDF.type, AIF.maNode).mapWith(x -> x.getSubject()).toList();
        transitions = model.listStatements(null, RDF.type, AIF.taNode).mapWith(x -> x.getSubject()).toList();
        
        resourceToLabel = new HashMap<Resource, String>();
        labelToResource = new HashMap<String, Resource>();
        labelToType = new HashMap<String, Byte>();
        labelToNodeNumber = new HashMap<String, Integer>();
        
        addToLookups(resourceToLabel, labelToResource, information, labelToType, labelToNodeNumber, I_NODE);
        addToLookups(resourceToLabel, labelToResource, locution, labelToType, labelToNodeNumber, L_NODE);
        addToLookups(resourceToLabel, labelToResource, rules, labelToType, labelToNodeNumber, RA_NODE);
        addToLookups(resourceToLabel, labelToResource, conflicts, labelToType, labelToNodeNumber, CA_NODE);		
        addToLookups(resourceToLabel, labelToResource, rephrases, labelToType, labelToNodeNumber, MA_NODE);		
        addToLookups(resourceToLabel, labelToResource, transitions, labelToType, labelToNodeNumber, TA_NODE);		

	}	
	
    private void addToLookups(Map<Resource, String> resourceTolabel, Map<String, Resource> labelToResource, List<Resource> resources, Map<String, Byte> labelToType, Map<String, Integer> labelToNodeNumber, byte type) {
        
        int index = labelToNodeNumber.size();
        
        for ( Resource resource: resources ) {
             
             String label = String.format("%s%d", FastArgumentMap.prefix[type], index);
             labelToResource.put(label, resource);
             resourceTolabel.put(resource, label);
             labelToType.put(label, type);
             labelToNodeNumber.put(label, index++);
        }
    }
	
	private List<NodeStructure> makeNodes(Map<Resource, String> resourceToLabel, Map<String, Byte> labelToType, Map<String, Integer> labelToNodeNumber) {

		List<NodeStructure> nodes = new ArrayList<NodeStructure>();

		for (Resource resource : resourceToLabel.keySet()) {

			String label = resourceToLabel.get(resource);
			nodes.add(new NodeStructure(labelToNodeNumber.get(label), label, 0, labelToType.get(label), (byte) 0));
		}

		return nodes;
	}	
	
	private List<EdgeStructure> makeEdges(Model model, Map<Resource, String> resourceToLabel, Map<String, Integer> labelToNodeNumber) {

		List<EdgeStructure> edges = new ArrayList<EdgeStructure>();

		edges.addAll(model.listStatements(null, AIF.hasPremise, (RDFNode) null)
				.mapWith(s -> new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, labelToNodeNumber.get(resourceToLabel.get(s.getObject())), labelToNodeNumber.get(resourceToLabel.get(s.getSubject()))))
				.toList());

		edges.addAll(model.listStatements(null, AIF.hasConclusion, (RDFNode) null)
				.mapWith(s -> new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, labelToNodeNumber.get(resourceToLabel.get(s.getSubject())), labelToNodeNumber.get(resourceToLabel.get(s.getObject()))))
				.toList());

		edges.addAll(model.listStatements(null, AIF.startLocution, (RDFNode) null)
				.mapWith(s -> new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, labelToNodeNumber.get(resourceToLabel.get(s.getObject())), labelToNodeNumber.get(resourceToLabel.get(s.getSubject()))))
				.toList());

		edges.addAll(model.listStatements(null, AIF.endLocution, (RDFNode) null)
				.mapWith(s -> new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, labelToNodeNumber.get(resourceToLabel.get(s.getSubject())), labelToNodeNumber.get(resourceToLabel.get(s.getObject()))))
				.toList());

		return edges;
	}

}
