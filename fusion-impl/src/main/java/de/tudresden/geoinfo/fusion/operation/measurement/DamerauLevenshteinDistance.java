package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.data.relation.IRelationMeasurement;
import de.tudresden.geoinfo.fusion.data.relation.RelationMeasurement;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;

import java.util.Arrays;

/**
 * Damerau-Levenshtein distance
 */
public class DamerauLevenshteinDistance extends AbstractRelationMeasurement {

    private static final String PROCESS_TITLE = GeometryDistance.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Calculates feature relation based on Damerau-Levenshtein distance between specified attributes";

    private static final IMeasurementRange<Integer> MEASUREMENT_RANGE = IntegerLiteral.getPositiveRange();
    private static final String MEASUREMENT_TITLE = "Damerau-Levenshtein distance";
    private static final String MEASUREMENT_DESCRIPTION = "Damerau-Levenshtein distance between input attributes";
    private static final IResource MEASUREMENT_UNIT = Units.UNDEFINED.getResource();

    private final static String IN_DOMAIN_ATTRIBUTE_TITLE = "IN_DOMAIN_ATTRIBUTE";
    private final static String IN_DOMAIN_ATTRIBUTE_DESCRIPTION = "Attribute name in domain feature";
    private final static String IN_RANGE_ATTRIBUTE_TITLE = "IN_RANGE_ATTRIBUTE";
    private final static String IN_RANGE_ATTRIBUTE_DESCRIPTION = "Attribute name in range feature";
    private static final String IN_THRESHOLD_TITLE = "IN_THRESHOLD";
    private static final String IN_THRESHOLD_DESCRIPTION = "Distance threshold for creating a relation";

    private int iThreshold;
    private String sAttributeNameDomain;
    private String sAttributeNameRange;

    /**
     * constructor
     */
    public DamerauLevenshteinDistance() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        this.iThreshold = ((IntegerLiteral) getInputConnector(IN_THRESHOLD_TITLE).getData()).resolve();
        this.sAttributeNameDomain = ((StringLiteral) getInputConnector(IN_DOMAIN_ATTRIBUTE_TITLE).getData()).resolve();
        this.sAttributeNameRange = ((StringLiteral) getInputConnector(IN_RANGE_ATTRIBUTE_TITLE).getData()).resolve();
        super.execute();
    }

    @Override
    public IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature) {
        //check attributes
        if (sAttributeNameDomain == null || sAttributeNameRange == null || domainFeature.resolve().getAttribute(sAttributeNameDomain) == null || rangeFeature.resolve().getAttribute(sAttributeNameRange) == null)
            return null;
        //get Attributes
        String sAttributeValueDomain = domainFeature.resolve().getAttribute(sAttributeNameDomain).toString();
        String sAttributeValueRange = rangeFeature.resolve().getAttribute(sAttributeNameRange).toString();
        //return null, if at least one attribute is empty
        if (sAttributeValueDomain == null || sAttributeValueDomain.isEmpty() || sAttributeValueRange == null || sAttributeValueRange.isEmpty())
            return null;
        //calculate distance
        int iDistance = getDamerauLevenshteinDistance(sAttributeValueDomain, sAttributeValueRange);
        if (iDistance <= iThreshold)
            return new RelationMeasurement<>(null, domainFeature, rangeFeature, iDistance, MEASUREMENT_TITLE, MEASUREMENT_DESCRIPTION, this, MEASUREMENT_RANGE, MEASUREMENT_UNIT);
            //return null if distance > threshold
        else
            return null;
    }

    /**
     * calculates Damerau Levenshtein Distance between 2 Strings
     *
     * @param string1 reference String
     * @param string2 target String
     * @return Damerau Levenshtein Distance
     */
    private int getDamerauLevenshteinDistance(String string1, String string2) {
        //get length of Strings
        int str1Len = string1.length();
        int str2Len = string2.length();
        //return 0 if one or both strings empty
        if (str1Len == 0 || str2Len == 0) return 0;
        //create matrix
        int[][] matrix = new int[str1Len + 1][str2Len + 1];
        //tmp variable cost
        int cost;
        //tmp variable Array for minimum value
        int[] minArray = new int[3];
        //set first row,col of matrix
        for (int i = 0; i <= str1Len; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= str2Len; j++) {
            matrix[0][j] = j;
        }
        //loop through matrix
        for (int i = 1; i <= str1Len; i++) {
            for (int j = 1; j <= str2Len; j++) {
                //set cost = 1 if chars are not equal
                if (string1.charAt(i - 1) == string2.charAt(j - 1)) cost = 0;
                else cost = 1;
                minArray[0] = matrix[i - 1][j] + 1; //deletion
                minArray[1] = matrix[i][j - 1] + 1; //insertion
                minArray[2] = matrix[i - 1][j - 1] + cost; //substitution
                Arrays.sort(minArray);
                matrix[i][j] = minArray[0];
                //calculate transportation (after Damerau)
                if (i > 1 && j > 1 && string1.charAt(i - 1) == string2.charAt(j - 2) && string1.charAt(i - 2) == string2.charAt(j - 1)) {
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i - 2][j - 2] + cost); //transportation
                }
            }
        }
        //return Damerau-Levenshtein-Distance
        return (matrix[str1Len][str2Len]);
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(IN_THRESHOLD_TITLE, IN_THRESHOLD_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new InputData(new IntegerLiteral(2)).getOutputConnector());
        addInputConnector(IN_DOMAIN_ATTRIBUTE_TITLE, IN_DOMAIN_ATTRIBUTE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_RANGE_ATTRIBUTE_TITLE, IN_RANGE_ATTRIBUTE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

}
