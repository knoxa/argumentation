package test.argumentation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import argumentation.engine.Backtrack;
import argumentation.engine.Label;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class BacktrackTest {
	
	private FastGraph framework1;

	@Before
	public void setUp() throws Exception {

		framework1 = Frameworks.getFramework1();
	}

	@Test
	public void leftTransition() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);
		
		Label[] newLabels = Backtrack.leftTransition(framework, labels, 0);
		assertArrayEquals(newLabels, new Label[] {Label.IN, Label.OUT, Label.BLANK, Label.BLANK});

		newLabels = Backtrack.leftTransition(framework, labels, 1);
		assertArrayEquals(newLabels, new Label[] {Label.OUT, Label.IN, Label.OUT, Label.MUST_OUT});

		newLabels = Backtrack.leftTransition(framework, labels, 2);
		assertArrayEquals(newLabels, new Label[] {Label.BLANK, Label.MUST_OUT, Label.IN, Label.OUT});

		newLabels = Backtrack.leftTransition(framework, labels, 3);
		assertArrayEquals(newLabels, new Label[] {Label.BLANK, Label.OUT, Label.MUST_OUT, Label.IN});
	}

	@Test
	public void rightTransition() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);
		
		Label[] newLabels = Backtrack.rightTransition(framework, labels, 2);
		assertArrayEquals(newLabels, new Label[] {Label.BLANK, Label.BLANK, Label.UNDEC, Label.BLANK});
	}

	@Test
	public void isTerminalLabelling() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		labels[2] = Label.BLANK;		
		assertFalse(Backtrack.isTerminalLabelling(labels));

		labels[2] = Label.UNDEC;		
		assertTrue(Backtrack.isTerminalLabelling(labels));
	}

	@Test
	public void isDeadEndLabelling() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		assertFalse(Backtrack.isDeadEndLabelling(labels));

		labels[2] = Label.MUST_OUT;		
		assertTrue(Backtrack.isDeadEndLabelling(labels));
	}

	@Test
	public void isAdmissibleLabelling() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		labels[2] = Label.MUST_OUT;		
		assertFalse(Backtrack.isAdmissibleLabelling(labels));

		labels[2] = Label.UNDEC;		
		assertTrue(Backtrack.isAdmissibleLabelling(labels));

		labels[2] = Label.BLANK;		
		assertFalse(Backtrack.isAdmissibleLabelling(labels));
	}

	@Test
	public void selectAnyBlank() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		labels[2] = Label.BLANK;		
		assertFalse(Backtrack.isAdmissibleLabelling(labels));
		
		assertEquals(Label.BLANK, labels[Backtrack.selectAnyBlank(framework, labels)]);
	}
	
	@Test
	public void preferred1() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.preferred1(framework, labels, extensions);
		
		assertEquals(1, extensions.size());
		assertEquals(extensions, answer);
	}

	@Test
	public void isHopelessLabelling() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		assertFalse(Backtrack.isHopelessLabelling(framework, labels));

		labels[1] = Label.MUST_OUT;	labels[3] = Label.OUT;
		assertTrue(Backtrack.isHopelessLabelling(framework, labels));
		
		labels[0] = Label.IN;
		assertFalse(Backtrack.isHopelessLabelling(framework, labels));
	}	
	
	@Test
	public void preferred2() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));
		
		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.preferred2(framework, labels, extensions);
		
		assertEquals(1, extensions.size());
		assertEquals(extensions, answer);
	}
		
	@Test
	public void selectInfluential() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);	

		assertEquals(1, Backtrack.selectInfluential(framework, labels));

		labels[1] = Label.IN;
		assertEquals(2, framework.getNodeDegree((Backtrack.selectInfluential(framework, labels))));
	}

	@Test
	public void selectMustIn() {

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);	
		
		assertEquals(-1, Backtrack.selectMustIn(framework, labels));
		
		labels[0] = Label.MUST_OUT; labels[3] = Label.OUT;
		assertEquals(1, Backtrack.selectMustIn(framework, labels));
	}

	@Test
	public void preferred3() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.preferred3(framework, labels, extensions);
		
		assertEquals(1, extensions.size());
		assertEquals(extensions, answer);
	}

	@Test
	public void preferred4() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.preferred4(framework, labels, extensions);
		
		assertEquals(1, extensions.size());
		assertEquals(extensions, answer);
	}

	@Test
	public void admissible() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0})));
		answer.add(new HashSet<Integer>());

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.admissible(framework, labels, extensions);

		assertEquals(3, extensions.size());
		assertEquals(extensions, answer);
	}

	@Test
	public void isCompleteLabelling() {
		
		FastGraph framework = framework1;

		Label[] a = { Label.IN, Label.OUT, Label.IN, Label.OUT};
		Label[] b = { Label.IN, Label.OUT, Label.UNDEC, Label.UNDEC};
		
		assertTrue(Backtrack.isCompleteLabelling(framework, a));
		assertFalse(Backtrack.isCompleteLabelling(framework, b));
	} 

	@Test
	public void complete() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));
		answer.add(new HashSet<Integer>());

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.complete(framework, labels, extensions);
		
		assertEquals(2, extensions.size());
		assertEquals(extensions, answer);
	}

	@Test
	public void rightTransitionStable() {

		Label[] labels = new Label[framework1.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		Label[] newLabels = Backtrack.rightTransitionStable(labels, 2);
		assertEquals(Label.MUST_OUT, newLabels[2]);
	}

	@Test
	public void isStableLabelling() {

		Label[] labels = new Label[framework1.getNumberOfNodes()];
		Arrays.fill(labels, Label.UNDEC);	

		assertTrue(Backtrack.isStableLabelling(labels));
		
		labels[2] = Label.MUST_OUT;		
		assertFalse(Backtrack.isStableLabelling(labels));
		
		labels[1] = Label.BLANK; labels[2] = Label.UNDEC;		
		assertFalse(Backtrack.isStableLabelling(labels));
	}

	@Test
	public void enumerateStable() {
		
		Set<Set<Integer>> answer = new HashSet<Set<Integer>>();
		answer.add(new HashSet<Integer>(Arrays.asList(new Integer[] {0, 2})));

		FastGraph framework = framework1;
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);		
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		
		Backtrack.enumerateStable(framework, labels, extensions);
		
		assertEquals(1, extensions.size());
		assertEquals(extensions, answer);
	}
	
}
