package hsh.ins_jena;

import hsh.ins_jena.model.Generator;

public class App {
	public static void main(String[] args) {
		System.out.print("Generating ... ");
		Generator.createTBoxAndABox();
		System.out.println("done.");
	}
}
