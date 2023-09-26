package test.argumentation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import argumentation.aif.FastArgumentMap;
import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class DummyArgumentMap implements FastArgumentMap {

	@Override
	public FastGraph getGraph() {
	
		NodeStructure[] n = new NodeStructure[7];
		EdgeStructure[] e = new EdgeStructure[6];
		
		n[0] = new NodeStructure(0, "I-1",  0, (byte) I_NODE,  (byte) 0);
		n[1] = new NodeStructure(1, "I-2",  0, (byte) I_NODE,  (byte) 0);
		n[2] = new NodeStructure(2, "I-3",  0, (byte) I_NODE,  (byte) 0);
		n[3] = new NodeStructure(3, "RA-1", 0, (byte) RA_NODE, (byte) 0);
		n[4] = new NodeStructure(4, "CA-1", 0, (byte) CA_NODE, (byte) 0);
		n[5] = new NodeStructure(5, "I-4",  0, (byte) I_NODE,  (byte) 0);
		n[6] = new NodeStructure(6, "CA-2", 0, (byte) CA_NODE, (byte) 0);

		e[0] = new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, n[0].getId(), n[3].getId());
		e[1] = new EdgeStructure(1, "", 0, (byte) 0, (byte) 0, n[1].getId(), n[4].getId());
		e[2] = new EdgeStructure(2, "", 0, (byte) 0, (byte) 0, n[3].getId(), n[2].getId());
		e[3] = new EdgeStructure(3, "", 0, (byte) 0, (byte) 0, n[4].getId(), n[2].getId());
		e[4] = new EdgeStructure(4, "", 0, (byte) 0, (byte) 0, n[5].getId(), n[6].getId());
		e[5] = new EdgeStructure(5, "", 0, (byte) 0, (byte) 0, n[6].getId(), n[1].getId());

		FastGraph framework = FastGraph.structureFactory("AIF", (byte) 0, Arrays.asList(n), Arrays.asList(e), true);
		return framework;
	}

	@Override
	public Map<String, String> getClaimText() {
		
		Map<String, String> claimText = new HashMap<String, String>();
		return claimText;
	}
	
}
