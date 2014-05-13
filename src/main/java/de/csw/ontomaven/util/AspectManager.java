package de.csw.ontomaven.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityRemover;

/**
 *
 */
public class AspectManager {

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private String[] userAspects;
	private String aspectsIRI;
	private List<OWLAnnotationAssertionAxiom> annotationAssertions;
	private List<OWLAnnotation> annotations;
	private List<String> aspects;
	private int originalAxiomsCount;
	private int originalEntitiesCount;
	

	/**
	 * Standard constructor for {@link AspectManager}
	 * 
	 * @param ontologyManager which created the ontology
	 * @param ontology to work with
	 * @param userAspects list of aspects the user defined
	 */
	public AspectManager(OWLOntologyManager ontologyManager, String aspectsIRI,
			OWLOntology ontology, String[] userAspects) {
		this.manager = ontologyManager;
		this.ontology = ontology;
		this.userAspects = userAspects;
		this.aspectsIRI = aspectsIRI;
				
		// Defining and filling annotation assertions list
		annotationAssertions = new LinkedList<OWLAnnotationAssertionAxiom>();
		findAllAnnotationAssertions();
		
		// Defining and filling annotations list. First assertions then annotations
		// because assertions can also have annotations but annotations cannot
		// have assertions. We also will search in found assertions for annotations
		annotations = new LinkedList<OWLAnnotation>();
		findAllAnnotations();
		
		// Defining and filling aspects list. The method searchs in assertions and
		// annotations for aspect names and collects them.
		aspects = new LinkedList<String>();
		findAllAspectNames();
		
		originalAxiomsCount = ontology.getAxiomCount();
		originalEntitiesCount = ontology.getSignature().size();
	}

	/**
	 * Lets applying all axiom aspects and entity aspects on the ontology
	 * by removing the axioms and entities which are not effective because
	 * of an unvalid aspect.
	 */
	public void applyAllAspects(){
		for(OWLAxiom axiom: ontology.getAxioms()){
			if (!isEffectiveAxiom(axiom))
				manager.removeAxiom(ontology, axiom);
		}
		
		for (OWLEntity entity: ontology.getSignature()){
			
			// Creating entityRemover, which removes an entity and its users
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(ontology);
			OWLEntityRemover entityRemover = new OWLEntityRemover(manager,
					ontologies);
			
			if(!isEffectiveEntity(entity))
				entity.accept(entityRemover);

			// Applying changes. There will be probably more changes as the
			// removing uneffective entities, because the remover removes also
			// also axioms and other entities which need this entity
			manager.applyChanges(entityRemover.getChanges());
		}
	}

	private boolean isEffectiveEntity(OWLEntity entity) {
		boolean isEffective = true;
		for (OWLAnnotationAssertionAxiom assertion:
			entity.getAnnotationAssertionAxioms(ontology)){
			if (isAspectAssertion(assertion)){
				if(!isEffectiveAspectAssertion(assertion))
					return true; // OR: one assertion says yes
				 else
					isEffective = false;
			}
		}
		return isEffective;
	}

	private boolean isEffectiveAspectAssertion(
			OWLAnnotationAssertionAxiom assertion) {
		boolean isEffective = false; // Means: having axiom is effective
		boolean isEffectiveConceringInnerAnnotations = true;
		
		// First: Check if it is is effective, because it is not an user aspect.
		// If no: return true, you don't have to check inner annotations to
		// realize an AND. If yes, maybe inner aspects are not effective.
		if (!isUserAspect(getAspectNameOfAssertion(assertion))){
			isEffective = true;
		} else {
			return false;
		}
		
		// Second: Check if inner annotations are effective. If yes, the parent
		// assertion is uneffective. Because the parent assertion and inner
		// annotation build an AND. The parent annotation is effective, if
		// inner annotations does not restrict it.
		for (OWLAnnotation innerAnnotation: assertion.getAnnotations()){
			if (isAspectAnnotation(innerAnnotation)){
				if (isEffektiveAspectAnnotation(innerAnnotation)){
					isEffectiveConceringInnerAnnotations = true;
				} else {
					isEffectiveConceringInnerAnnotations = false;
					break; // Realizes an OR. If one inner annotations says, that
						   // alows the parent to be effective, its enough.
				}
			}
		}
		
		// If it is effective and no inner annotation hinder it, return true
		if (isEffective && isEffectiveConceringInnerAnnotations)
			return true;
		return false;
	}

