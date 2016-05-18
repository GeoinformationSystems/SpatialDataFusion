package de.tudresden.gis.fusion.operation.measurement;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class DamerauLevenshteinDistance extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_SOURCE_ATT = "IN_SOURCE_ATT";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_TARGET_ATT = "IN_TARGET_ATT";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private String sourceAtt, targetAtt;
	private int iThreshold;
	private boolean bDropRelations;
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_STRING_DAMLEV.asString(),
			"Damerau Levenshtein distance",
			"Damerau Levenshtein distance between two attribute values",
			IntegerLiteral.positiveRange(),
			RDFVocabulary.UOM_UNDEFINED.asResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
		sourceAtt = ((StringLiteral) getInput(IN_SOURCE_ATT)).resolve();
		GTFeatureCollection inTarget = (GTFeatureCollection) getInput(IN_TARGET);
		targetAtt = ((StringLiteral) getInput(IN_TARGET_ATT)).resolve();
		iThreshold = ((IntegerLiteral) getInput(IN_THRESHOLD)).resolve();
		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).resolve();
		
		//execute
		IDataCollection<IFeatureRelation> relations = 
				inputContainsKey(IN_RELATIONS) ?
						relations(inSource, inTarget, (FeatureRelationCollection) getInput(IN_RELATIONS), bDropRelations) :
						relations(inSource, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	@Override
	protected IRelationMeasurement[] getMeasurements(IFeature reference, IFeature target){
		//get attributes
		String sReference = getAttributeValue((GTFeature) reference, sourceAtt);
		String sTarget = getAttributeValue((GTFeature) target, targetAtt);
		if(sReference == null || sReference.isEmpty() || sTarget == null || sTarget.isEmpty())
			return null;
		//get distance
		int iDistance = getDistance(sReference, sTarget);
		//check for overlap
		if(iDistance <= iThreshold){
			return getMeasurements(new RelationMeasurement(
					new StringLiteral(sourceAtt),
					new StringLiteral(targetAtt),
					new IntegerLiteral(iDistance), 
					distanceDescription));
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
		if(property == null || property.getValue() == null)
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
		return "Damerau-Levenshtein String distance calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature attribute relation based on Damerau-Levenshtein String distance for specified attribute";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(
					IN_SOURCE, IN_SOURCE, "Reference features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_SOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_TARGET, IN_TARGET, "Target features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_TARGET),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_THRESHOLD, IN_THRESHOLD, "Threshold for Damerau-Levenshtein distance",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{IntegerLiteral.class})
					},
					new IntegerLiteral(3)));
			inputDescriptions.add(new InputDescription(IN_RELATIONS, IN_RELATIONS, "If set, relation measures are added to existing relations (reference and target inputs are ignored)",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_DROP_RELATIONS, IN_DROP_RELATIONS, "If true, relations that do not satisfy the threshold are dropped",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
					},
					new BooleanLiteral(false)));
			inputDescriptions.add(new InputDescription(IN_SOURCE_ATT, IN_SOURCE_ATT, "Name of the source attribute",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{StringLiteral.class}),
							ContraintFactory.getMandatoryConstraint(IN_SOURCE_ATT)
					}));
			inputDescriptions.add(new InputDescription(IN_TARGET_ATT, IN_TARGET_ATT, "Name of the target attribute",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{StringLiteral.class}),
							ContraintFactory.getMandatoryConstraint(IN_TARGET_ATT)
					}));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_RELATIONS, OUT_RELATIONS, "Output relations with Damerau-Levenshtein distance relation between attributes",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
					}));
		}
		return outputDescriptions;
	}

}
