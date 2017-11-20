package hsh.examplecode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Hello world!
 *
 */
public class SampleCode {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		String familyUri = "http://family/";
		String relationshipUri = "http://purl.org/vocab/relationship/";
		
		Model model = ModelFactory.createDefaultModel();
		Resource donald = model.createResource(familyUri + "donald");
		Resource tick = model.createResource(familyUri + "tick");

		Property childOf = model.createProperty(relationshipUri, "childOf");
		Property knows = model.createProperty(relationshipUri, "knows");
		tick.addProperty(childOf, donald);
		
		Statement st1 = model.createStatement(donald, knows, tick);
		model.add(st1);
		FileOutputStream outfile;
		try {
			outfile = new FileOutputStream("./test.ttl");
			model.write(outfile, "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
