package de.tudresden.gis.fusion.operation.measurement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.opengis.feature.Property;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.IntegerLiteral;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class DamerauLevenshteinDistance extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_SOURCE_ATT = "IN_SOURCE_ATT";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_TARGET_ATT = "IN_TARGET_ATT";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private String referenceAtt, targetAtt;
	private int iThreshold;
	private boolean bDropRelations;
	
	private IIdentifiableResource referenceProperty, targetProperty;
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_STRING_DAMLEV.asString(),
			"Damerau Levenshtein distance",
			"Damerau Levenshtein distance between two Strings",
			IntegerLiteral.positiveRange(),
			RDFVocabulary.UOM_UNDEFINED.asResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) input(IN_SOURCE);
		referenceAtt = ((StringLiteral) input(IN_SOURCE_ATT)).resolve();
		GTFeatureCollection inTarget = (GTFeatureCollection) input(IN_TARGET);
		targetAtt = ((StringLiteral) input(IN_TARGET_ATT)).resolve();
		iThreshold = ((IntegerLiteral) input(IN_THRESHOLD)).resolve();
		bDropRelations = inputContainsKey(IN_DROP_RELATIONS) ? ((BooleanLiteral) input(IN_DROP_RELATIONS)).resolve() : false;
		
		//set properties
		referenceProperty = new Resource(RDFVocabulary.PROPERTY_THEM.asString() + "#" + referenceAtt);
		targetProperty = new Resource(RDFVocabulary.PROPERTY_THEM.asString() + "#" + targetAtt);
		
		//execute
		IDataCollection<IFeatureRelation> relations = 
				inputContainsKey(IN_RELATIONS) ?
						relations(inSource, inTarget, (FeatureRelationCollection) input(IN_RELATIONS), bDropRelations) :
						relations(inSource, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	@Override
	protected IRelationMeasurement getMeasurement(IFeature reference, IFeature target){
		//get attributes
		String sReference = getAttributeValue((GTFeature) reference, referenceAtt);
		String sTarget = getAttributeValue((GTFeature) target, targetAtt);
		if(sReference == null || sReference.isEmpty() || sTarget == null || sTarget.isEmpty())
			return null;
		//get distance
		int iDistance = getDistance(sReference, sTarget);
		//check for overlap		
		if(iDistance <= iThreshold) {
			return new RelationMeasurement(
					referenceProperty,
					targetProperty,
					new IntegerLiteral(iDistance), 
					distanceDescription);
		}
		else return null;
	}
	
	/**
	 * get feature property
	 * @param feature input feature
	 * @param name property name
	 * @return feature property as String
	 */
	private String getAttributeValue(GTFeature feature, String name) throws ProcessException {
		Property property = feature.resolve().getProperty(name);
		if(property == null)
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Feature has no attribute " + name);
		return property.getValue().toString();
	}
	
	/**
	 * calculates Damerau Levenshtein Distance between 2 Strings
	 * @param sReference reference String
	 * @param sTarget target String
	 * @return Damerau Levenshtein Distance
	 */
	private int getDistance(String sReference, String sTarget) {
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
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Damerau Levenshtein String distance calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature attribute relation based on Damerau Levenshtein String distance for specified attribute";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> getInputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}