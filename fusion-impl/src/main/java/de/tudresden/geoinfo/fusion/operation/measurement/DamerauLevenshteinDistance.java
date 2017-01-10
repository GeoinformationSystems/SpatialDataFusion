package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Operations;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.InputConnector;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.util.Arrays;
import java.util.Map;

/**
 * bounding box distance
 */
public class DamerauLevenshteinDistance extends AbstractRelationMeasurement {

    private static final IIdentifier PROCESS = new Identifier(GeometryDistance.class.getSimpleName());

    private final static IIdentifier IN_DOMAIN_ATTRIBUTE = new Identifier("IN_DOMAIN_ATTRIBUTE");
    private final static IIdentifier IN_RANGE_ATTRIBUTE = new Identifier("IN_RANGE_ATTRIBUTE");
    private final static IIdentifier IN_THRESHOLD = new Identifier("IN_THRESHOLD");

    private static final IResource MEASUREMENT_OPERATION = Operations.STRING_DAMERAU_LEVENSHTEIN.getResource();
    private static final IResource MEASUREMENT_TYPE = Objects.INTEGER.getResource();
    private static final IResource MEASUREMENT_UNIT = Units.UNDEFINED.getResource();

    /**
     * constructor
     */
    public DamerauLevenshteinDistance() {
        super(PROCESS);
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //get attributes
        String sAttributeNameDomain = ((StringLiteral) getInputConnector(IN_DOMAIN_ATTRIBUTE).getData()).resolve();;
        String sAttributeNameRange = ((StringLiteral) getInputConnector(IN_RANGE_ATTRIBUTE).getData()).resolve();;
        int iThreshold = ((IntegerLiteral) getInputConnector(IN_THRESHOLD).getData()).resolve();
        //check attributes
        if (sAttributeNameDomain == null || sAttributeNameRange == null || domainFeature.resolve().getAttribute(sAttributeNameDomain) == null || rangeFeature.resolve().getAttribute(sAttributeNameRange) == null)
            return null;
        //get Attributes
        String sAttributeValueDomain = domainFeature.resolve().getAttribute(sAttributeNameDomain).toString();
        String sAttributeValueRange = rangeFeature.resolve().getAttribute(sAttributeNameRange).toString();
        //return null, if at least one attribute is empty
        if(sAttributeValueDomain == null || sAttributeValueDomain.isEmpty() || sAttributeValueRange == null || sAttributeValueRange.isEmpty())
            return null;
        //calculate distance
		int iDistance = getDamerauLevenshteinDistance(sAttributeValueDomain, sAttributeValueRange);
        if (iDistance <= iThreshold)
            return new RelationMeasurement(null, domainFeature, rangeFeature, IntegerLiteral.getMeasurement(iDistance, getMetadataForMeasurement()));
        //return null if distance > threshold
        else
            return null;
    }

    /**
	 * calculates Damerau Levenshtein Distance between 2 Strings
	 * @param string1 reference String
	 * @param string2 target String
	 * @return Damerau Levenshtein Distance
	 */
	private int getDamerauLevenshteinDistance(String string1, String string2) {
		//get length of Strings
		int str1Len = string1.length();
		int str2Len = string2.length();
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
				if(string1.charAt(i-1) == string2.charAt(j-1)) cost = 0;
				else cost = 1;
				minArray[0] = matrix[i-1][j] + 1; //deletion
				minArray[1] = matrix[i][j-1] + 1; //insertion
				minArray[2] = matrix[i-1][j-1] + cost; //substitution
				Arrays.sort(minArray);
				matrix[i][j] = minArray[0];
				//calculate transportation (after Damerau)
				if(i>1 && j>1 && string1.charAt(i-1) == string2.charAt(j-2) && string1.charAt(i-2) == string2.charAt(j-1)) {
					matrix[i][j] = Math.min(matrix[i][j], matrix[i-2][j-2] + cost); //transportation
				}
			}
		}
		//return Damerau-Levenshtein-Distance
		return(matrix[str1Len][str2Len]);
	}

    @Override
    public String getProcessTitle() {
        return "Damerau-Levenshtein distance calculation";
    }

    @Override
    public String getProcessAbstract() {
        return "Calculates feature relation based on Damerau-Levenshtein distance between specified attributes";
    }

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier, IInputConnector> inputConnectors = super.initInputConnectors();
        inputConnectors.put(IN_THRESHOLD, new InputConnector(
                IN_THRESHOLD,
                new MetadataForConnector(IN_THRESHOLD.toString(), "Distance threshold for creating a relation"),
                new IDataConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new IntegerLiteral(2)));
        inputConnectors.put(IN_DOMAIN_ATTRIBUTE, new InputConnector(
                IN_DOMAIN_ATTRIBUTE,
                new MetadataForConnector(IN_DOMAIN_ATTRIBUTE.toString(), "Attribute name in domain feature"),
                new IDataConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_RANGE_ATTRIBUTE, new InputConnector(
                IN_RANGE_ATTRIBUTE,
                new MetadataForConnector(IN_RANGE_ATTRIBUTE.toString(), "Attribute name in range feature"),
                new IDataConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryConstraint()},
                null,
                null));
        return inputConnectors;
    }

    @Override
    protected String getMeasurementTitle() {
        return "Damerau-Levenshtein distance";
    }

    @Override
    protected String getMeasurementDescription() {
        return "Damerau-Levenshtein distance between input attributes";
    }

    @Override
    protected IResource getMeasurementDataType() {
        return MEASUREMENT_TYPE;
    }

    @Override
    protected IResource getMeasurementOperation() {
        return MEASUREMENT_OPERATION;
    }

    @Override
    protected IMeasurementRange getMeasurementRange() {
        return IntegerLiteral.getPositiveRange();
    }

    @Override
    protected IResource getMeasurementUnit() {
        return MEASUREMENT_UNIT;
    }

}
