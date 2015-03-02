package de.tudresden.gis.fusion.operation.relation.similarity;

import java.util.Arrays;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EMeasurementType;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.metadata.data.ISimilarityMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementRange;
import de.tudresden.gis.fusion.metadata.data.SimilarityMeasurementDescription;
import de.tudresden.gis.fusion.operation.ASimilarityMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class DamerauLevenshteinDistance extends ASimilarityMeasurementOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_REFERENCE_ATT = "IN_REFERENCE_ATT";
	private final String IN_TARGET_ATT = "IN_TARGET_ATT";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
			EProcessType.OP_REL_PROP_STRING.resource()
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle());
	private final String MEASUREMENT_DESC = "Distance between bounding boxes";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.STRING_DIST.resource()
	};
	
	private String referenceAtt;
	private String targetAtt;
	private int iThreshold;
	private boolean bDropRelations;
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		referenceAtt = ((StringLiteral) getInput(IN_REFERENCE_ATT)).getIdentifier();
		targetAtt = ((StringLiteral) getInput(IN_TARGET_ATT)).getIdentifier();
		iThreshold = ((IntegerLiteral) getInput(IN_THRESHOLD)).getValue();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).getValue();
		
		//execute
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				relate(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS)) :
				relate(inReference, inTarget));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	@Override
	protected boolean dropRelations() {
		return bDropRelations;
	}
	
	@Override
	protected SimilarityMeasurement relate(IFeature reference, IFeature target) {
		//get attributes
		String sReference = getAttributeValue(reference, referenceAtt);
		String sTarget = getAttributeValue(target, targetAtt);
		if(sReference == null || sReference.isEmpty() || sTarget == null || sTarget.isEmpty())
			return null;
		//get distance
		int iDistance = getDLDistance(sReference, sTarget);
		//add similarity measurement, if distance <= threshold 
		if(iDistance <= iThreshold){
			return new SimilarityMeasurement( 
				new IntegerLiteral(iDistance),
				this.PROCESS_RESOURCE,
				this.getMeasurementDescription(MEASUREMENT_ID)
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
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}
	
	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Calculates Damerau-Levenshtein Distance between feature attributes";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_REFERENCE, "Reference features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_LINE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_TARGET, "Target features",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
					ERestrictions.GEOMETRY_LINE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_REFERENCE_ATT, "Reference attribute name",
				new IIORestriction[]{
					ERestrictions.BINDING_STRING.getRestriction()
				}
			),
			new IODescription(
				IN_TARGET_ATT, "Target attribute name",
				new IIORestriction[]{
					ERestrictions.BINDING_STRING.getRestriction()
				}
			),
			new IODescription(
				IN_THRESHOLD, "String distance threshold for relations",
				new IntegerLiteral(5),
				new IIORestriction[]{
					ERestrictions.BINDING_INTEGER.getRestriction()
				}
			),
			new IODescription(
				IN_DROP_RELATIONS, "relations that do not satisfy the threshold are dropped",
				new BooleanLiteral(false),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			),
			new IODescription(
				IN_RELATIONS, "Input relations; if set, similarity measures are added to the relations (reference and target inputs are ignored)",
				new IIORestriction[]{
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
		)};
	}
	
	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_RELATIONS, "Output relations",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected ISimilarityMeasurementDescription[] getSupportedMeasurements() {		
		return new SimilarityMeasurementDescription[]{
				new SimilarityMeasurementDescription(
				MEASUREMENT_ID, MEASUREMENT_DESC,
				new MeasurementRange<Integer>(
						new IntegerLiteral[]{new IntegerLiteral(0), new IntegerLiteral(Integer.MIN_VALUE)}, 
						true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}
	
}
