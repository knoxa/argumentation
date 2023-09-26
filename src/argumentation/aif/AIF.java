package argumentation.aif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class AIF {

	public static final String NS = "http://www.arg.dundee.ac.uk/aif#";
	
	public static final Resource caNode = new ResourceImpl(NS + "CA-node");
	public static final Resource raNode = new ResourceImpl(NS + "RA-node");
	public static final Resource maNode = new ResourceImpl(NS + "MA-node");
	public static final Resource taNode = new ResourceImpl(NS + "TA-node");
	public static final Resource iNode  = new ResourceImpl(NS + "I-node");
	public static final Resource lNode  = new ResourceImpl(NS + "L-node");
	
	public static final Property claimText     = new PropertyImpl(NS, "claimText");
	public static final Property hasPremise    = new PropertyImpl(NS, "Premise");
	public static final Property hasConclusion = new PropertyImpl(NS, "Conclusion");
	public static final Property startLocution = new PropertyImpl(NS, "StartLocution");
	public static final Property endLocution   = new PropertyImpl(NS, "EndLocution");

}
