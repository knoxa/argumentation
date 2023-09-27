package test.argumentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import argumentation.aif.FastArgumentMap;
import argumentation.aif.LinkedArgumentMap;
import argumentation.engine.Backtrack;
import argumentation.engine.Label;
import argumentation.presumptive.Analysis;
import argumentation.presumptive.PresumptiveAAF;
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
	
	@Test
	public void linkedArgument() throws FileNotFoundException {
		
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, this.getClass().getResourceAsStream("aif-ntriples.txt"), Lang.NTRIPLES);
        
        LinkedArgumentMap map = new LinkedArgumentMap();
        map.setModel(model);
        
        FastGraph aif = map.getGraph();
        FastGraph framework = PresumptiveAAF.makeArgumentFramework(aif);
        
        Label[] labels = new Label[framework.getNumberOfNodes()];
        Arrays.fill(labels, Label.BLANK);
        Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
        Backtrack.stable(framework, labels, extensions);
        assertEquals(2, extensions.size());
        
        Set<Integer> extension0 = new HashSet<Integer>(); extension0.add(0);
        assertEquals(3, Analysis.getAcceptableInformation(aif, framework, extension0).size());

        Set<Integer> extension1 = new HashSet<Integer>(); extension1.add(1);
        assertEquals(1, Analysis.getAcceptableInformation(aif, framework, extension1).size());
        
        Map<String, String> claims = map.getClaimText();
        assertEquals("the plug is out", claims.get("I-2"));
        assertEquals("tap is on", claims.get("I-3"));
                
        Set<String> hypotheses = new HashSet<String>();
        hypotheses.addAll(Arrays.asList(new String[] {"I-2", "I-3"}));
        
        Map<Set<String>, Set<Set<Integer>>> partitions = Analysis.partitionExtensions(aif, framework, extensions, hypotheses);
        
        Set<String> partition0 = new HashSet<String>(); partition0.add("I-3");
        Set<String> partition1 = new HashSet<String>(); partition1.add("I-2");   
        assertTrue(partitions.get(partition0).contains(extension0));
        assertTrue(partitions.get(partition1).contains(extension1));
    }
}