	/**
	 * Checks for a given axiom, if it is effective in spite of the
	 * annotations, which could be aspect annotations and the aspects could
	 * be uneffective. The axiom is not effective, when at least one
	 * annotation makes it uneffective and no annotations make it effective.
	 * 
	 * @param axiom for that to check
	 * @return if axiom effective
	 */
	private boolean isEffectiveAxiom(OWLAxiom axiom) {
		boolean isEffektive = true;
		for (OWLAnnotation annotation : axiom.getAnnotations()) {
			if (isAspectAnnotation(annotation)) {
				if (!isEffektiveAspectAnnotation(annotation))
					return true; // OR: if one aspect says yes, then yes
				else
					isEffektive = false;
			}
		}
		return isEffektive;
	}

	/**
	 * Checks for a given annotation, if it is an aspect annotation and
	 * makes an axiom uneffective, because the aspect is not an user aspect
	 * 
	 * @param annotation for that the effectivity will be checked
	 * @return if annotation is an effective aspect annotation
	 */
	private boolean isEffektiveAspectAnnotation(OWLAnnotation annotation) {
		
		boolean isEffective = false; // Means: having axiom is effective
		boolean isEffectiveConceringInnerAnnotations = true;
		
		// First: Check if it is an is effective, because it is not an user
		// aspect. If no: return true, you don't have to check inner
		// annotations to realize an AND. If yes, maybe inner aspects are
		// not effective.
		if (!isUserAspect(getAspectNameOfAnnotation(annotation))){
			isEffective = true;
		} else {
			return false;
		}
		
		// Second: Check if inner annotations are effective. If yes, the parent
		// annotation is uneffective. Because the parent and inner annotation
		// build an AND. The parent annotation is effective, if inner annotations
		// does not restrict it.
		for (OWLAnnotation innerAnnotation: annotation.getAnnotations()){
			if (isAspectAnnotation(innerAnnotation)){
				if (isEffektiveAspectAnnotation(innerAnnotation)){
					isEffectiveConceringInnerAnnotations = true;
				} else {
					isEffectiveConceringInnerAnnotations = false;
					break; // Realizes an OR. If one inner annotations says, that
						   // alows the parent to be effective, its enough.
				}
			}
		}
		
		// If it is effective and no inner annotation hinder it, return true
		if (isEffective && isEffectiveConceringInnerAnnotations)
			return true;
		return false;
	}

	/**
	 * Returns all names of in the ontology contained aspects.
	 * @return names of aspects
	 */
	public List<String> getAllAspectNames(){
		return aspects;
	}
	
	/**
	 * Search recursiv in all axioms and annotationassertionaxioms for
	 * annotations and add them to variable annotations
	 */
	private void findAllAnnotations(){
		for (OWLAxiom owlAxiom : ontology.getAxioms()) {
			for (OWLAnnotation annotation : owlAxiom.getAnnotations())
				annotations.addAll(getRecursivAllAnnotations(annotation));
		}
		
		for (OWLAnnotationAssertionAxiom assertion: annotationAssertions){
			for (OWLAnnotation annotation : assertion.getAnnotations())
				annotations.addAll(getRecursivAllAnnotations(annotation));
		}
	}
	
	/**
	 * Search in all entities in the ontology for annotationsassertions
	 * and adds them to the variable annotationassertions
	 */
	private void findAllAnnotationAssertions(){
		for (OWLEntity entity: ontology.getSignature())
			annotationAssertions.addAll(entity
					.getAnnotationAssertionAxioms(ontology));
	}
	
	/**
	 * Finds in the found annotations and annotationassertions all names
	 * of aspects, if these are aspect annotations or assertions. The
	 * aspect names will be added to the variable aspects
	 * Because aspects can have inheritance, the method searchs against
	 * in all annotations and assertions, if at least one new aspect name
	 * is found. Because there could be another aspects which is a sub
	 * aspect of the found aspect. Every aspect name will be added only once.
	 */
	private void findAllAspectNames() {
		for (OWLAnnotation foundAnnotation : annotations) {
			if (isAspectAnnotation(foundAnnotation) && !aspects
							.contains(getAspectNameOfAnnotation(foundAnnotation))) {
				aspects.add(getAspectNameOfAnnotation(foundAnnotation));
			}
		}
		for (OWLAnnotationAssertionAxiom assertion : annotationAssertions) {
			if (isAspectAssertion(assertion)
					&& !aspects.contains(getAspectNameOfAssertion(assertion))) {
				aspects.add(getAspectNameOfAssertion(assertion));
			}
		}
	}

	private String getAspectNameOfAssertion( OWLAnnotationAssertionAxiom assertion) {
		return assertion.getValue().toString();
	}

