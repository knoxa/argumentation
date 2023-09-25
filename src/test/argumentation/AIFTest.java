package test.argumentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import argumentation.aif.FastArgumentMap;
import argumentation.aif.PresumptiveAAF;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class AIFTest {

	@Test
	public void makeAAF() {
		
		FastArgumentMap map = new DummyArgumentMap();
		FastGraph framework = PresumptiveAAF.makeArgumentFramework(map.getGraph());
		
		assertEquals(3, framework.getNumberOfNodes());
		
		IntStream.range(0, framework.getNumberOfNodes());
		
		Set<String> labels = IntStream.range(0, framework.getNumberOfNodes())
				.mapToObj(x -> framework.getNodeLabel(x))
				.collect(Collectors.toSet());
		
		Set<String> answer = new HashSet<String>();
		answer.addAll(Arrays.asList(new String[] {"CA-1", "CA-2", "RA-1"}));
		
		assertEquals(answer, labels);
	}
}
