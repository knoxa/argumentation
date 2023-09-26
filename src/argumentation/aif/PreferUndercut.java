package argumentation.aif;

import java.util.Comparator;

import uk.ac.kent.dover.fastGraph.FastGraph;

public class PreferUndercut implements Comparator<Integer> {
	
	FastGraph aif;

	@Override
	public int compare(Integer attacker, Integer attacked) {
		
		int[] potentialUndercutFrom = aif.getNodeConnectingOutNodes(attacker);
		int[] potentialUndercutTo   = aif.getNodeConnectingOutNodes(attacked);

		int x1 = potentialUndercutFrom[0] == attacked ? 1 : 0;
		int x2 = potentialUndercutTo[0]   == attacker ? 1 : 0;

		return x1 - x2;
	}

	public PreferUndercut(FastGraph aif) {

		this.aif = aif;
	}
}