	private boolean isAspectAssertion(OWLAnnotationAssertionAxiom assertion) {
		OWLAnnotationProperty property = assertion.getProperty();
		if (property != null) {
			IRI propertyIRI = assertion.getProperty().getIRI();
			if (propertyIRI != null) {
				if (propertyIRI.toString().equals(aspectsIRI))
					return true;
				for (String containedAspect: aspects){
					if (propertyIRI.toString().equals(containedAspect))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns for a annotation the list of the annotations of it and itself
	 * 
	 * @param parentAnnotation for that inner annotations will be returned
	 * @return list of contained annotations
	 */
	private List<OWLAnnotation> getRecursivAllAnnotations(
			OWLAnnotation parentAnnotation) {
		List<OWLAnnotation> foundAnnotations = new LinkedList<OWLAnnotation>();
		foundAnnotations.add(parentAnnotation);
		for (OWLAnnotation childAnnotation : parentAnnotation.getAnnotations()) {
			foundAnnotations.addAll(getRecursivAllAnnotations(childAnnotation));
		}
		return foundAnnotations;
	}

	/**
	 * Checks for an aspect, if it is contained in the list of user aspects.
	 * 
	 * @param aspect name
	 * 
	 * @return if aspect contained in user aspects
	 */
	private boolean isUserAspect(String aspect) {
		for (String userAspect : userAspects) {
			if (aspect.equals(userAspect)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for an {@link OWLAnnotation} if it has an
	 * {@link OWLAnnotationProperty} which have an {@link IRI} ending with
	 * "hasAspect"
	 * 
	 * @param annotation to be checked for
	 * 
	 * @return if annotation has an property ending with "hasAspect"
	 */
	private boolean isAspectAnnotation(OWLAnnotation annotation) {
		OWLAnnotationProperty property = annotation.getProperty();
		if (property != null) {
			IRI propertyIRI = annotation.getProperty().getIRI();
			if (propertyIRI != null) {
				if (propertyIRI.toString().equals(aspectsIRI))
					return true;
				for (String containedAspect: aspects){
					if (propertyIRI.toString().equals(containedAspect))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the name of an aspect of an {@link OWLAnnotation}. The name of
	 * the aspect is the value of the annotation. The method should be used
	 * if you know, that the annotation has an aspect property
	 * 
	 * @param annotation
	 * 
	 * @return name of an aspect (annotation value)
	 */
	private String getAspectNameOfAnnotation(OWLAnnotation annotation) {
		return annotation.getValue().toString().replace("\"", "");
	}
	
	/**
	 * Returns the number of the removed axioms by building the difference
	 * between the existing axioms and the original axioms.
	 * 
	 * @return number of removed axioms
	 */
	public int getRemovedAxiomsCount(){
		return originalAxiomsCount - ontology.getAxiomCount();
	}
	
	/**
	 * Returns the number of the removed entities by building the difference
	 * between the existing entities and the original entities.
	 * 
	 * @return number of deleted entities
	 */
	public int getRemovedEntitiesCount(){
		return originalEntitiesCount - ontology.getSignature().size();
	}
	
	public void removeAspects(){
		
		for(OWLAnnotationAssertionAxiom assertion: annotationAssertions){
			if (isAspectAssertion(assertion)){
				manager.removeAxiom(ontology, assertion);
			}
		}
		
		
		for (OWLAxiom axiom: ontology.getAxioms()){
			Set<OWLAnnotation> notAspectAnnotations =
					new HashSet<OWLAnnotation>();
			for (OWLAnnotation annotation: axiom.getAnnotations()){
				if (!isAspectAnnotation(annotation))
					notAspectAnnotations.add(annotation);
			}
			
			OWLAxiom newAxiom = axiom.getAxiomWithoutAnnotations()
					.getAnnotatedAxiom(notAspectAnnotations);
			
			manager.applyChange(new RemoveAxiom(ontology, axiom));
			manager.applyChange(new AddAxiom(ontology, newAxiom));
		}
	}

	public List<String> getAspectNamesOfAxiom(OWLAxiom axiom) {
		// First: finding all annotations
		List<OWLAnnotation> annotationsOfThisAxiom =
				new LinkedList<OWLAnnotation>();
		for (OWLAnnotation annotation : axiom.getAnnotations())
			annotations.addAll(getRecursivAllAnnotations(annotation));
			
		// Second: Filtering the aspect annotations
		List<String> foundAspects = new LinkedList<String>();
		for (OWLAnnotation annotation: annotationsOfThisAxiom){
			if (isAspectAnnotation(annotation))
				foundAspects.add(getAspectNameOfAnnotation(annotation));
		}
		
		return foundAspects;
	}
}