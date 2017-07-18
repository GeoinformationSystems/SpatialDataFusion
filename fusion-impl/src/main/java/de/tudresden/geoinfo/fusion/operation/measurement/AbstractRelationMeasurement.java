package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private IMetadata measurementMetadata;

    /**
     * constructor
     *
     */
    public AbstractRelationMeasurement(String title, String description) {
        super(title, description);
        this.measurementMetadata = initMeasurementMetadata();
    }

    @Override
    public void executeOperation() {

        //get inputs
        GTFeatureCollection sourceFeatures = (GTFeatureCollection) this.getMandatoryInputData(IN_DOMAIN_TITLE);
        GTFeatureCollection targetFeatures = (GTFeatureCollection) this.getMandatoryInputData(IN_RANGE_TITLE);
        BinaryRelationCollection relations = (BinaryRelationCollection) this.getInputData(IN_RELATIONS_TITLE);
        BooleanLiteral dropRelations = (BooleanLiteral) this.getMandatoryInputData(IN_DROP_RELATIONS_TITLE);

        //measurement without existing relations
        if (relations == null)
            setOutput(OUT_MEASUREMENTS_TITLE, performRelationMeasurement(sourceFeatures, targetFeatures));
            //measurement with existing relations
        else
            setOutput(OUT_RELATIONS_TITLE, performRelationMeasurement(sourceFeatures, targetFeatures, relations, dropRelations.resolve()));
    }

    /**
     * perform relation measurements
     *
     * @param domainFeatures source features
     * @param rangeFeatures  target features
     * @return result relations
     */
    public @NotNull RelationMeasurementCollection performRelationMeasurement(@NotNull GTFeatureCollection domainFeatures, @NotNull GTFeatureCollection rangeFeatures) {
        RelationMeasurementCollection relationMeasurements = new RelationMeasurementCollection(new ResourceIdentifier(), null, null);
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
    public @NotNull BinaryRelationCollection performRelationMeasurement(@NotNull GTFeatureCollection domainFeatures, @NotNull GTFeatureCollection rangeFeatures, @NotNull BinaryRelationCollection relations, boolean dropRelations) {
        BinaryRelationCollection newRelations = new BinaryRelationCollection(relations.getIdentifier(), null, null);
        for (IRelation relation : relations) {
            //continue in case of non-binary relation
            if (!(relation instanceof BinaryRelation))
                continue;
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getMember(((BinaryRelation) relation).getDomain().getIRI());
            GTVectorFeature range = rangeFeatures.getMember(((BinaryRelation) relation).getRange().getIRI());
            if (domain == null || range == null)
                continue;
            //add relation measurement
            IRelationMeasurement measurement = performRelationMeasurement(domain, range);
            if (measurement != null)
                ((BinaryRelation) relation).addMeasurement(measurement);
            else if(dropRelations)
                continue;
            newRelations.add((BinaryRelation) relation);
        }
        return newRelations;
    }

    /**
     * perform relation measurements
     *
     * @param domainFeature source feature
     * @param rangeFeature  target feature
     * @return feature relation or null, if relation measurement is above threshold
     */
    public abstract @Nullable IRelationMeasurement performRelationMeasurement(@NotNull GTVectorFeature domainFeature, @NotNull GTVectorFeature rangeFeature);

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_DOMAIN_TITLE, IN_DOMAIN_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_RANGE_TITLE, IN_RANGE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_RELATIONS_TITLE, IN_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryRelationCollection.class)},
                null,
                null);
        addInputConnector(IN_DROP_RELATIONS_TITLE, IN_DROP_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_MEASUREMENTS_TITLE, OUT_MEASUREMENTS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null);
        addOutputConnector(OUT_RELATIONS_TITLE, OUT_RELATIONS_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BinaryRelationCollection.class)},
                null);
    }

    /**
     * get initialized measurement metadata
     *
     * @return measurement metadata
     */
    final @NotNull IMetadata getMeasurementMetadata() {
        if(this.measurementMetadata == null)
            this.measurementMetadata = initMeasurementMetadata();
        return this.measurementMetadata;
    }

    @NotNull
    public abstract IMetadata initMeasurementMetadata();

}
