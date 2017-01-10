package de.tudresden.geoinfo.fusion.operation.mapping;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Relations;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public abstract class AbstractFeatureMapping extends AbstractOperation {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_RELATIONS = new Identifier("IN_RELATIONS");
    private final static IIdentifier IN_MEASUREMENTS = new Identifier("IN_MEASUREMENTS");
    private final static IIdentifier IN_DROP_RELATIONS = new Identifier("IN_DROP_RELATIONS");

    private final static IIdentifier OUT_RELATIONS = new Identifier("OUT_RELATIONS");

    private BinaryRelationType binaryRelationType;

    /**
     * constructor
     * @param identifier operation identifier
     */
    public AbstractFeatureMapping(IIdentifier identifier){
        super(identifier);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector sourceConnector = getInputConnector(IN_DOMAIN);
        IInputConnector targetConnector = getInputConnector(IN_RANGE);
        IInputConnector measurementsConnector = getInputConnector(IN_MEASUREMENTS);
        IInputConnector relationsConnector = getInputConnector(IN_RELATIONS);
        IInputConnector dropRelationConnector = getInputConnector(IN_DROP_RELATIONS);
        //get inputs
        GTFeatureCollection domainFeatures = (GTFeatureCollection) sourceConnector.getData();
        GTFeatureCollection rangeFeatures = (GTFeatureCollection) targetConnector.getData();
        RelationMeasurementCollection measurements = measurementsConnector.isConnected() ? (RelationMeasurementCollection) relationsConnector.getData() : null;
        FeatureRelationCollection relations = relationsConnector.isConnected() ? (FeatureRelationCollection) relationsConnector.getData() : null;
        BooleanLiteral dropRelations = dropRelationConnector.isConnected() ? (BooleanLiteral) dropRelationConnector.getData() : (BooleanLiteral) getInputConnector(IN_DROP_RELATIONS).getDefault();
        //relations based on measurements
        if(measurements != null)
            connectOutput(OUT_RELATIONS, performRelationMapping(domainFeatures, rangeFeatures, measurements));
        //relations based on measurements attached to existing relations
        else
            connectOutput(OUT_RELATIONS, performRelationMapping(domainFeatures, rangeFeatures, relations, dropRelations.resolve()));
    }

    /**
     * perform relation mapping
     * @param domainFeatures domain features
     * @param rangeFeatures range features
     * @param measurements relation measurements
     * @return feature relations
     */
    private FeatureRelationCollection performRelationMapping(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, RelationMeasurementCollection measurements) {
        FeatureRelationCollection relations = new FeatureRelationCollection(null, null);
        for(GTVectorFeature domain : domainFeatures){
            for(GTVectorFeature range : rangeFeatures){
                Set<IRelationMeasurement> associatedMeasurements = getAssociatedMeasurements(domain, range, measurements);
                if(associatedMeasurements != null && !associatedMeasurements.isEmpty()){
                    IRelation<? extends IFeature> relation = performRelationMapping(domain, range, associatedMeasurements);
                    if(relation != null)
                        relations.add(relation);
                }
            }
        }
        return relations;
    }

    /**
     * get measurements associated with both domain and range feature
     * @param domain domain feature
     * @param range range feature
     * @param measurements relation measurement collection
     * @return set of associated measurements
     */
    private Set<IRelationMeasurement> getAssociatedMeasurements(GTVectorFeature domain, GTVectorFeature range, RelationMeasurementCollection measurements){
        Set<IRelationMeasurement> associatedWithDomain = measurements.getMeasurements(domain);
        Set<IRelationMeasurement> associatedWithRange = measurements.getMeasurements(range);
        associatedWithDomain.retainAll(associatedWithRange);
        return associatedWithDomain;
    }

    /**
     * perform relation mapping
     * @param domainFeatures source features
     * @param rangeFeatures target features
     * @param relations exsting relations
     * @param dropRelations flag: drop relations, if there is no relation created by this operation
     * @return feature relations
     */
    private FeatureRelationCollection performRelationMapping(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, FeatureRelationCollection relations, boolean dropRelations) {
        FeatureRelationCollection newRelations = new FeatureRelationCollection(null, null);
        for(IRelation<? extends IFeature> relation : relations){
            //continue in case of non-binary relation
            if(!(relation instanceof BinaryFeatureRelation))
                continue;
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getFeatureById(((BinaryFeatureRelation) relation).getDomain().getIdentifier());
            GTVectorFeature range = rangeFeatures.getFeatureById(((BinaryFeatureRelation) relation).getRange().getIdentifier());
            if(domain == null || range == null)
                continue;
            //add relation
            IRelation<? extends IFeature> newRelation = performRelationMapping(domain, range, ((BinaryFeatureRelation) relation).getMeasurements());
            if(newRelation == null && dropRelations)
                continue;
            newRelations.add(newRelation);
        }
        return newRelations;
    }

    /**
     * perform relation mapping
     * @param domainFeature domain feature
     * @param rangeFeature range feature
     * @param measurements relation measurements associated to domain and range
     * @return feature relation or null, if no relation is established
     */
    public abstract IRelation<? extends IFeature> performRelationMapping(GTVectorFeature domainFeature, GTVectorFeature rangeFeature, Set<IRelationMeasurement> measurements);

    @Override
    public Map<IIdentifier,IInputConnector> initInputConnectors() {
        Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
        inputConnectors.put(IN_DOMAIN, new InputConnector(
                IN_DOMAIN,
                new MetadataForConnector(IN_DOMAIN.toString(), "Input domain features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_RANGE, new InputConnector(
                IN_RANGE,
                new MetadataForConnector(IN_RANGE.toString(), "Input range features"),
                new IDataConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null,
                null));
        inputConnectors.put(IN_MEASUREMENTS, new InputConnector(
                IN_MEASUREMENTS,
                new MetadataForConnector(IN_MEASUREMENTS.toString(), "Relation measurements"),
                new IDataConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null,
                null));
        inputConnectors.put(IN_RELATIONS, new InputConnector(
                IN_RELATIONS,
                new MetadataForConnector(IN_RELATIONS.toString(), "Existing relations"),
                new IDataConstraint[]{
                        new BindingConstraint(FeatureRelationCollection.class)},
                null,
                null));
        inputConnectors.put(IN_DROP_RELATIONS, new InputConnector(
                IN_DROP_RELATIONS,
                new MetadataForConnector(IN_DROP_RELATIONS.toString(), "Flag: drop existing relationships, if the relation is not confirmed"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false)));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_RELATIONS, new OutputConnector(
                OUT_RELATIONS,
                new MetadataForConnector(OUT_RELATIONS.toString(), "Output feature relations"),
                new IDataConstraint[]{
                        new BindingConstraint(FeatureRelationCollection.class)},
                null));
        return outputConnectors;
    }


    /**
     * get relation type
     * @return relation type
     */
    protected IBinaryRelationType getRelationType(){
        if(binaryRelationType == null)
            initRelationType();
        return binaryRelationType;
    }

    /**
     * initialize relation type
     */
    protected void initRelationType(){
        binaryRelationType = new BinaryRelationType(
                getRelationTypeIdentifier(),
                getRelationTypeMetadata(),
                new Role(Relations.ROLE_DOMAIN.getResource().getIdentifier()),
                new Role(Relations.ROLE_RANGE.getResource().getIdentifier()),
                isSymmetric(),
                isTransitive(),
                isReflexive()
        );
    }

    /**
     * get relation type identifier
     * @return relation type identifier
     */
    protected abstract IIdentifier getRelationTypeIdentifier();

    /**
     * get relation type identifier
     * @return relation type identifier
     */
    protected abstract IMetadataForData getRelationTypeMetadata();

    /**
     * flag: symmetric relation type
     * @return true, if relation type is symmetric
     */
    protected abstract boolean isSymmetric();

    /**
     * flag: transitive relation type
     * @return true, if relation type is transitive
     */
    protected abstract boolean isTransitive();

    /**
     * flag: reflexive relation type
     * @return true, if relation type is reflexive
     */
    protected abstract boolean isReflexive();

}
