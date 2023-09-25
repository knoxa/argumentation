package argumentation.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import argumentation.engine.Label;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class Backtrack {

	/*
	 
	Algorithms from "Looking-ahead in backtracking algorithms for abstract argumentation"; Samer Nofala, Katie Atkinson, Paul E.Dunne
	https://doi.org/10.1016/j.ijar.2016.07.013
	
	The algorithms operate on a Dung AAF's expressed as a Dover FastGraph.
	
	 */
	
	private static final Set<Label> hopeless = new HashSet<Label>();
	private static final Set<Label> mustIn   = new HashSet<Label>();

	static {
		
		Collections.addAll(hopeless, Label.OUT, Label.MUST_OUT, Label.UNDEC);
		Collections.addAll(mustIn, Label.OUT, Label.MUST_OUT);
	}

	public static Label[] leftTransition(FastGraph framework, Label[] labels, int argument) {
		
		// Definition 3
		
		assert labels[argument] == Label.BLANK;

		Label[] newLabels = labels.clone();
		newLabels[argument] = Label.IN;
		
		int[] attacked = framework.getNodeConnectingOutNodes(argument);
		Arrays.stream(attacked).forEach(x -> newLabels[x] = Label.OUT);
				
		int[] attackers = framework.getNodeConnectingInNodes(argument);
		Arrays.stream(attackers).filter( x -> newLabels[x] != Label.OUT ).forEach( x -> newLabels[x] = Label.MUST_OUT );

		return newLabels;
	}
	
	
	// Definition 4
	public static Label[] rightTransition(FastGraph framework, Label[] labels, int argument) {
		
		Label[] newLabels = labels.clone();
		newLabels[argument] = Label.UNDEC;
		
		return newLabels;
	}

	
	// Definition 5 - terminal labelling
	public static boolean isTerminalLabelling(Label[] labels) {

		return Arrays.stream(labels).allMatch(label -> label != Label.BLANK);
	}


	// Definition 6 - dead-end labelling
	public static boolean isDeadEndLabelling(Label[] labels) {
			
		return isTerminalLabelling(labels) && Arrays.stream(labels).anyMatch(label -> label == Label.MUST_OUT);
	}

	
	// Definition 7 - admissible labelling
	public static boolean isAdmissibleLabelling(Label[] labels) {
		
		return isTerminalLabelling(labels) && Arrays.stream(labels).noneMatch(label -> label == Label.MUST_OUT);
	}

	
	// Algorithm 1
	public static void preferred1(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) { // done
		
		if ( isDeadEndLabelling(labels) ) return;
		
		if ( isTerminalLabelling(labels) ) { 
			
			if ( isAdmissibleLabelling(labels) ) { 
				
				Set<Integer> extension = new HashSet<Integer>();
				IntStream.range(0, labels.length).filter(x -> labels[x] == Label.IN).forEach( x -> extension.add(x) );
				
				if ( extensions.stream().noneMatch(e -> e.containsAll(extension)) ) extensions.add(extension);
				
				return;
			}			
		}
		
		int nextArgument = selectAnyBlank(framework, labels);
		
		Label[] newLabels;
		
		newLabels = leftTransition(framework, labels, nextArgument);
		preferred1(framework, newLabels, extensions);
		
		newLabels = rightTransition(framework, labels, nextArgument);
		preferred1(framework, newLabels, extensions);
	}


	public static int selectAnyBlank(FastGraph framework, Label[] labels) {
		
		return IntStream.range(0, labels.length).filter(x -> labels[x] == Label.BLANK).findAny().getAsInt();
	}

	
	// Definition 9 - hopeless labelling
	public static boolean isHopelessLabelling(FastGraph framework, Label[] labels) {
		
		return IntStream.range(0, labels.length)
			.filter( x -> labels[x] == Label.MUST_OUT )
			.anyMatch( x -> 
					Arrays.stream(framework.getNodeConnectingInNodes(x))
						.allMatch( node -> hopeless.contains(labels[node]) )
			);
	}

	
	// Algorithm 2
	public static void preferred2(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
				
		if ( isHopelessLabelling(framework, labels) ) return;
		
		if ( isTerminalLabelling(labels) ) { 
			
			if ( isAdmissibleLabelling(labels) ) { 
				
				Set<Integer> extension = new HashSet<Integer>();
				IntStream.range(0, labels.length).filter(x -> labels[x] == Label.IN).forEach( x -> extension.add(x) );
				
				if ( extensions.stream().noneMatch(e -> e.containsAll(extension)) ) extensions.add(extension);
				
				return;
			}			
		}
		
		int nextArgument = selectAnyBlank(framework, labels);
		
		Label[] newLabels;
		
		newLabels = leftTransition(framework, labels, nextArgument);
		preferred2(framework, newLabels, extensions);
		
		newLabels = rightTransition(framework, labels, nextArgument);
		preferred2(framework, newLabels, extensions);
	}
	

	// Definition 10 - influential arguments
	public static int selectInfluential(FastGraph framework, Label[] labels) {
		
		// there must be at least one BLANK label
		
		List<Integer> blanks = IntStream.range(0, labels.length).filter(x -> labels[x] == Label.BLANK).boxed().collect(Collectors.toList());
		
		Collections.sort(blanks, new Comparator<Integer>() {

			@Override
			public int compare(Integer a, Integer b) {

				return framework.getNodeDegree(b) - framework.getNodeDegree(a);
			}
			
		});
		
		assert(blanks.size() > 0);
		
		return blanks.get(0);
	}

	
	// Definition 11 - must-in arguments
	public static int selectMustIn(FastGraph framework, Label[] labels) {
		
		return IntStream.range(0, labels.length)
				.filter( x -> labels[x] == Label.BLANK )
				.filter( x -> Arrays.stream(framework.getNodeConnectingInNodes(x)).allMatch( node -> mustIn.contains(labels[node]) ) )
				.findFirst().orElse(-1);
	}

	
	// Definition 12 - labelling propagation
	public static void propagateLabels(FastGraph framework, Label[] labels) {
		
		int mustIn;
		
		while ( (mustIn = selectMustIn(framework, labels)) >= 0 ) {
			
			labels[mustIn] = Label.IN;
			Arrays.stream(framework.getNodeConnectingOutNodes(mustIn)).forEach(x -> labels[x] = Label.OUT);
		}
	}
	

	// Algorithm 3
	public static void preferred3(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
				
		if ( isHopelessLabelling(framework, labels) ) return;
		
		if ( isTerminalLabelling(labels) ) { 
			
			if ( isAdmissibleLabelling(labels) ) { 
				
				Set<Integer> extension = new HashSet<Integer>();
				IntStream.range(0, labels.length).filter(x -> labels[x] == Label.IN).forEach( x -> extension.add(x) );
				
				if ( extensions.stream().noneMatch(e -> e.containsAll(extension)) ) extensions.add(extension);
			}
			
			return;
		}

		propagateLabels(framework, labels);
		
		if ( isTerminalLabelling(labels) ) {
			
			preferred3(framework, labels, extensions);
		}
		else {
			
			int nextArgument = selectInfluential(framework, labels);
			
			Label[] newLabels;
			
			newLabels = leftTransition(framework, labels, nextArgument);
			preferred3(framework, newLabels, extensions);
			
			newLabels = rightTransition(framework, labels, nextArgument);
			preferred3(framework, newLabels, extensions);
		}
	}
	

	// Algorithm 4
	public static void preferred4(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
		
		propagateLabels(framework, labels);	

		if ( isHopelessLabelling(framework, labels) ) return;
		
		while ( ! isTerminalLabelling(labels) ) { 
			
			int nextArgument = selectInfluential(framework, labels);
			
			Label[] newLabels = leftTransition(framework, labels, nextArgument);
			if ( ! isHopelessLabelling(framework, newLabels) )  preferred4(framework, newLabels, extensions);
			
			labels = rightTransition(framework, labels, nextArgument);
			if ( isHopelessLabelling(framework, labels) ) return;
		}
		
		if ( isAdmissibleLabelling(labels) ) { 
			
			Set<Integer> extension = new HashSet<Integer>();
			Label[] labelclone = labels.clone();
			IntStream.range(0, labelclone.length).filter(x -> labelclone[x] == Label.IN).forEach( x -> extension.add(x) );
			
			if ( extensions.stream().noneMatch(e -> e.containsAll(extension)) ) extensions.add(extension);
		}
		
		return;
	}


	// Algorithm 5
	public static void admissible(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
		
		while ( ! isTerminalLabelling(labels) ) { 
			
			int nextArgument = selectInfluential(framework, labels);
			
			Label[] newLabels = leftTransition(framework, labels, nextArgument);
			if ( ! isHopelessLabelling(framework, newLabels) )  admissible(framework, newLabels, extensions);
			
			labels = rightTransition(framework, labels, nextArgument);
			if ( isHopelessLabelling(framework, labels) ) return;
		}
		
		if ( isAdmissibleLabelling(labels) ) { 
			
			Set<Integer> extension = new HashSet<Integer>();
			Label[] labelclone = labels.clone();
			IntStream.range(0, labelclone.length).filter(x -> labelclone[x] == Label.IN).forEach( x -> extension.add(x) );			
			extensions.add(extension);
		}
				
		return;
	}

	
	// Definition 14 - complete labelling
	public static boolean isCompleteLabelling(FastGraph framework, Label[] labels) {
		
		return isAdmissibleLabelling(labels) && IntStream.range(0, labels.length)
			.filter( x -> labels[x] == Label.UNDEC )
			.allMatch( x -> 
					Arrays.stream(framework.getNodeConnectingInNodes(x))
						.anyMatch( node -> labels[node] == Label.UNDEC )
			);
	}

	
	// Algorithm 6
	public static void complete(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
				
		propagateLabels(framework, labels);	

		if ( isHopelessLabelling(framework, labels) ) return;
		
		while ( ! isTerminalLabelling(labels) ) { 
			
			int nextArgument = selectInfluential(framework, labels);
			
			Label[] newLabels = leftTransition(framework, labels, nextArgument);
			if ( ! isHopelessLabelling(framework, newLabels) )  complete(framework, newLabels, extensions);
			
			labels = rightTransition(framework, labels, nextArgument);
			if ( isHopelessLabelling(framework, labels) ) return;
		}

		if ( isCompleteLabelling(framework, labels) ) { 
			
			Set<Integer> extension = new HashSet<Integer>();
			Label[] labelclone = labels.clone();
			IntStream.range(0, labelclone.length).filter(x -> labelclone[x] == Label.IN).forEach( x -> extension.add(x) );
			
			extensions.add(extension);
		}
		
		return;
	}
	

	// Definition 15 - right-transition-stable
	public static Label[] rightTransitionStable(Label[] labels, int argument) {
		
		Label[] newLabels = labels.clone();
		newLabels[argument] = Label.MUST_OUT;
		
		return newLabels;
	}

	
	// Definition 16 - stable labelling
	public static boolean isStableLabelling(Label[] labels) {
		
		return isTerminalLabelling(labels) && Arrays.stream(labels).noneMatch(label -> label == Label.MUST_OUT);
	}

	
	// Algorithm 7
	public static void enumerateStable(FastGraph framework, Label[] labels, Set<Set<Integer>> extensions) {
		
		propagateLabels(framework, labels);	

		if ( isHopelessLabelling(framework, labels) ) return;
		
		while ( ! isTerminalLabelling(labels) ) { 
			
			int nextArgument = selectInfluential(framework, labels);
			
			Label[] newLabels = leftTransition(framework, labels, nextArgument);
			if ( ! isHopelessLabelling(framework, newLabels) )  enumerateStable(framework, newLabels, extensions);
			
			labels = rightTransitionStable(labels, nextArgument);
			if ( isHopelessLabelling(framework, labels) ) return;
		}
		
		if ( isStableLabelling(labels) ) { 
			
			Set<Integer> extension = new HashSet<Integer>();
			Label[] labelclone = labels.clone();
			IntStream.range(0, labelclone.length).filter(x -> labelclone[x] == Label.IN).forEach( x -> extension.add(x) );
			
			extensions.add(extension);
		}
		
		return;
	}


}
