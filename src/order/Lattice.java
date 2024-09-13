package order;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Lattice<E, A> {

	private List<Set<E>> extents;
	private Map<Integer, Set<A>> attributes;
	Map<Integer, Set<Integer>> preorder;
	private String id = "_:" + String.valueOf(this.hashCode());

	public List<Set<E>> getExtents() {
		return extents;
	}

	public void setExtents(List<Set<E>> extents) {
		this.extents = extents;
	}

	public Map<Integer, Set<A>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<Integer, Set<A>> attributes) {
		this.attributes = attributes;
	}

	public Map<Integer, Set<Integer>> getPreorder() {
		return preorder;
	}

	public void setPreorder(Map<Integer, Set<Integer>> preorder) {
		this.preorder = preorder;
	}
	
	public void serializeLattice(ContentHandler ch) throws SAXException {
		
		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "id",  "id", "String", this.id);
		ch.startElement("", "lattice", "lattice", attr);
		
		for ( int x = 0; x < extents.size(); x++ ) {
			
			attr = new AttributesImpl();
			attr.addAttribute("", "id",  "id", "Integer", String.valueOf(x));
			ch.startElement("", "extent", "extent", attr);
			
			Set<A> attributeSet = attributes.get(x);
			
			if ( attributeSet != null ) {

				for ( A attribute: attributeSet ) {
					
					ch.startElement("", "attribute", "attribute", null);
					String value = attribute.toString();
					ch.characters(value.toCharArray(), 0, value.length());
					ch.endElement("", "attribute", "attribute");
				}
			}
			
			Set<E> objectSet = extents.get(x);
			
			if ( objectSet != null ) {
				
				for ( E object: objectSet ) {
					
					if ( object != null ) {
						
						attr = new AttributesImpl();
						ch.startElement("", "object", "object", attr);
						String value = object.toString();
						ch.characters(value.toCharArray(), 0, value.length());
						ch.endElement("", "object", "object");
					}
				}
			}
						
			ch.endElement("", "extent", "extent");
		}
		
		ch.startElement("", "preorder", "preorder", null);
		
		for ( Integer fm: preorder.keySet() ) {
			
			Set<Integer> toSet = preorder.get(fm);
			
			for ( Integer to: toSet ) {
				
				attr = new AttributesImpl();
				attr.addAttribute("", "from",  "from", "Integer", String.valueOf(fm));
				attr.addAttribute("", "to",  "to", "Integer", String.valueOf(to));
				ch.startElement("", "edge", "edge", attr);
				ch.endElement("", "edge", "edge");
			}
		}
		
		ch.endElement("", "preorder", "preorder");

		ch.endElement("", "lattice", "lattice");
	}
}
