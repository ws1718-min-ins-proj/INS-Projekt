package hsh.ins_jena.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Generator {

	public static final String T_BOX_FILENAME_TTL = "tbox.ttl";
	public static final String T_BOX_FILENAME_JSON = "tbox.json";
	public static final String T_BOX_FILENAME_XML = "tbox.xml";

	public static final String A_BOX_FILENAME_TTL = "abox.ttl";
	public static final String A_BOX_FILENAME_JSON = "abox.json";
	public static final String A_BOX_FILENAME_XML = "abox.xml";

	// Our own namespace uri
	public static final String OWN_URI = "http://example.com/ins_uebung/#";

	// Uris for other vocabularies we use
	public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema#";
	public static final String FOAF_URI = "http://xmlns.com/foaf/0.1/";
	public static final String REV_URI = "http://purl.org/stuff/rev#";
	
	public static final String MODEL_TURTLE = "TURTLE";
	public static final String MODEL_JSON = "JSON-LD";
	public static final String MODEL_RDF_XML = "RDF/XML";

	public static void createTBoxAndABox() {
		createTBoxAndABox(".");
	}

	public static void createTBoxAndABox(String outputPath) {
		Model tboxModel = ModelFactory.createDefaultModel();

		// Some boilerplate stuff
		Property rdfType = tboxModel.createProperty(RDF_URI + "type");
		Resource rdfClass = tboxModel.createResource(RDF_URI + "Class");
		Resource rdfProperty = tboxModel.createResource(RDF_URI + "Property");

		Property rdfsSubclassOf = tboxModel.createProperty(RDFS_URI + "subclassOf");
		Property rdfsSubPropertyOf = tboxModel.createProperty(RDFS_URI + "subPropertyOf");
		Property rdfsDomain = tboxModel.createProperty(RDFS_URI + "domain");
		Property rdfsRange = tboxModel.createProperty(RDFS_URI + "range");
		Property rdfsLabel = tboxModel.createProperty(RDFS_URI + "label");
		Resource rdfsDatatype = tboxModel.createResource(RDFS_URI + "Datatype");

		Resource foafPerson = tboxModel.createResource(FOAF_URI + "Person");
		Resource foafOrganization = tboxModel.createResource(FOAF_URI + "Organization");

		Resource xsdInt = tboxModel.createResource(XSD_URI + "int");
		Resource xsdBoolean = tboxModel.createResource(XSD_URI + "boolean");
		Resource xsdString = tboxModel.createResource(XSD_URI + "string");

		// Own classes
		Resource gameConsole = tboxModel.createResource(OWN_URI + "GameConsole");
		Resource portableGameConsole = tboxModel.createResource(OWN_URI + "PortableGameConsole");

		// Add statements to the model
		tboxModel.add(tboxModel.createStatement(gameConsole, rdfType, rdfClass));
		tboxModel.add(tboxModel.createStatement(portableGameConsole, rdfType, rdfClass));
		tboxModel.add(tboxModel.createStatement(portableGameConsole, rdfsSubclassOf, gameConsole));

		// Add properties to the model
		Property ceo = tboxModel.createProperty(OWN_URI + "ceo");
		ceo.addProperty(rdfType, rdfProperty);
		ceo.addProperty(rdfsDomain, foafOrganization);
		ceo.addProperty(rdfsRange, foafPerson);

		Property foundingYear = tboxModel.createProperty(OWN_URI + "foundingYear");
		foundingYear.addProperty(rdfType, rdfProperty);
		foundingYear.addProperty(rdfsDomain, foafOrganization);
		foundingYear.addProperty(rdfsRange, xsdInt);

		Property internetEnabled = tboxModel.createProperty(OWN_URI + "internetEnabled");
		internetEnabled.addProperty(rdfType, rdfProperty);
		internetEnabled.addProperty(rdfsDomain, gameConsole);
		internetEnabled.addProperty(rdfsRange, xsdBoolean);

		Property consoleName = tboxModel.createProperty(OWN_URI + "consoleName");
		consoleName.addProperty(rdfType, rdfProperty);
		consoleName.addProperty(rdfsDomain, gameConsole);
		consoleName.addProperty(rdfsRange, xsdString);

		Property numOfSupportedControllers = tboxModel.createProperty(OWN_URI + "numOfSupportedControllers");
		numOfSupportedControllers.addProperty(rdfType, rdfProperty);
		numOfSupportedControllers.addProperty(rdfsDomain, gameConsole);
		numOfSupportedControllers.addProperty(rdfsRange, xsdInt);

		Property predecessorOfConsole = tboxModel.createProperty(OWN_URI + "predecessorOfConsole");
		predecessorOfConsole.addProperty(rdfType, rdfProperty);
		predecessorOfConsole.addProperty(rdfsDomain, gameConsole);
		predecessorOfConsole.addProperty(rdfsRange, gameConsole);

		Property successorOfConsole = tboxModel.createProperty(OWN_URI + "successorOfConsole");
		successorOfConsole.addProperty(rdfType, rdfProperty);
		successorOfConsole.addProperty(rdfsDomain, gameConsole);
		successorOfConsole.addProperty(rdfsRange, gameConsole);

		Property relatedToConsole = tboxModel.createProperty(OWN_URI + "relatedToConsole");
		relatedToConsole.addProperty(rdfType, rdfProperty);
		relatedToConsole.addProperty(rdfsDomain, gameConsole);
		relatedToConsole.addProperty(rdfsRange, gameConsole);

		Property releaseYear = tboxModel.createProperty(OWN_URI + "releaseYear");
		releaseYear.addProperty(rdfType, rdfProperty);
		releaseYear.addProperty(rdfsDomain, gameConsole);
		releaseYear.addProperty(rdfsRange, xsdInt);

		Property madeBy = tboxModel.createProperty(OWN_URI + "madeBy");
		madeBy.addProperty(rdfType, rdfProperty);
		madeBy.addProperty(rdfsDomain, gameConsole);
		madeBy.addProperty(rdfsRange, foafOrganization);

		// Declare our datatype
		Resource priceEur = tboxModel.createResource(OWN_URI + "PriceEur");
		priceEur.addProperty(rdfType, rdfsDatatype);
		priceEur.addProperty(rdfsLabel, "Preis in Euro");

		Property hasPrice = tboxModel.createProperty(OWN_URI + "hasPrice");
		hasPrice.addProperty(rdfType, rdfProperty);
		hasPrice.addProperty(rdfsDomain, gameConsole);
		hasPrice.addProperty(rdfsRange, priceEur);

		// Use of subPropertyOf
		predecessorOfConsole.addProperty(rdfsSubPropertyOf, relatedToConsole);
		successorOfConsole.addProperty(rdfsSubPropertyOf, relatedToConsole);

		// Store the model into a file
		
		Generator.writeModelFile(tboxModel, outputPath, T_BOX_FILENAME_TTL, MODEL_TURTLE);
		Generator.writeModelFile(tboxModel, outputPath, T_BOX_FILENAME_JSON, MODEL_JSON);
		Generator.writeModelFile(tboxModel, outputPath, T_BOX_FILENAME_XML, MODEL_RDF_XML);

		/******************************************************************/

		// Now it's time to create the abox
		Model aboxModel = ModelFactory.createDefaultModel();

		// Prepare some more properties for use within the ABox
		Property foafFamilyName = aboxModel.createProperty(FOAF_URI + "familyName");
		Property foafGivenName = aboxModel.createProperty(FOAF_URI + "givenName");
		Property foafName = aboxModel.createProperty(FOAF_URI + "name");
		Property revHasReview = aboxModel.createProperty(REV_URI + "hasReview");
		Property revReviewer = aboxModel.createProperty(REV_URI + "reviewer");
		Property revText = aboxModel.createProperty(REV_URI + "text");

		Resource revReview = aboxModel.createResource(REV_URI + "Review");

		// Let's add stuff into the ABox
		Resource kimishima = aboxModel.createResource(OWN_URI + "Kimishima");
		kimishima.addProperty(rdfType, foafPerson);
		kimishima.addProperty(foafFamilyName, "Kimishima");
		kimishima.addProperty(foafGivenName, "Tatsumi");

		Resource nintendo = aboxModel.createResource(OWN_URI + "Nintendo");
		nintendo.addProperty(rdfType, foafOrganization);
		nintendo.addProperty(foafName, "Nintendo Co., Ltd.");
		nintendo.addProperty(foundingYear, aboxModel.createTypedLiteral(1889));
		nintendo.addProperty(ceo, kimishima);

		Resource wii = aboxModel.createResource(OWN_URI + "Wii");
		wii.addProperty(rdfType, gameConsole);
		wii.addProperty(madeBy, nintendo);

		Resource wiiU = aboxModel.createResource(OWN_URI + "Wii_u");
		wiiU.addProperty(rdfType, gameConsole);
		wiiU.addProperty(internetEnabled, aboxModel.createTypedLiteral(true));
		wiiU.addProperty(consoleName, "Wii U");
		wiiU.addProperty(numOfSupportedControllers, aboxModel.createTypedLiteral(8));
		wiiU.addProperty(predecessorOfConsole, wii);
		wiiU.addProperty(releaseYear, aboxModel.createTypedLiteral(2012));
		wiiU.addProperty(madeBy, nintendo);

		Resource cSwitch = aboxModel.createResource(OWN_URI + "Switch");
		wiiU.addProperty(successorOfConsole, cSwitch);
		cSwitch.addProperty(rdfType, portableGameConsole);
		cSwitch.addProperty(releaseYear, aboxModel.createTypedLiteral(2017));
		cSwitch.addProperty(madeBy, nintendo);

		Resource jpt = aboxModel.createResource(OWN_URI + "JPT");
		jpt.addProperty(rdfType, foafPerson);
		jpt.addProperty(foafName, "Jan Philipp Timme");

		Resource maschell = aboxModel.createResource(OWN_URI + "Maschell");
		maschell.addProperty(rdfType, foafPerson);
		maschell.addProperty(foafName, "Marcel Felix");

		Resource switchReviewByJPT = aboxModel.createResource(OWN_URI + "SwitchReviewByJPT");
		switchReviewByJPT.addProperty(rdfType, revReview);
		switchReviewByJPT.addProperty(revReviewer, jpt);
		switchReviewByJPT.addProperty(revText, "Yet another gaming console. I lost a tetris game once. Meh.");

		cSwitch.addProperty(revHasReview, switchReviewByJPT);

		cSwitch.addProperty(hasPrice, "329,00€");

		// Store the model into a file
		
		Generator.writeModelFile(aboxModel, outputPath, A_BOX_FILENAME_TTL, MODEL_TURTLE);
		Generator.writeModelFile(aboxModel, outputPath, A_BOX_FILENAME_JSON, MODEL_JSON);
		Generator.writeModelFile(aboxModel, outputPath, A_BOX_FILENAME_XML, MODEL_RDF_XML);
	}

	private static void writeModelFile(Model model, String path, String filename, String format) {
		FileOutputStream outfile = null;
		try {
			outfile = new FileOutputStream(path + "/" + filename);
			model.write(outfile, format);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
