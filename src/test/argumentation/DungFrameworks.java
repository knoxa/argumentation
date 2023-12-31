package test.argumentation;

import java.util.Arrays;

import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;

public class DungFrameworks {

	public static FastGraph getFramework1() {

		// This is the AAF from Fig. 3 of https://doi.org/10.1016/j.ijar.2016.07.013
		
		NodeStructure[] n = new NodeStructure[4];
		EdgeStructure[] e = new EdgeStructure[5];
		
		n[0] = new NodeStructure(0, "a", 0, (byte) 0, (byte) 0);
		n[1] = new NodeStructure(1, "b", 0, (byte) 0, (byte) 0);
		n[2] = new NodeStructure(2, "c", 0, (byte) 0, (byte) 0);
		n[3] = new NodeStructure(3, "d", 0, (byte) 0, (byte) 0);
		
		e[0] = new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, n[0].getId(), n[1].getId());
		e[1] = new EdgeStructure(1, "", 0, (byte) 0, (byte) 0, n[1].getId(), n[0].getId());
		e[2] = new EdgeStructure(2, "", 0, (byte) 0, (byte) 0, n[1].getId(), n[2].getId());
		e[3] = new EdgeStructure(3, "", 0, (byte) 0, (byte) 0, n[2].getId(), n[3].getId());
		e[4] = new EdgeStructure(4, "", 0, (byte) 0, (byte) 0, n[3].getId(), n[1].getId());
		
		
		FastGraph framework = FastGraph.structureFactory("AAF1", (byte) 0, Arrays.asList(n), Arrays.asList(e), true);
		return framework;
	}

	public static FastGraph getFramework2() {

		// This is the AAF describe in section 3.5 of https://doi.org/10.1016/j.ijar.2016.07.013
		
		NodeStructure[] n = new NodeStructure[3];
		EdgeStructure[] e = new EdgeStructure[4];
		
		n[0] = new NodeStructure(0, "a", 0, (byte) 0, (byte) 0);
		n[1] = new NodeStructure(1, "b", 0, (byte) 0, (byte) 0);
		n[2] = new NodeStructure(2, "c", 0, (byte) 0, (byte) 0);
		
		e[0] = new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, n[0].getId(), n[1].getId());
		e[1] = new EdgeStructure(1, "", 0, (byte) 0, (byte) 0, n[1].getId(), n[0].getId());
		e[2] = new EdgeStructure(2, "", 0, (byte) 0, (byte) 0, n[1].getId(), n[2].getId());
		e[3] = new EdgeStructure(3, "", 0, (byte) 0, (byte) 0, n[2].getId(), n[2].getId());
		
		
		FastGraph framework = FastGraph.structureFactory("AAF2", (byte) 0, Arrays.asList(n), Arrays.asList(e), true);
		return framework;
	}

	public static FastGraph getFramework3() {

		// This AAF has a grounded extension: [0, 2]
		
		NodeStructure[] n = new NodeStructure[3];
		EdgeStructure[] e = new EdgeStructure[1];
		
		n[0] = new NodeStructure(0, "a", 0, (byte) 0, (byte) 0);
		n[1] = new NodeStructure(1, "b", 0, (byte) 0, (byte) 0);
		n[2] = new NodeStructure(2, "c", 0, (byte) 0, (byte) 0);
		
		e[0] = new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, n[0].getId(), n[1].getId());		

		FastGraph framework = FastGraph.structureFactory("AAF3", (byte) 0, Arrays.asList(n), Arrays.asList(e), true);
		return framework;
	}

}
