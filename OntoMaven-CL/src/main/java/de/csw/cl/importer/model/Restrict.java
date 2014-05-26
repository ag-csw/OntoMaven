package de.csw.cl.importer.model;

import java.util.List;

/**
 * Represents a imports restriction in common logic.
 */
public class Restrict {
	private String name;
	private List<Import> imports;

	/**
	 * Standard constructor.
	 */
	public Restrict(String name, List<Import> imports) {
		this.name = name;
		this.imports = imports;
	}

	/**
	 * Returns the name / URI of the restriction.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the restricted imports.
	 */
	public List<Import> getImports() {
		return imports;
	}

	/**
	 * Returns a string representation of this this.
	 */
	public String toString() {
		String asString = "[Restrict" + System.lineSeparator();
		asString += "name=\"" + name + "\"";
		for (Import importStatement : imports)
			asString += importStatement.toString() + System.lineSeparator();
		asString += "]";
		return asString;
	}

	/**
	 * Solves imports.
	 */
	public void solveImports() {
		for (Import importDecl: imports) {
			importDecl.solve();
			imports.remove(importDecl);
		}
	}
	
	/**
	 * Returns if it has any imports.
	 */
	public boolean hasAnyImports(){
		return imports.size() > 0;
	}
}
