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
		Resource rdfProperty = model.createResource(rdfUri+"Property");
		
		Property rdfsSubclassOf = model.createProperty(rdfsUri+"subclassOf");
		Property rdfsDomain = model.createProperty(rdfsUri+"domain");
		Property rdfsRange = model.createProperty(rdfsUri+"range");

		Resource foafPerson = model.createResource(foafUri+"Person");
		Resource foafOrganization = model.createResource(foafUri+"Organization");

		Resource xsdInt = model.createResource(xsdUri+"int");
		Resource xsdBoolean = model.createResource(xsdUri+"boolean");
		Resource xsdString = model.createResource(xsdUri+"string");
		
		// Own classes
		Resource gameConsole = model.createResource(ownUri+"GameConsole");
		Resource portableGameConsole = model.createResource(ownUri+"PortableGameConsole");

		// Add statements to the model
		model.add(model.createStatement(gameConsole, rdfType, rdfClass));
		model.add(model.createStatement(portableGameConsole, rdfType, rdfClass));
		model.add(model.createStatement(portableGameConsole, rdfsSubclassOf, gameConsole));

		// Add properties to the model
		Resource ceo = model.createProperty(ownUri+"ceo");
		ceo.addProperty(rdfType, rdfProperty);
		ceo.addProperty(rdfsDomain, foafOrganization);
		ceo.addProperty(rdfsRange, foafPerson);
		
		Resource foundingYear = model.createProperty(ownUri+"foundingYear");
		foundingYear.addProperty(rdfType, rdfProperty);
		foundingYear.addProperty(rdfsDomain, foafOrganization);
		foundingYear.addProperty(rdfsRange, xsdInt);

		Resource internetEnabled = model.createProperty(ownUri+"internetEnabled");
		internetEnabled.addProperty(rdfType, rdfProperty);
		internetEnabled.addProperty(rdfsDomain, gameConsole);
		internetEnabled.addProperty(rdfsRange, xsdBoolean);
		
		Resource consoleName = model.createProperty(ownUri+"consoleName");
		consoleName.addProperty(rdfType, rdfProperty);
		consoleName.addProperty(rdfsDomain, gameConsole);
		consoleName.addProperty(rdfsRange, xsdString);
		
		Resource numOfSupportedControllers = model.createProperty(ownUri+"numOfSupportedControllers");
		numOfSupportedControllers.addProperty(rdfType, rdfProperty);
		numOfSupportedControllers.addProperty(rdfsDomain, gameConsole);
		numOfSupportedControllers.addProperty(rdfsRange, xsdInt);
		
		Resource predecessorOfConsole = model.createProperty(ownUri+"predecessorOfConsole");
		predecessorOfConsole.addProperty(rdfType, rdfProperty);
		predecessorOfConsole.addProperty(rdfsDomain, gameConsole);
		predecessorOfConsole.addProperty(rdfsRange, gameConsole);
		
		Resource successorOfConsole = model.createProperty(ownUri+"successorOfConsole");
		successorOfConsole.addProperty(rdfType, rdfProperty);
		successorOfConsole.addProperty(rdfsDomain, gameConsole);
		successorOfConsole.addProperty(rdfsRange, gameConsole);

		Resource relatedToConsole = model.createProperty(ownUri+"relatedToConsole");
		relatedToConsole.addProperty(rdfType, rdfProperty);
		relatedToConsole.addProperty(rdfsDomain, gameConsole);
		relatedToConsole.addProperty(rdfsRange, gameConsole);

		Resource releaseYear = model.createProperty(ownUri+"releaseYear");
		releaseYear.addProperty(rdfType, rdfProperty);
		releaseYear.addProperty(rdfsDomain, gameConsole);
		releaseYear.addProperty(rdfsRange, xsdInt);

		Resource madeBy = model.createProperty(ownUri+"madeBy");
		madeBy.addProperty(rdfType, rdfProperty);
		madeBy.addProperty(rdfsDomain, gameConsole);
		madeBy.addProperty(rdfsRange, foafOrganization);

		// Store the model into a file
		Generator.writeModelToFile(model, "./output/tbox.ttl");
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
