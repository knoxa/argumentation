package argumentation.aif;

import uk.ac.kent.dover.fastGraph.FastGraph;

public interface FastArgumentMap {

    public static final byte I_NODE = 0, MA_NODE = 1, RA_NODE = 2, CA_NODE = 3, L_NODE = 4, TA_NODE = 5;
    
    public FastGraph getGraph();

}
