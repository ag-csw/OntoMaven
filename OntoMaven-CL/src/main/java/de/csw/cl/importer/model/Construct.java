package de.csw.cl.importer.model;

import java.util.List;

/**
 * Represents a construct in common logic.
 */
public class Construct {
	private List<Titling> titlings;
	private List<Restrict> restricts;
	private List<Import> imports;

	/**
	 * Standard constructor.
	 */
	public Construct(List<Titling> titlings, List<Restrict> restricts,
			List<Import> imports) {
		this.titlings = titlings;
		this.restricts = restricts;
		this.imports = imports;
	}
	
	/**
	 * Returns the titlings which were children of this.
	 */
	public List<Titling> getTitlings() {
		return titlings;
	}

	/**
	 * Returns the restricts which were children of this.
	 */
	public List<Restrict> getRestricts() {
		return restricts;
	}

	/**
	 * Returns imports which were children of this.
	 */
	public List<Import> getImports() {
		return imports;
	}

	/**
	 * Returns a string representation of this.
	 */
	public String toString() {
		String asString = "[Construct" + System.lineSeparator();
		for (Titling titling: titlings)
			asString += titling.toString() + System.lineSeparator();
		for (Restrict restrict: restricts)
			asString += restrict.toString() + System.lineSeparator();
		for (Import importDeclaration: imports)
			asString += importDeclaration.toString() + System.lineSeparator();
		asString += "]";
		return asString;
	}
	
	/**
	 * Checks if this or 
	 */
	public boolean hasAnyImports(){
		if (imports.size() > 0)
			return true;
		
		for (Titling titling: titlings){
			if (titling.hasAnyImports())
				return true;
		}
		
		for (Restrict restrict: restricts){
			if (restrict.hasAnyImports())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Solve own imports and imports of inner elements.
	 */
	public void solveImports(){
		for (Titling titling : titlings)
			titling.solveImports();

		for (Restrict restrict: restricts)
			restrict.solveImports();

		for (Import importDeclaration : imports)
			importDeclaration.solve();
	}
}