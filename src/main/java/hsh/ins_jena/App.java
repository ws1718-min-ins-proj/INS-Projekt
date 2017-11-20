package hsh.ins_jena;

import hsh.ins_jena.model.Generator;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.print("Generating ... ");
		Generator.createTBoxAndABox();
		System.out.println("done.");
	}
}
