package de.tudresden.gis.fusion.operation.similarity.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.FeatureRelation;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class DamerauLevenshteinDistance extends AbstractMeasurementOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_REFERENCE_ATT = "IN_REFERENCE_ATT";
	private final String IN_TARGET_ATT = "IN_TARGET_ATT";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#DamerauLevenshteinDistance";
	private final String RELATION_STRING_DAMLEV = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/string#damerauLevenshtein";
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		StringLiteral inReferenceAtt = (StringLiteral) getInput(IN_REFERENCE_ATT);
		StringLiteral inTargetAtt = (StringLiteral) getInput(IN_TARGET_ATT);
		IntegerLiteral inThreshold = (IntegerLiteral) getInput(IN_THRESHOLD);
		
		//set defaults
		int iThreshold = inThreshold == null ? ((IntegerLiteral) this.getInputDescription(new IRI(IN_THRESHOLD)).getDefault()).getValue() : inThreshold.getValue();
		
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS), inReferenceAtt, inTargetAtt, iThreshold) :
				calculateRelation(inReference, inTarget, inReferenceAtt, inTargetAtt, iThreshold));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, StringLiteral referenceAtt, StringLiteral targetAtt, int iThreshold) {

		IFeatureRelationCollection relations = new GTFeatureRelationCollection();
	    for(IFeature fRef : reference) {
		    for(IFeature fTar : target) {
		    	SimilarityMeasurement similarity = calculateSimilarity(fRef, fTar, referenceAtt, targetAtt, iThreshold);
	    		if(similarity != null)
	    			relations.addRelation(new FeatureRelation(fRef, fTar, similarity, null));
		    }
	    }
	    return relations;
	    
	}
		
	private IFeatureRelationCollection calculateRelation(IFeatureCollection reference, IFeatureCollection target, IFeatureRelationCollection relations, StringLiteral referenceAtt, StringLiteral targetAtt, int iThreshold){
		
		//init relations
		for(IFeatureRelation relation : relations){
			//get features
			IFeature fReference = reference.getFeatureById(relation.getReference().getIdentifier());
			IFeature fTarget = target.getFeatureById(relation.getTarget().getIdentifier());
			if(reference == null || target == null)
				continue;
			SimilarityMeasurement similarity = calculateSimilarity(fReference, fTarget, referenceAtt, targetAtt, iThreshold);
    		if(similarity != null)
    			relation.addRelationMeasurement(similarity);
	    }
		return relations;
	    
	}
	
	private SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target, StringLiteral referenceAtt, StringLiteral targetAtt, int iThreshold) {
		//get attributes
		String sReference = getAttributeValue(reference, referenceAtt.getIdentifier());
		String sTarget = getAttributeValue(target, targetAtt.getIdentifier());
		if(sReference == null || sReference.isEmpty() || sTarget == null || sTarget.isEmpty())
			return null;
		//get distance
		int distance = getDLDistance(sReference, sTarget);
		//add similarity measurement, if distance <= threshold 
		if(distance <= iThreshold){
			return new SimilarityMeasurement( 
					new IntegerLiteral(distance), 
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_STRING_DAMLEV)))
			);
		}
		else return null;
	}
	
	private String getAttributeValue(IFeature feature, String name){
		for(IThematicProperty property : feature.getThematicProperties()){
			if(property.getIdentifier().equals(name))
				return property.getValue().toString();
		}
		return null;
	}
	
	/**
	 * calculates Damerau Levenshtein Distance between 2 Strings
	 * @param sReference reference String
	 * @param sTarget target String
	 * @return Damerau Levenshtein Distance
	 */
	private int getDLDistance(String sReference, String sTarget) {
		//get length of Strings
		int str1Len = sReference.length();
		int str2Len = sTarget.length();
		//return 0 if one or both strings empty
		if(str1Len == 0 || str2Len == 0) return 0;
		//create matrix
		int[][] matrix = new int[str1Len+1][str2Len+1];
		//tmp variable cost
		int cost;
		//tmp variable Array for minimum value
		int[] minArray = new int[3];
		//set first row,col of matrix
		for(int i=0;i<=str1Len;i++) {
			matrix[i][0] = i;
		}
		for(int j=0;j<=str2Len;j++){
			matrix[0][j] = j;
		}
		//loop through matrix
		for(int i=1;i<=str1Len;i++) {
			for(int j=1;j<=str2Len;j++){
				//set cost = 1 if chars are not equal
				if(sReference.charAt(i-1) == sTarget.charAt(j-1)) cost = 0;
				else cost = 1;
				minArray[0] = matrix[i-1][j] + 1; //deletion
				minArray[1] = matrix[i][j-1] + 1; //insertion
				minArray[2] = matrix[i-1][j-1] + cost; //substitution
				Arrays.sort(minArray);
				matrix[i][j] = minArray[0];
				//calculate transportation (after Damerau)
				if(i>1 && j>1 && sReference.charAt(i-1) == sTarget.charAt(j-2) && sReference.charAt(i-2) == sTarget.charAt(j-1)) {
					matrix[i][j] = Math.min(matrix[i][j], matrix[i-2][j-2] + cost); //transportation
				}
			}
		}
		//return Damerau-Levenshtein-Distance
		return(matrix[str1Len][str2Len]);
	}

	@Override
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Calculates Damerau Levenshtein Distance between feature attributes";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
					})
		);
		inputs.add(new IODescription(
				new IRI(IN_REFERENCE_ATT), "Reference attribute name",
				new IDataRestriction[]{
					ERestrictions.BINDING_STRING.getRestriction()
				})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET_ATT), "Target attribute name",
					new IDataRestriction[]{
						ERestrictions.BINDING_STRING.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_THRESHOLD), "String distance threshold for relations",
					new IntegerLiteral(5),
					new IDataRestriction[]{
						ERestrictions.BINDING_INTEGER.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_RELATIONS), "Input relations; if set, similarity measures are added to the relations (reference and target inputs are ignored)",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
					})
		);
		return inputs;
	}
	
	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		Collection<IIODescription> outputs = new ArrayList<IIODescription>();
		outputs.add(new IODescription(
					new IRI(OUT_RELATIONS), "Output relations",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
					})
		);
		return outputs;
	}
	
	@Override
	protected Collection<IMeasurementDescription> getSupportedMeasurements() {
		Collection<IMeasurementDescription> measurements = new ArrayList<IMeasurementDescription>();		
		measurements.add(new MeasurementDescription(
					this.getProcessIRI(),
					"Damerau Levenshtein Distance between feature attributes", 
					new RelationType(new IRI(RELATION_STRING_DAMLEV)),
					new MeasurementRange<Integer>(
							new IntegerLiteral[]{new IntegerLiteral(0), new IntegerLiteral(Integer.MAX_VALUE)}, 
							true
					))
		);
		return measurements;
	}
	
}
