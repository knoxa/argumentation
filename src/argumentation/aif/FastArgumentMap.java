package argumentation.aif;

import java.util.Map;

import uk.ac.kent.dover.fastGraph.FastGraph;

public interface FastArgumentMap {

    public static final byte I_NODE = 0, MA_NODE = 1, RA_NODE = 2, CA_NODE = 3, L_NODE = 4, TA_NODE = 5, YA_NODE = 5;
    public static final String[] prefix = new String[] {"I-", "MA-", "RA-", "CA-", "L-", "TA-", "YA-"};

    public FastGraph getGraph();
    
    public Map<String, String> getClaimText();
}
