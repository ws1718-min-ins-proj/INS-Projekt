package hsh.ins_jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import hsh.ins_jena.model.Generator;

public class App {
	public static String OUTPUT_PATH = "./output";

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

		System.out.println(model);

		Query query = QueryFactory.create("" + "PREFIX : <" + Generator.OWN_URI + ">" + "" + "SELECT ?console\n"
				+ "WHERE {\n" + "  ?console :madeBy :Nintendo .\n" + "  ?console :releaseYear ?releaseYear .  \n"
				+ "  FILTER(?releaseYear > 2015)\n" + "}");

//		Query query = QueryFactory
//				.create("PREFIX : <" + Generator.OWN_URI + ">" + " " + " SELECT ?s ?p ?p WHERE {" + "?s ?p ?o .}");

		QueryExecution queryExec = QueryExecutionFactory.create(query, model);

		ResultSet rs = queryExec.execSelect();

		if (rs.hasNext()) {
			rs.forEachRemaining(qs -> {
				System.out.println(qs);
			});
		}

	}

	private static void generateFiles(String outputhPath) {
		new File(OUTPUT_PATH).mkdirs();
		System.out.print("Generating ... ");
		Generator.createTBoxAndABox(OUTPUT_PATH);
		System.out.println("done.");

	}
}
