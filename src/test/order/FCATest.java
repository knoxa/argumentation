package test.order;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import argumentation.aif.FastArgumentMap;
import argumentation.engine.Backtrack;
import argumentation.engine.Label;
import argumentation.presumptive.Analysis;
import argumentation.presumptive.PresumptiveAAF;
import order.ContextBuilder;
import order.FormalConceptAnalysis;
import order.Lattice;
import test.argumentation.DummyArgumentMap;
import uk.ac.kent.dover.fastGraph.FastGraph;

public class FCATest {

	Map<Integer, Set<String>> textbook;
	Map<String, Set<Set<Integer>>> acceptable;
	
	@Before
	public void setUp() {
		
		textbook = getExampleTextBook();
		
		FastArgumentMap map = new DummyArgumentMap();
		FastGraph aif = map.getGraph();
		FastGraph framework = PresumptiveAAF.makeArgumentFramework(aif);
        Label[] labels = new Label[framework.getNumberOfNodes()];
        Arrays.fill(labels, Label.BLANK);
        Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
        Backtrack.stable(framework, labels, extensions);
        acceptable = Analysis.getInformationObjects(aif, framework, extensions);
	}
	
	@Test
	public void eliminationOrder() {
		
		List<Entry<Integer, Set<String>>> order1 = FormalConceptAnalysis.eliminationOrder(textbook);

		assertEquals((Integer) 1, (Integer) order1.get(0).getKey());
		assertEquals(5, order1.get(0).getValue().size());
		
		assertEquals((Integer) 4, (Integer) order1.get(2).getKey());
		assertEquals(4, order1.get(2).getValue().size());

		List<Entry<String, Set<Set<Integer>>>> order2 = FormalConceptAnalysis.eliminationOrder(acceptable);

		assertEquals("I-4", order2.get(0).getKey());
		assertEquals(1, order2.get(0).getValue().size());
		
		assertEquals("I-3", order2.get(2).getKey());
		assertEquals(1, order2.get(2).getValue().size());
		
		Lattice<String, Integer> l = FormalConceptAnalysis.order(textbook);
		assertEquals(10, l.getExtents().size());
	}
	
	@Test
	public void contextBuilder() {
		
		ContextBuilder builder = new ContextBuilder();
		AttributesImpl attr;
		String attribute;
		
		try {
			builder.startDocument();
			builder.startElement("", "context", "context", null);

			attr = new AttributesImpl();
			attr.addAttribute("", "name",  "name", "String", "temporary");
			builder.startElement("", "object", "object", attr);

			builder.startElement("", "attribute", "attribute", attr);
			attribute = "puddle";
			builder.characters(attribute.toCharArray(), 0, attribute.length());
			builder.endElement("", "attribute", "attribute");

			builder.endElement("", "object", "object");

			attr = new AttributesImpl();
			attr.addAttribute("", "name",  "name", "String", "running");
			builder.startElement("", "object", "object", attr);

			builder.startElement("", "attribute", "attribute", attr);
			attribute = "canal";
			builder.characters(attribute.toCharArray(), 0, attribute.length());
			builder.endElement("", "attribute", "attribute");

			builder.startElement("", "attribute", "attribute", attr);
			attribute = "channel";
			builder.characters(attribute.toCharArray(), 0, attribute.length());
			builder.endElement("", "attribute", "attribute");

			builder.endElement("", "object", "object");

			builder.endElement("", "context", "context");
			builder.endDocument();
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		
		Map<String, Set<String>> context = builder.getContext();
		assertEquals(3, context.size());
		assertEquals(1, context.get("canal").size());
		
		builder = new ContextBuilder();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(builder);
			reader.parse("src\\test\\order\\water.xml");
		}
		catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		context = builder.getContext();
		assertEquals(6, context.size());
		
		//FormalConceptAnalysis.order(context);
	}
	
	public static Map<Integer, Set<String>> getExampleTextBook() {

		Set<String> a = new HashSet<String>();
		Set<String> b = new HashSet<String>();
		Set<String> c = new HashSet<String>();
		Set<String> d = new HashSet<String>();
		Set<String> e = new HashSet<String>();
		Set<String> f = new HashSet<String>();
		Set<String> g = new HashSet<String>();
		
		String S = new String("S");
		String T = new String("T");
		String U = new String("U");
		String V = new String("V");
		String W = new String("W");
		String X = new String("X");
		
		a.add(S); a.add(T); a.add(U); a.add(V); a.add(X);
		b.add(S); b.add(T); b.add(U); b.add(W);
		c.add(V);
		d.add(S); d.add(T); d.add(U); d.add(W);
		e.add(T); e.add(U); e.add(V);
		f.add(S); f.add(U); f.add(V); f.add(X);
		g.add(U);
		
		Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();
		
		map.put(1, a);
		map.put(2, b);
		map.put(3, c);
		map.put(4, d);
		map.put(5, e);
		map.put(6, f);
		map.put(7, g);
		
		return map;
	}
	
	public static Map<String, Set<String>> getExampleWater() {

		Set<String> temporary = new HashSet<String>();
		Set<String> running = new HashSet<String>();
		Set<String> natural = new HashSet<String>();
		Set<String> stagnant = new HashSet<String>();
		Set<String> constant = new HashSet<String>();
		Set<String> maritime = new HashSet<String>();
		
		String canal = new String("canal");
		String channel = new String("channel");
		String lagoon = new String("lagoon");
		String lake = new String("lake");
		String maar = new String("maar");
		String puddle = new String("puddle");
		String pond = new String("pond");
		String pool = new String("pool");
		String reservoir = new String("reservoir");
		String river = new String("river");
		String rivulet = new String("rivulet");
		String runnel = new String("runnel");
		String sea = new String("sea");
		String stream = new String("stream");
		String tarn = new String("tarn");
		String torrent = new String("torrent");
		String trickle = new String("trickle");
		
		temporary.add(puddle);
		running.add(canal); running.add(channel); running.add(river); running.add(rivulet); running.add(runnel); running.add(stream); running.add(torrent); running.add(trickle);
		natural.add(lagoon); natural.add(lake); natural.add(maar); natural.add(puddle); natural.add(pond); natural.add(pool); natural.add(river); natural.add(rivulet); natural.add(runnel); natural.add(sea); natural.add(stream); natural.add(tarn); natural.add(torrent); natural.add(trickle);
		stagnant.add(lagoon); stagnant.add(lake); stagnant.add(maar); stagnant.add(puddle); stagnant.add(pond); stagnant.add(pool); stagnant.add(reservoir); stagnant.add(sea); stagnant.add(tarn);
		constant.add(canal); constant.add(channel); constant.add(lagoon); constant.add(lake); constant.add(maar); constant.add(pond); constant.add(pool); constant.add(reservoir); constant.add(river); constant.add(rivulet); constant.add(runnel); constant.add(sea); constant.add(stream); constant.add(tarn); constant.add(torrent); constant.add(trickle);
		maritime.add(lagoon); maritime.add(sea);
		
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		
		map.put("temporary", temporary);
		map.put("running", running);
		map.put("natural", natural);
		map.put("stagnant", stagnant);
		map.put("constant", constant);
		map.put("maritime", maritime);
		
		return map;
	}
	


}
