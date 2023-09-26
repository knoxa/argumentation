package argumentation.aif;

import java.util.Comparator;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class PreferContradiction implements Comparator<Integer> {
	
	FastGraph aif;

	@Override
	public int compare(Integer attacker, Integer attacked) {

		int x1 = aif.getNodeType(attacker) == FastArgumentMap.CA_NODE ? 1 : 0;
		int x2 = aif.getNodeType(attacked) == FastArgumentMap.CA_NODE ? 1 : 0;

		return x1 - x2;
	}

	public PreferContradiction(FastGraph aif) {

		this.aif = aif;
	}
}
