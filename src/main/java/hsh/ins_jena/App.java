package hsh.ins_jena;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;

import hsh.ins_jena.model.Generator;

public class App {
    public static String OUTPUT_PATH = "./output";
    public static String SPARQL_ENDPOINT = "http://localhost:3030/test/query";

    public static void main(String[] args) throws IOException {
        generateFiles(OUTPUT_PATH);
        readAndHandleFiles(OUTPUT_PATH);
    }

    private static void readAndHandleFiles(String inputPath) throws IOException {
        // Model tboxModel = FileManager.get().loadModel("file:" + inputPath + "/" Generator.T_BOX_FILENAME_XML);
        // Model aboxModel = FileManager.get().loadModel("file:" + inputPath + "/" Generator.A_BOX_FILENAME_XML);
        Model tboxModel = FileManager.get().loadModel("file:" + "data/tbox.ttl");
        Model aboxModel = FileManager.get().loadModel("file:" + "data/abox.ttl");

        // Let's create an owl reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(tboxModel);
        //OntModelSpec ontModelSpec = OntModelSpec.OWL_DL_MEM_RULE_INF;
        //ontModelSpec.setReasoner(reasoner);
        InfModel infModel = ModelFactory.createInfModel(reasoner, aboxModel);

        validate(infModel);
        reasoner = new GenericRuleReasoner(Rule.rulesFromURL("data/jenarules.txt"));
        infModel = ModelFactory.createInfModel(reasoner, infModel);
        
        validate(infModel);

        printResource(infModel, "JPT");
        printResource(infModel, "Wii");
        printResource(infModel, "Wii_u");
        printResource(infModel, "Switch");
        printResource(infModel, "HomeConsole");
        printResource(infModel, "PartyConsole");
        printResource(infModel, "NintendoConsole");

        //@formatter:off
        
        // Let's define a bunch of queries
        Query queryReleaseYear = QueryFactory.create(
                "PREFIX : <" + Generator.OWN_URI + ">" + 
                "SELECT ?console\n" + "WHERE {\n" + 
                "  ?console :madeBy :Nintendo .\n" + 
                "  ?console :releaseYear ?releaseYear .  \n" + 
                "  FILTER(?releaseYear > 2015)\n" + 
                "}");

        Query queryNintendoConsoles = QueryFactory.create(
                "PREFIX : <" + Generator.OWN_URI + ">" + 
                "PREFIX rdf: <" + Generator.RDF_URI + ">" + 
                "SELECT ?console\n" + "WHERE {\n" + 
                "  ?console rdf:type :NintendoConsole .\n" + 
                "}");

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
                "  ?ceo rdf:type foaf:Person .\n" 
            + "}\n" + "");

        Query queryAll = QueryFactory.create(
                "PREFIX : <" + Generator.OWN_URI + ">" + 
                " SELECT ?s ?p ?p WHERE {" + "?s ?p ?o .}");
        
         //@formatter:on

        // Let's execute one query and print its results
        //QueryExecution queryExecLocalConsoles = QueryExecutionFactory.create(queryAll, infModel);
        //System.err.println("Show consoles from local model");
        // printQueryResult(queryExecLocalConsoles);

        QueryExecution queryExecNintendoConsoles = QueryExecutionFactory.create(queryNintendoConsoles, infModel);
        System.out.println(">> Doing local NintendoConsoles query");
        printQueryResult(queryExecNintendoConsoles);

        // Let's do more
        QueryExecution queryExecLocalReleaseDate = QueryExecutionFactory.create(queryReleaseYear, infModel);
        QueryExecution queryExecRemoteReleaseDate = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, queryReleaseYear);

        QueryExecution queryExecLocalCEOtoConsole = QueryExecutionFactory.create(queryCEOForConsole, infModel);
        QueryExecution queryExecRemoteCEOtoConsole = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, queryCEOForConsole);

        System.out.println(">> Doing local release date query");
        printQueryResult(queryExecLocalReleaseDate);
        System.out.println(">> Doing remote release date query");
        printQueryResult(queryExecRemoteReleaseDate);

        System.out.println(">> Doing local CEO query");
        printQueryConstructs(queryExecLocalCEOtoConsole);
        System.out.println(">> Doing remote CEO query");
        printQueryConstructs(queryExecRemoteCEOtoConsole);
    }

    private static void validate(InfModel model) {
        ValidityReport validity = model.validate();
        if (validity.isValid()) {
            System.out.println("Validation: OK");
        } else {
            System.out.println("Conflicts");
            for (Iterator<Report> i = validity.getReports(); i.hasNext();) {
                ValidityReport.Report report = (ValidityReport.Report) i.next();
                System.out.println(" - " + report);
            }
        }

    }

    private static void printResource(Model model, String resource) {
        Resource nForce = model.getResource("http://example.com/ins_uebung/#" + resource);
        System.out.println(resource + "*:");
        printStatements(model, nForce, null, null);
    }

    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
            Statement stmt = i.nextStatement();
            System.out.println(" - " + PrintUtil.print(stmt));
        }
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
