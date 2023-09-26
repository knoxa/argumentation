package argumentation.presumptive;

import java.util.Comparator;

class PreferenceDefault implements Comparator<Integer> {

	@Override
	public int compare(Integer attacker, Integer attacked) {

		// The default is no preference - all arguments are equal ...
		return 0;
	}
}
