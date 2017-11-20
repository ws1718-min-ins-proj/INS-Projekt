package hsh.ins_jena.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Generator {

	// Our own namespace uri
	private static String ownUri = "http://example.com/ins_uebung/#";

	// Uris for other vocabularies we use
	private static String rdfUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static String rdfsUri = "http://www.w3.org/2000/01/rdf-schema#";
	private static String xsdUri = "http://www.w3.org/2001/XMLSchema#";
	private static String foafUri = "http://xmlns.com/foaf/0.1/";
	private static String revUri = "http://purl.org/stuff/rev#";
	
	public static void createTBox() {
		Model model = ModelFactory.createDefaultModel();

		// Some boilerplate stuff
		Property rdfType = model.createProperty(rdfUri+"type");
		Resource rdfClass = model.createResource(rdfUri+"Class");
		
		Property rdfsSubclassOf = model.createProperty(rdfsUri+"subclassOf");

		Resource gameConsole = model.createResource(ownUri+"GameConsole");
		Resource portableGameConsole = model.createResource(ownUri+"PortableGameConsole");
		
		model.add(model.createStatement(gameConsole, rdfType, rdfClass));
		model.add(model.createStatement(portableGameConsole, rdfType, rdfClass));
		model.add(model.createStatement(portableGameConsole, rdfsSubclassOf, gameConsole));
		
		/*
		Resource donald = model.createResource(familyUri + "donald");
		Resource tick = model.createResource(familyUri + "tick");

		Property childOf = model.createProperty(relationshipUri, "childOf");
		Property knows = model.createProperty(relationshipUri, "knows");
		tick.addProperty(childOf, donald);

		Statement st1 = model.createStatement(donald, knows, tick);
		model.add(st1);
		*/
		
		Generator.writeModelToFile(model, "./tbox.ttl");
	}
	
	private static void writeModelToFile(Model model, String path) {
		FileOutputStream outfile = null;
		try {
			outfile = new FileOutputStream(path);
			model.write(outfile, "TURTLE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
