package lattice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class FormalConceptAnalysis {
	
	// This is the Formal Concept Analysis algorithm from Ch. 3 of "Introduction to Lattices and Order", Second Edition,
	// B.A. Davey, H.A. Priestley.

	public static <A, E> List<Entry<A, Set<E>>> eliminationOrder (Map<A, Set<E>> map) {
		
		// Construct an elimination order. This is a list of the entries of the map ordered by
		// set inclusion on their values.

		List<Entry<A, Set<E>>> results = new ArrayList<Entry<A, Set<E>>>();			
		List<Entry<A, Set<E>>> entries = new ArrayList<Entry<A, Set<E>>>();

		entries.addAll(map.entrySet());
		
		while ( entries.size() > 0 ) {
			
			List<Entry<A, Set<E>>> m = maximal(entries);
			results.addAll(m);
			entries.removeAll(m);
		}
				
		return results;
	}

	
	public static <A, E> List<Entry<A, Set<E>>> maximal(List<Entry<A, Set<E>>> entries) {
		
		List<Entry<A, Set<E>>> maximal = new ArrayList<Entry<A, Set<E>>>();
		
		// sort entries in decreasing order of value set size
		entries.sort(new Comparator<Entry<A, Set<E>>>() {

			@Override
			public int compare(Entry<A, Set<E>> o1, Entry<A, Set<E>> o2) {
				return o2.getValue().size() - o1.getValue().size();
			}			
		});

		ListIterator<Entry<A, Set<E>>> iter = entries.listIterator();
		
		while (iter.hasNext() ) {
			
			Entry<A, Set<E>> entry = iter.next();
			
			if ( isMaximal(maximal, entry) ) {
				
				maximal.add(entry);
			}
		}
		
		return maximal;
	}

	
	public static <A, E> boolean isMaximal (Collection<Entry<A, Set<E>>> maximal, Entry<A, Set<E>> candidate) {
		
		// check if the candidate set in the Map entry is maximal wrt the collection of maximal sets.
		
		if ( maximal.isEmpty() ) return true;
		
		Iterator<Entry<A, Set<E>>> iter = maximal.iterator();
		Set<E> candidateSet = candidate.getValue();
		
		while ( iter.hasNext() ) {
			
			Set<E> maximalSet = iter.next().getValue();
			
			if ( maximalSet.containsAll(candidateSet) && candidateSet.size() < maximalSet.size() ) return false;
		}
		
		return true;
	}

	
	public static <E> Set<Set<E>> intersections (List<Set<E>> collection, Set<E> set) {
		
		// find all distinct intersections between given set and any of those in the collection
		
		Set<Set<E>> intersections = new HashSet<Set<E>>();		

		Iterator<Set<E>> iter = collection.iterator();
		
		while ( iter.hasNext() ) {
			
			Set<E> item = new HashSet<E>(iter.next());			
			item.retainAll(set);
			
			if ( item.size() > 0 && item.size() < set.size() ) {
				
				intersections.add(item);
			}
		}
		
		return intersections;
	}

	
	public static <A> void addAttributeToTable(A attribute, Map<Integer, Set<A>> attributeMap, int row) {
		
		// A concept table is a list of extents and associated attributes. Each extent may be associated with
		// zero or more attributes. This function adds the given attribute to the set in the given row, creating
		// that set if necessary.
		
		Set<A> attributes = attributeMap.get(row);
		
		if ( attributes == null ) {
			
			attributes = new HashSet<A>();
		}
		
		attributes.add(attribute);
		attributeMap.put(row, attributes);
	}

	
	private static <E> void link(List<Entry<Integer, Set<E>>> current, List<Entry<Integer, Set<E>>> previous, Map<Integer, Set<Integer>> lattice) {
		
		ListIterator<Entry<Integer, Set<E>>> currIter = current.listIterator();

		// for each extent in the "current" list ...
		
		while ( currIter.hasNext() ) {
			
			Entry<Integer, Set<E>> c = currIter.next();
			Set<E> fromset = c.getValue();
			int from = c.getKey();
			
			ListIterator<Entry<Integer, Set<E>>> prevIter = previous.listIterator();
			
			while ( prevIter.hasNext() ) {

				Entry<Integer, Set<E>> p = prevIter.next();
				Set<E> toset = p.getValue();
				int to = p.getKey();

				if ( toset.containsAll(fromset) ) {
					
					// make a link to any extent in the "previous" list which contains it.		

					Set<Integer> fm = lattice.get(from);
					if ( fm == null )   fm = new HashSet<Integer>();
					fm.add(to);
					lattice.put(from, fm);
				}
			}
		}
	}

	public static <A, E> void order(Map<A, Set<E>> map) {
		
		// The algorithm described in section 3.14 of "Introduction to Lattices and Order".
		// Some comments below refer to the text.
		
		List<Entry<A, Set<E>>> eliminationOrder = FormalConceptAnalysis.eliminationOrder(map);		
		
		// Step 1
		
		List<Set<E>> extents = new ArrayList<Set<E>>();
		Map<Integer, Set<A>> attributes = new HashMap<Integer, Set<A>>();
		
		// "extents" and "attributes" together form the table of attributes and extents described in Ch. 3:
		// "extents" is an ordered list; "attributes" is a a map with keys that are positions in the extents list.

		ListIterator<Entry<A, Set<E>>> elimIter = eliminationOrder.listIterator();

		while ( elimIter.hasNext() ) {
			
			// (1.2)
			Entry<A, Set<E>> entry = elimIter.next();
			A attribute = entry.getKey();
			Set<E> extent = entry.getValue();
			
			int tableRow = extents.indexOf(extent);
			
			// (1.2.1)
			if ( tableRow < 0 ) {			
				
				Set<Set<E>> intersections = intersections(extents, extent);
				
				extents.add(extent);
				// there are attributes associated with this extent, so update attributes map accordingly
				addAttributeToTable(attribute, attributes, extents.size()-1);
				
				for ( Set<E> intersection: intersections ) {
					
					if ( !extents.contains(intersection) )  extents.add(intersection);
				}
			}
			else {
				
				// (1.2.2)
				addAttributeToTable(attribute, attributes, tableRow);
			}			
		}
		
		System.out.println("\n============\n");
		
		printTable(extents, attributes);
		
		System.out.println("\n============\n");
		
		// Step 2 - linking rows of the extents and attributes table to make a lattice
		
		// The lattice is a Map, with positions of an extent in "extents" as both keys and values.
		// Each entry key represents a node in a lattice diagram, with its values representing capping concepts.
		Map<Integer, Set<Integer>> lattice = new HashMap<Integer, Set<Integer>>();
		
		// some working data ...
		Map<Integer, Set<E>> extentMap = new HashMap<Integer, Set<E>>();	
		for ( int i = 0; i < extents.size(); i++ )  extentMap.put(i, extents.get(i));
		
		List<Entry<Integer, Set<E>>> tableEntries;
		List<Entry<Integer, Set<E>>> previous = null;
				
		while ( ! extentMap.isEmpty() ) {
			
			tableEntries = new ArrayList<Entry<Integer, Set<E>>>(extentMap.entrySet());			
			List<Entry<Integer, Set<E>>> maximalRemaining = maximal(tableEntries);
			
			if ( previous != null ) {
				
				link(maximalRemaining, previous, lattice);
			}
			previous = maximalRemaining;
			
			for ( Entry<Integer, Set<E>> entry: maximalRemaining ) extentMap.remove(entry.getKey());
		}
		
		System.out.println("lattice - " + lattice);
		
		FastGraph graph = FastLattice.getGraph(lattice);
		//GraphUtils.dumpGraph(graph, "lattice.txt");
		
		for ( int i = 0; i < extents.size(); i++ ) {
		
			Set<E> below = new HashSet<E>();
			Set<E> extent = extents.get(i);
			
			Arrays.stream(graph.getNodeConnectingInNodes(i)).forEach( x -> below.addAll(extents.get(x)) );
 			extent.removeAll(below);
			
		}

		for ( int i = 0; i < extents.size(); i++ ) {
			
			System.out.printf("(2) %3d: %s, %s\n", i, extents.get(i), attributes.get(i));
		}

	}

	
	public static <A, E> void printTable(List<Set<E>> extents, Map<Integer, Set<A>> attributes) {
		
		for ( int i = 0; i < extents.size(); i++ ) {
			
			System.out.printf("%3d: %s - %s\n", i, extents.get(i), attributes.get(i));
		}
	}
}

