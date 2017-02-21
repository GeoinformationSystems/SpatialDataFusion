package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

public abstract class AbstractRelationMeasurement extends AbstractOperation {

    private final static String IN_DOMAIN_TITLE = "IN_DOMAIN";
    private final static String IN_DOMAIN_DESCRIPTION = "Domain features";
    private final static String IN_RANGE_TITLE = "IN_RANGE";
    private final static String IN_RANGE_DESCRIPTION = "Range features";
    private final static String IN_RELATIONS_TITLE = "IN_RELATIONS";
    private final static String IN_RELATIONS_DESCRIPTION = "Existing relations";
    private final static String IN_DROP_RELATIONS_TITLE = "IN_DROP_RELATIONS";
    private final static String IN_DROP_RELATIONS_DESCRIPTION = "Flag: existing relationships can be dropped";

    private final static String OUT_MEASUREMENTS_TITLE = "OUT_MEASUREMENTS";
    private final static String OUT_MEASUREMENTS_DESCRIPTION = "Relation measurements";
    private final static String OUT_RELATIONS_TITLE = "OUT_RELATIONS";
    private final static String OUT_RELATIONS_DESCRIPTION = "Feature relations with attached measurements";

    /**
     * constructor
     *
     * @param title operation title
     */
    public AbstractRelationMeasurement(String title, String description) {
        super(title, description);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector sourceConnector = getInputConnector(IN_DOMAIN_TITLE);
        IInputConnector targetConnector = getInputConnector(IN_RANGE_TITLE);
        IInputConnector relationsConnector = getInputConnector(IN_RELATIONS_TITLE);
        IInputConnector dropRelationConnector = getInputConnector(IN_DROP_RELATIONS_TITLE);
        //get inputs
        GTFeatureCollection sourceFeatures = (GTFeatureCollection) sourceConnector.getData();
        GTFeatureCollection targetFeatures = (GTFeatureCollection) targetConnector.getData();
        BinaryFeatureRelationCollection relations = relationsConnector.getData() != null ? (BinaryFeatureRelationCollection) relationsConnector.getData() : null;
        BooleanLiteral dropRelations = (BooleanLiteral) dropRelationConnector.getData();
        //measurement without existing relations
        if (relations == null)
            connectOutput(OUT_MEASUREMENTS_TITLE, performRelationMeasurement(sourceFeatures, targetFeatures));
            //measurement with existing relations
        else
            connectOutput(OUT_RELATIONS_TITLE, performRelationMeasurement(sourceFeatures, targetFeatures, relations, dropRelations.resolve()));
    }

    /**
     * perform relation measurements
     *
     * @param domainFeatures source features
     * @param rangeFeatures  target features
     * @return result relations
     */
    public RelationMeasurementCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures) {
        RelationMeasurementCollection relationMeasurements = new RelationMeasurementCollection(null, null, null);
        for (GTVectorFeature domain : domainFeatures) {
            for (GTVectorFeature range : rangeFeatures) {
                IRelationMeasurement measurement = performRelationMeasurement(domain, range);
                if (measurement != null)
                    relationMeasurements.add(measurement);
            }
        }
        return relationMeasurements;
    }

    /**
     * perform relation measurements based on existing relations
     *
     * @param domainFeatures source features
     * @param rangeFeatures  target features
     * @param relations      exsting relations
     * @param dropRelations  flag: drop relations, if this measurement is above threshold
     * @return result relations
     */
    public BinaryFeatureRelationCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, BinaryFeatureRelationCollection relations, boolean dropRelations) {
        for (IRelation<? extends IFeature> relation : relations) {
            //continue in case of non-binary relation
            if (!(relation instanceof BinaryFeatureRelation))
                continue;
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getFeatureById(((BinaryFeatureRelation) relation).getDomain().getIdentifier());
            GTVectorFeature range = rangeFeatures.getFeatureById(((BinaryFeatureRelation) relation).getRange().getIdentifier());
            if (domain == null || range == null)
                continue;
            //add relation measurement
            IRelationMeasurement measurement = performRelationMeasurement(domain, range);
            if (measurement == null && dropRelations)
                continue;
            ((BinaryFeatureRelation) relation).addMeasurement(measurement);
        }
        return relations;
    }

    /**
     * perform relation measurements
     *
     * @param domainFeature source feature
     * @param rangeFeature  target feature
     * @return feature relation or null, if relation measurement is above threshold
     */
    public abstract IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature);

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null);
        addInputConnector(IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null);
        addInputConnector(IN_RELATIONS_TITLE, IN_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryFeatureRelationCollection.class)},
                null,
                null);
        addInputConnector(IN_DROP_RELATIONS_TITLE, IN_DROP_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new InputData(new BooleanLiteral(false)).getOutputConnector());
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_MEASUREMENTS_TITLE, OUT_MEASUREMENTS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null);
        addOutputConnector(OUT_RELATIONS_TITLE, OUT_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryFeatureRelationCollection.class)},
                null);
    }

}
