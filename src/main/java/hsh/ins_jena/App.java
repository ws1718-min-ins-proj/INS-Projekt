package hsh.ins_jena;

import java.io.File;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;

import hsh.ins_jena.model.Generator;

public class App {
	public static String OUTPUT_PATH = "./output";
	public static String SPARQL_ENDPOINT = "http://localhost:3030/foobar/query";

	public static void main(String[] args) {
		generateFiles(OUTPUT_PATH);
		readAndHandleFiles(OUTPUT_PATH);
	}

	private static void readAndHandleFiles(String inputPath) {
		Model tboxModel = FileManager.get().loadModel("file:" + inputPath + "/" + Generator.T_BOX_FILENAME_XML);
		Model aboxModel = FileManager.get().loadModel("file:" + inputPath + "/" + Generator.T_BOX_FILENAME_XML);

		// Combine both models to an RDFS model
		InfModel rdfsModel = ModelFactory.createRDFSModel(tboxModel, aboxModel);
		
		// Do some validity checking
		ValidityReport validity = rdfsModel.validate();
		if (validity.isValid()) {
		    System.out.println("\nRDFSModel Validity Report: OK");
		} else {
		    System.out.println("\nRDFSModel Validity Report: Conflicts!");
		    for (Iterator<ValidityReport.Report> i = validity.getReports(); i.hasNext(); ) {
		        ValidityReport.Report report = (ValidityReport.Report)i.next();
		        System.out.println(" - " + report);
		    }
		}
		
		// Let's create an rdfs reasoner
		Reasoner rdfsReasoner = ReasonerRegistry.getRDFSReasoner();
		rdfsReasoner = rdfsReasoner.bindSchema(rdfsModel);
		InfModel infModel = ModelFactory.createInfModel(rdfsReasoner, rdfsModel);

		System.err.println("infModel");
		System.out.println(infModel);
		
		// Let's define a bunch of queries
		Query queryReleaseYear = QueryFactory.create(
				"PREFIX : <" + Generator.OWN_URI + ">" 
				+ "SELECT ?console\n"
				+ "WHERE {\n" + "  ?console :madeBy :Nintendo .\n" + "  ?console :releaseYear ?releaseYear .  \n"
				+ "  FILTER(?releaseYear > 2015)\n" + "}");
		
		Query queryConsoles = QueryFactory.create(
				"PREFIX : <" + Generator.OWN_URI + ">" 
				+ "SELECT ?console\n"
				+ "WHERE {\n" 
				+ "  ?console :madeBy :Nintendo .\n"
				+ "}");

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

		Query queryAll = QueryFactory.create("PREFIX : <" + Generator.OWN_URI + ">" + " " + " SELECT ?s ?p ?p WHERE {" + "?s ?p ?o .}");

		// Let's execute one query and print its results
		QueryExecution queryExecLocalConsoles = QueryExecutionFactory.create(queryAll, infModel);
		System.err.println("Show consoles from local model");
		printQueryResult(queryExecLocalConsoles);
		
		// Let's do more
		QueryExecution queryExecLocalReleaseDate = QueryExecutionFactory.create(queryReleaseYear, infModel);
		QueryExecution queryExecRemoteReleaseDate = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, queryReleaseYear);
		
		QueryExecution queryExecLocalCEOtoConsole = QueryExecutionFactory.create(queryCEOForConsole, infModel);
		QueryExecution queryExecRemoteCEOtoConsole = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, queryCEOForConsole);
		
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
		System.out.println("Generating model and saving it...");
		Generator.createTBoxAndABox(OUTPUT_PATH);
		System.out.println("done.");
	}
}
