package order;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lattice<E, A> {

	private List<Set<E>> extents;
	private Map<Integer, Set<A>> attributes;
	Map<Integer, Set<Integer>> preorder;

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
	
}
