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
	
	public static void createTBoxAndABox() {
		Model tboxModel = ModelFactory.createDefaultModel();

		// Some boilerplate stuff
		Property rdfType = tboxModel.createProperty(rdfUri+"type");
		Resource rdfClass = tboxModel.createResource(rdfUri+"Class");
		Resource rdfProperty = tboxModel.createResource(rdfUri+"Property");
		
		Property rdfsSubclassOf = tboxModel.createProperty(rdfsUri+"subclassOf");
		Property rdfsSubPropertyOf = tboxModel.createProperty(rdfsUri+"subPropertyOf");
		Property rdfsDomain = tboxModel.createProperty(rdfsUri+"domain");
		Property rdfsRange = tboxModel.createProperty(rdfsUri+"range");
		Property rdfsLabel = tboxModel.createProperty(rdfsUri+"label");
		Resource rdfsDatatype = tboxModel.createResource(rdfsUri+"Datatype");

		
		Resource foafPerson = tboxModel.createResource(foafUri+"Person");
		Resource foafOrganization = tboxModel.createResource(foafUri+"Organization");

		Resource xsdInt = tboxModel.createResource(xsdUri+"int");
		Resource xsdBoolean = tboxModel.createResource(xsdUri+"boolean");
		Resource xsdString = tboxModel.createResource(xsdUri+"string");

		
		// Own classes
		Resource gameConsole = tboxModel.createResource(ownUri+"GameConsole");
		Resource portableGameConsole = tboxModel.createResource(ownUri+"PortableGameConsole");

		// Add statements to the model
		tboxModel.add(tboxModel.createStatement(gameConsole, rdfType, rdfClass));
		tboxModel.add(tboxModel.createStatement(portableGameConsole, rdfType, rdfClass));
		tboxModel.add(tboxModel.createStatement(portableGameConsole, rdfsSubclassOf, gameConsole));

		// Add properties to the model
		Property ceo = tboxModel.createProperty(ownUri+"ceo");
		ceo.addProperty(rdfType, rdfProperty);
		ceo.addProperty(rdfsDomain, foafOrganization);
		ceo.addProperty(rdfsRange, foafPerson);
		
		Property foundingYear = tboxModel.createProperty(ownUri+"foundingYear");
		foundingYear.addProperty(rdfType, rdfProperty);
		foundingYear.addProperty(rdfsDomain, foafOrganization);
		foundingYear.addProperty(rdfsRange, xsdInt);

		Property internetEnabled = tboxModel.createProperty(ownUri+"internetEnabled");
		internetEnabled.addProperty(rdfType, rdfProperty);
		internetEnabled.addProperty(rdfsDomain, gameConsole);
		internetEnabled.addProperty(rdfsRange, xsdBoolean);
		
		Property consoleName = tboxModel.createProperty(ownUri+"consoleName");
		consoleName.addProperty(rdfType, rdfProperty);
		consoleName.addProperty(rdfsDomain, gameConsole);
		consoleName.addProperty(rdfsRange, xsdString);
		
		Property numOfSupportedControllers = tboxModel.createProperty(ownUri+"numOfSupportedControllers");
		numOfSupportedControllers.addProperty(rdfType, rdfProperty);
		numOfSupportedControllers.addProperty(rdfsDomain, gameConsole);
		numOfSupportedControllers.addProperty(rdfsRange, xsdInt);
		
		Property predecessorOfConsole = tboxModel.createProperty(ownUri+"predecessorOfConsole");
		predecessorOfConsole.addProperty(rdfType, rdfProperty);
		predecessorOfConsole.addProperty(rdfsDomain, gameConsole);
		predecessorOfConsole.addProperty(rdfsRange, gameConsole);
		
		Property successorOfConsole = tboxModel.createProperty(ownUri+"successorOfConsole");
		successorOfConsole.addProperty(rdfType, rdfProperty);
		successorOfConsole.addProperty(rdfsDomain, gameConsole);
		successorOfConsole.addProperty(rdfsRange, gameConsole);

		Property relatedToConsole = tboxModel.createProperty(ownUri+"relatedToConsole");
		relatedToConsole.addProperty(rdfType, rdfProperty);
		relatedToConsole.addProperty(rdfsDomain, gameConsole);
		relatedToConsole.addProperty(rdfsRange, gameConsole);

		Property releaseYear = tboxModel.createProperty(ownUri+"releaseYear");
		releaseYear.addProperty(rdfType, rdfProperty);
		releaseYear.addProperty(rdfsDomain, gameConsole);
		releaseYear.addProperty(rdfsRange, xsdInt);

		Property madeBy = tboxModel.createProperty(ownUri+"madeBy");
		madeBy.addProperty(rdfType, rdfProperty);
		madeBy.addProperty(rdfsDomain, gameConsole);
		madeBy.addProperty(rdfsRange, foafOrganization);

		// Declare our datatype
		Resource priceEur = tboxModel.createResource(ownUri+"PriceEur");
		priceEur.addProperty(rdfType, rdfsDatatype);
		priceEur.addProperty(rdfsLabel, "Preis in Euro");
		
		Property hasPrice = tboxModel.createProperty(ownUri+"hasPrice");
		hasPrice.addProperty(rdfType, rdfProperty);
		hasPrice.addProperty(rdfsDomain, gameConsole);
		hasPrice.addProperty(rdfsRange, priceEur);

		// Use of subPropertyOf
		predecessorOfConsole.addProperty(rdfsSubPropertyOf, relatedToConsole);
		successorOfConsole.addProperty(rdfsSubPropertyOf, relatedToConsole);

		// Store the model into a file
		Generator.writeModelToTurtleFile(tboxModel, "./output/tbox.ttl");
		Generator.writeModelToJsonFile(tboxModel, "./output/tbox.json");

		/******************************************************************/
				
		// Now it's time to create the abox
		Model aboxModel = ModelFactory.createDefaultModel();

		// Prepare some more properties for use within the ABox
		Property foafFamilyName = aboxModel.createProperty(foafUri+"familyName");
		Property foafGivenName = aboxModel.createProperty(foafUri+"givenName");
		Property foafName = aboxModel.createProperty(foafUri+"name");
		Property revHasReview = aboxModel.createProperty(revUri+"hasReview");
		Property revReviewer = aboxModel.createProperty(revUri+"reviewer");
		Property revText = aboxModel.createProperty(revUri+"text");
		
		Resource revReview = aboxModel.createResource(revUri+"Review");

		// Let's add stuff into the ABox
		Resource kimishima = aboxModel.createResource(ownUri+"Kimishima");
		kimishima.addProperty(rdfType, foafPerson);
		kimishima.addProperty(foafFamilyName, "Kimishima");
		kimishima.addProperty(foafGivenName, "Tatsumi");

		Resource nintendo = aboxModel.createResource(ownUri+"Nintendo");
		nintendo.addProperty(rdfType, foafOrganization);
		nintendo.addProperty(foafName, "Nintendo Co., Ltd.");
		nintendo.addProperty(foundingYear, aboxModel.createTypedLiteral(1889));
		nintendo.addProperty(ceo, kimishima);
		
		Resource wii = aboxModel.createResource(ownUri+"Wii");
		wii.addProperty(rdfType, gameConsole);
		wii.addProperty(madeBy, nintendo);
		
		Resource wiiU = aboxModel.createResource(ownUri+"Wii_u");
		wiiU.addProperty(rdfType, gameConsole);
		wiiU.addProperty(internetEnabled, aboxModel.createTypedLiteral(true));
		wiiU.addProperty(consoleName, "Wii U");
		wiiU.addProperty(numOfSupportedControllers, aboxModel.createTypedLiteral(8));
		wiiU.addProperty(predecessorOfConsole, wii);
		wiiU.addProperty(releaseYear, aboxModel.createTypedLiteral(2012));
		wiiU.addProperty(madeBy, nintendo);

		Resource cSwitch = aboxModel.createResource(ownUri+"Switch");
		wiiU.addProperty(successorOfConsole, cSwitch);
		cSwitch.addProperty(rdfType, portableGameConsole);
		cSwitch.addProperty(madeBy, nintendo);
		
		Resource jpt = aboxModel.createResource(ownUri+"JPT");
		jpt.addProperty(rdfType, foafPerson);
		jpt.addProperty(foafName, "Jan Philipp Timme");

		Resource maschell = aboxModel.createResource(ownUri+"Maschell");
		maschell.addProperty(rdfType, foafPerson);
		maschell.addProperty(foafName, "Marcel Felix");
		
		Resource switchReviewByJPT = aboxModel.createResource(ownUri+"SwitchReviewByJPT");
		switchReviewByJPT.addProperty(rdfType, revReview);
		switchReviewByJPT.addProperty(revReviewer, jpt);
		switchReviewByJPT.addProperty(revText, "Yet another gaming console. I lost a tetris game once. Meh.");
		
		cSwitch.addProperty(revHasReview, switchReviewByJPT);

		cSwitch.addProperty(hasPrice, "329,00€");
		
		// Store the model into a file
		Generator.writeModelToTurtleFile(aboxModel, "./output/abox.ttl");
		Generator.writeModelToJsonFile(aboxModel, "./output/abox.json");
	}
		
	private static void writeModelToTurtleFile(Model model, String path) {
		FileOutputStream outfile = null;
		try {
			outfile = new FileOutputStream(path);
			model.write(outfile, "TURTLE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void writeModelToJsonFile(Model model, String path) {
		FileOutputStream outfile = null;
		try {
			outfile = new FileOutputStream(path);
			model.write(outfile, "JSON-LD");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
