package hsh.ins_jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import hsh.ins_jena.model.Generator;

public class App {
	public static String OUTPUT_PATH = "./output";
	public static String SPARQL_ENDPOINT = "http://localhost:3030/foobar/query";

	public static void main(String[] args) {
		generateFiles(OUTPUT_PATH);
		readAndHandleFiles(OUTPUT_PATH);
	}

	private static void readAndHandleFiles(String inputPath) {
		Model model = ModelFactory.createDefaultModel();

		InputStream inT, inA;
		try {
			inT = new FileInputStream(new File(inputPath + "/" + Generator.T_BOX_FILENAME_XML));
			inA = new FileInputStream(new File(inputPath + "/" + Generator.A_BOX_FILENAME_XML));
			model.read(inT, "");
			model.read(inA, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.err.println("Loaded model:");
		System.out.println(model);

		Query queryReleaseYear = QueryFactory.create(
				"PREFIX : <" + Generator.OWN_URI + ">" 
				+ "SELECT ?console\n"
				+ "WHERE {\n" + "  ?console :madeBy :Nintendo .\n" + "  ?console :releaseYear ?releaseYear .  \n"
				+ "  FILTER(?releaseYear > 2015)\n" + "}");
		
		Query queryCEOForConsole = QueryFactory.create(
			"PREFIX : <" + Generator.OWN_URI + ">" +
			"PREFIX rdf: <" + Generator.RDF_URI + ">" +
			"PREFIX rdfs: <" + Generator.RDFS_URI + ">" +
			"PREFIX foaf: <" + Generator.FOAF_URI + ">" +
			"CONSTRUCT {\n" + 
			"  ?ceo :isRelatedToConsole ?console .\n" + 
			"}\n" + 
			"WHERE {\n" + 
			"  ?console rdf:type :GameConsole .\n" + 
			"  ?console :madeBy  ?org .\n" + 
			"  ?org :ceo ?ceo .\n" + 
			"  ?ceo rdf:type foaf:Person .\n" + 
			"}\n" + 
			"");

//		Query query = QueryFactory
//				.create("PREFIX : <" + Generator.OWN_URI + ">" + " " + " SELECT ?s ?p ?p WHERE {" + "?s ?p ?o .}");

		QueryExecution queryExecLocalReleaseDate = QueryExecutionFactory.create(queryReleaseYear, model);
		QueryExecution queryExecRemoteReleaseDate = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT,queryReleaseYear);
		
		QueryExecution queryExecLocalCEOtoConsole = QueryExecutionFactory.create(queryCEOForConsole, model);
		QueryExecution queryExecRemoteCEOtoConsole = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT,queryCEOForConsole);
		
		System.err.println("Doing local release date query");
		printQueryResult(queryExecLocalReleaseDate); 
		System.err.println("Doing remote release date query");
		printQueryResult(queryExecRemoteReleaseDate); 
		
		System.err.println("Doing local CEO query");
		printQueryConstructs(queryExecLocalCEOtoConsole);
		System.err.println("Doing remote CEO query");
		printQueryConstructs(queryExecRemoteCEOtoConsole);
	}
	
	private static void printQueryResult(QueryExecution queryExec) {
		queryExec.execSelect().forEachRemaining(qs -> System.out.println(qs));
	}
	
	private static void printQueryConstructs(QueryExecution queryExec) {		
		queryExec.execConstruct().listStatements().toList().stream().forEach(s -> System.out.println(s));
	}

	private static void generateFiles(String outputhPath) {
		new File(OUTPUT_PATH).mkdirs();
		System.err.println("Generating model and saving it...");
		Generator.createTBoxAndABox(OUTPUT_PATH);
		System.out.println("done.");
	}
}
