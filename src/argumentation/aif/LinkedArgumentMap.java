package argumentation.aif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
    private Map<String, String> claimText = new HashMap<String, String>();
    
    FastGraph graph;

    @Override
	public FastGraph getGraph() {
		return graph;
	}
    
    private void buildGraph() {
    	
		List<NodeStructure> nodes = makeNodes(resourceToLabel, labelToType, labelToNodeNumber);
		List<EdgeStructure> edges = makeEdges(model, resourceToLabel, labelToNodeNumber);

		nodes.sort(new Comparator<NodeStructure>() {
			public int compare(NodeStructure n1, NodeStructure n2) {
				return n1.getId() - n2.getId();
			}
		});
		
		graph = FastGraph.structureFactory("AIF", (byte) 0, nodes, edges, true);
    }
    
	public LinkedArgumentMap() {
		
	}

	public LinkedArgumentMap(Model model) {
		super();
		this.setModel(model);
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
        claimText = new HashMap<String, String>();
        
        addToLookups(resourceToLabel, labelToResource, information, labelToType, labelToNodeNumber, I_NODE);
        addToLookups(resourceToLabel, labelToResource, locution, labelToType, labelToNodeNumber, L_NODE);
        addToLookups(resourceToLabel, labelToResource, rules, labelToType, labelToNodeNumber, RA_NODE);
        addToLookups(resourceToLabel, labelToResource, conflicts, labelToType, labelToNodeNumber, CA_NODE);		
        addToLookups(resourceToLabel, labelToResource, rephrases, labelToType, labelToNodeNumber, MA_NODE);		
        addToLookups(resourceToLabel, labelToResource, transitions, labelToType, labelToNodeNumber, TA_NODE);
        
        buildGraph();
	}	
	
    private void addToLookups(Map<Resource, String> resourceTolabel, Map<String, Resource> labelToResource, List<Resource> resources, Map<String, Byte> labelToType, Map<String, Integer> labelToNodeNumber, byte type) {
        
        int index = labelToNodeNumber.size();
        
        for ( Resource resource: resources ) {
             
             String label = String.format("%s%d", FastArgumentMap.prefix[type], index);
             labelToResource.put(label, resource);
             resourceTolabel.put(resource, label);
             labelToType.put(label, type);
             labelToNodeNumber.put(label, index++);
             
             Statement claim = resource.getProperty(AIF.claimText);            
             if ( claim != null )  claimText.put(label, claim.getString());
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

	
	@Override
	public Map<String, String> getClaimText() {

		return claimText;
	}

	
	public LinkedArgumentMap getSubArgumentFromLabels(Set<String> labels) {
		
		Set<Resource> resources = new HashSet<Resource>();
		labels.stream().forEach(x -> resources.add(labelToResource.get(x)));		
		return getSubArgument(resources);
	}
	
	
    public LinkedArgumentMap getSubArgument(Set<Resource> nodes) {
        
        Model model = this.getModel();
        
        StmtIterator iter;
        List<Statement> subgraph = new ArrayList<Statement>();
        
        // get the literal properties for each wanted node
        
        for (Resource node: nodes) {
             
             iter = node.listProperties();
             
             while ( iter.hasNext() ) {
                   
                   Statement statement = iter.next();
                   if ( statement.getObject().isLiteral() || statement.getPredicate().equals(RDF.type) )  subgraph.add(statement);
             }
        }
        
        // get properties that link wanted nodes
        
        iter = model.listStatements();

        while ( iter.hasNext() ) {
             
             Statement statement = iter.next();               
             if ( nodes.contains(statement.getSubject()) && statement.getObject().isResource() )  subgraph.add(statement);
       }

        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(subgraph);

        return new LinkedArgumentMap(newModel);
    }
    
 	public Set<String> getLabelsForResources(Set<Resource> resources) {
 		
 		Set<String> labels = new HashSet<String>();		
 		for ( Resource resource: resources ) labels.add(resourceToLabel.get(resource));		
 		return labels;
 	}
    
    public void validate() {
    	
    	if ( model == null ) {
    		System.err.println("model is null");
    		return;
    	}
    		
		ResIterator resourceIterator;
		StmtIterator iterator;

		resourceIterator = model.listResourcesWithProperty(RDF.type, AIF.iNode);
		
		for ( Resource inode: resourceIterator.toSet() ) {
			
			if ( inode.getProperty(AIF.claimText) == null ) {
				
				System.err.println("No claim text for: " + inode);
			}
			
			if ( resourceToLabel.get(inode) == null ) {
				
				System.err.println("No label for: " + inode);
			}
		}


		resourceIterator = model.listResourcesWithProperty(RDF.type, AIF.lNode);
		
		for ( Resource lnode: resourceIterator.toSet() ) {
			
			if ( lnode.getProperty(AIF.claimText) == null ) {
				
				System.err.println("No claim text for: " + lnode);
			}
			
			if ( resourceToLabel.get(lnode) == null ) {
				
				System.err.println("No label for: " + lnode);
			}
		}

		resourceIterator = model.listResourcesWithProperty(RDF.type, AIF.caNode);
		
		for ( Resource snode: resourceIterator.toSet() ) {
			
			if ( snode.getProperty(AIF.hasConclusion) == null ) {
				
				System.err.println("No conclusion: " + snode);
			}
		}

		resourceIterator = model.listResourcesWithProperty(RDF.type, AIF.taNode);
		
		for ( Resource tanode: resourceIterator.toSet() ) {
			
			if ( tanode.getProperty(AIF.endLocution) == null ) {
				
				System.err.println("No target locution: " + tanode);
			}
		}

		iterator = model.listStatements(null, AIF.hasPremise, (RDFNode) null);

		for ( Statement s: iterator.toSet() ) {
			
			if ( !s.getObject().isResource() ) {
				
				System.err.println("The object of an aif:Premise is not Resource: " + s + "[" + s.getObject() + "]");
			}
			Resource obj  = s.getObject().asResource();
			Resource subj = s.getSubject().asResource();
			
			if ( resourceToLabel.get(obj) == null ) {
				
				System.err.println("The object of an aif:Premise is not an AIF I-node or S-node: " + s);
			}
			
			if ( resourceToLabel.get(subj) == null ) {
				
				System.err.println("The subject of an aif:Premise is not an AIF I-node or S-node: " + s);
			}
		}

		iterator = model.listStatements(null, AIF.hasConclusion, (RDFNode) null);

		for ( Statement s: iterator.toSet() ) {
			
			Resource obj  = s.getObject().asResource();
			Resource subj = s.getSubject().asResource();
			
			if ( resourceToLabel.get(obj) == null ) {
				
				System.err.println("The object of an aif:Conclusion is not an AIF I-node or S-node: " + s);
			}
			
			if ( resourceToLabel.get(subj) == null ) {
				
				System.err.println("The subject of an aif:Conclusion is not an AIF I-node or S-node: " + s);
			}
		}
	}
    
}
