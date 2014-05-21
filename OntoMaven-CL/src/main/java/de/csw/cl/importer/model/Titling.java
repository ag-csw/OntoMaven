package de.csw.cl.importer.model;

import java.util.List;

/**
 * Represents a titling in common logic. But contains only parts
 * which are needed for importing.
 */
public class Titling {
	private List<Construct> constructs;
	private List<Import> imports;
	private List<Restrict> restricts;
	private String name;

	/**
	 * Standard constructor.
	 */
	public Titling(List<Construct> constructs, String name,
			List<Import> imports, List<Restrict> restricts) {
		this.constructs = constructs;
		this.name = name;
		this.imports = imports;
		this.restricts = restricts;
	}

	/**
	 * Returns the constructs.
	 */
	public List<Construct> getConstructs() {
		return constructs;
	}

	/**
	 * Returns the name / URI of this titling.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a string representation of this class.
	 */
	public String toString() {
		String asString = "[Titling" + System.lineSeparator();
		asString += "name=\"" + name + "\"" + System.lineSeparator();
		for (Construct construct : constructs)
			asString += construct.toString() + System.lineSeparator();
		asString += "]";
		return asString;
	}

	/**
	 * Returns the imports.
	 */
	public List<Import> getImports() {
		return imports;
	}
	
	/**
	 * Returns, if this has imports by checking
	 *  1. own imports
	 *  2. imports of other inner elements.
	 * If there are own ore other imports -> true
	 */
	public boolean hasAnyImports(){
		if (imports.size() > 0)
			return true;
		
		for (Construct construct: constructs){
			if (construct.hasAnyImports())
				return true;
		}
		
		return false;
	}

	/**
	 * Solves the own imports and the imports of the inner elements.
	 */
	public void solveImports() {
		for (Import importDeclaration : imports)
			importDeclaration.solve();
		
		for (Construct construct : constructs)
			construct.solveImports();
	}

	/**
	 * Returns the list containing restricts.
	 */
	public List<Restrict> getRestricts() {
		return restricts;
	}
}
