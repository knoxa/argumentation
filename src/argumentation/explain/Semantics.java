package argumentation.explain;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;


public class Semantics {

	public static Set<Resource> hypotheses(Model model) {
		
        Set<Resource> resources = new HashSet<Resource>();
        
		try {
			String queryString = IOUtils.toString(Semantics.class.getResourceAsStream("hypotheses.sparql"), "UTF-8");
			
			Query qry = QueryFactory.create(queryString);
	        QueryExecution qe = QueryExecutionFactory.create(qry, model);
	        ResultSet rs = qe.execSelect();
	        
	        while( rs.hasNext() ) {
	        	
	            QuerySolution sol = rs.nextSolution();
	            RDFNode conclusion = sol.get("inode");
	            resources.add(conclusion.asResource());
	        }
	        
	        return resources;
		}
		catch (IOException e) {
			e.printStackTrace();
			return resources;
		}
	}

}
