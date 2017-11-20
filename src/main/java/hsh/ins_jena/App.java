package hsh.ins_jena;

import hsh.ins_jena.model.Generator;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Generating TBox ...");
		Generator.createTBox();
	}
}
