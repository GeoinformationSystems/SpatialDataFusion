package de.tudresden.geoinfo.fusion.operation.measurement;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.relation.*;
import de.tudresden.geoinfo.fusion.metadata.IMeasurementRange;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.metadata.MetadataForMeasurement;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRelationMeasurement extends AbstractOperation {

    private final static IIdentifier IN_DOMAIN = new Identifier("IN_DOMAIN");
    private final static IIdentifier IN_RANGE = new Identifier("IN_RANGE");
    private final static IIdentifier IN_RELATIONS = new Identifier("IN_RELATIONS");
    private final static IIdentifier IN_DROP_RELATIONS = new Identifier("IN_DROP_RELATIONS");

    private final static IIdentifier OUT_MEASUREMENTS = new Identifier("OUT_MEASUREMENTS");
    private final static IIdentifier OUT_RELATIONS = new Identifier("OUT_RELATIONS");

	private MetadataForMeasurement measurementDescription;

    /**
     * constructor
     * @param identifier operation identifier
     */
    public AbstractRelationMeasurement(IIdentifier identifier){
        super(identifier);
    }

	@Override
	public void execute() {
        //get input connectors
        IInputConnector sourceConnector = getInputConnector(IN_DOMAIN);
        IInputConnector targetConnector = getInputConnector(IN_RANGE);
        IInputConnector relationsConnector = getInputConnector(IN_RELATIONS);
        IInputConnector dropRelationConnector = getInputConnector(IN_DROP_RELATIONS);
        //get inputs
        GTFeatureCollection sourceFeatures = (GTFeatureCollection) sourceConnector.getData();
        GTFeatureCollection targetFeatures = (GTFeatureCollection) targetConnector.getData();
        FeatureRelationCollection relations = relationsConnector.isConnected() ? (FeatureRelationCollection) relationsConnector.getData() : null;
        BooleanLiteral dropRelations = dropRelationConnector.isConnected() ? (BooleanLiteral) dropRelationConnector.getData() : (BooleanLiteral) getInputConnector(IN_DROP_RELATIONS).getDefault();
        //measurement without existing relations
        if(relations == null)
            connectOutput(OUT_MEASUREMENTS, performRelationMeasurement(sourceFeatures, targetFeatures));
        //measurement with existing relations
        else
            connectOutput(OUT_RELATIONS, performRelationMeasurement(sourceFeatures, targetFeatures, relations, dropRelations.resolve()));
    }

    /**
     * perform relation measurements
     * @param domainFeatures source features
     * @param rangeFeatures target features
     * @return result relations
     */
	public RelationMeasurementCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures){
        RelationMeasurementCollection relationMeasurements = new RelationMeasurementCollection(null, null);
        for(GTVectorFeature domain : domainFeatures){
            for(GTVectorFeature range : rangeFeatures){
                IRelationMeasurement measurement = performRelationMeasurement(domain, range);
                if(measurement != null)
                    relationMeasurements.add(measurement);
            }
        }
        return relationMeasurements;
    }

    /**
     * perform relation measurements based on existing relations
     * @param domainFeatures source features
     * @param rangeFeatures target features
     * @param relations exsting relations
     * @param dropRelations flag: drop relations, if this measurement is above threshold
     * @return result relations
     */
    public FeatureRelationCollection performRelationMeasurement(GTFeatureCollection domainFeatures, GTFeatureCollection rangeFeatures, FeatureRelationCollection relations, boolean dropRelations){
        for(IRelation<? extends IFeature> relation : relations){
            //continue in case of non-binary relation
            if(!(relation instanceof BinaryFeatureRelation))
                continue;
            //check for feature identifier in domain and range features
            GTVectorFeature domain = domainFeatures.getFeatureById(((BinaryFeatureRelation) relation).getDomain().getIdentifier());
            GTVectorFeature range = rangeFeatures.getFeatureById(((BinaryFeatureRelation) relation).getRange().getIdentifier());
            if(domain == null || range == null)
                continue;
            //add relation measurement
            IRelationMeasurement measurement = performRelationMeasurement(domain, range);
            if(measurement == null && dropRelations)
                continue;
            ((BinaryFeatureRelation) relation).addMeasurement(measurement);
        }
        return relations;
    }

    /**
     * perform relation measurements
     * @param domainFeature source feature
     * @param rangeFeature target feature
     * @return feature relation or null, if relation measurement is above threshold
     */
    public abstract IRelationMeasurement performRelationMeasurement(GTVectorFeature domainFeature, GTVectorFeature rangeFeature);

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
        inputConnectors.put(IN_RELATIONS, new InputConnector(
                IN_RELATIONS,
                new MetadataForConnector(IN_RELATIONS.toString(), "Existing relations"),
                new IDataConstraint[]{
                        new BindingConstraint(FeatureRelationCollection.class)},
                null,
                null));
        inputConnectors.put(IN_DROP_RELATIONS, new InputConnector(
                IN_DROP_RELATIONS,
                new MetadataForConnector(IN_DROP_RELATIONS.toString(), "Flag: drop existing relationships, if this measurement is above threshold)"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false)));
        return inputConnectors;
    }

    @Override
    public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
        Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
        outputConnectors.put(OUT_MEASUREMENTS, new OutputConnector(
                OUT_MEASUREMENTS,
                new MetadataForConnector(OUT_MEASUREMENTS.toString(), "Output relation measurements"),
                new IDataConstraint[]{
                        new BindingConstraint(RelationMeasurementCollection.class)},
                null));
        outputConnectors.put(OUT_RELATIONS, new OutputConnector(
                OUT_RELATIONS,
                new MetadataForConnector(OUT_RELATIONS.toString(), "Output feature relations with attached measurements"),
                new IDataConstraint[]{
                        new BindingConstraint(FeatureRelationCollection.class)},
                null));
        return outputConnectors;
    }

    /**
     * get measurement description
     * @return measurement description
     */
    public MetadataForMeasurement getMetadataForMeasurement() {
        if (measurementDescription == null)
            initMeasurementDescription();
        return measurementDescription;
    }

    /**
     * initialize measurement description
     */
    public void initMeasurementDescription(){
        measurementDescription = new MetadataForMeasurement(
                getMeasurementTitle(),
                getMeasurementDescription(),
                getMeasurementOperation(),
                getMeasurementDataType(),
                getMeasurementRange(),
                getMeasurementUnit()
        );
    }

    /**
     * get measurement title
     * @return measurement title
     */
    protected abstract String getMeasurementTitle();

    /**
     * get measurement abstract
     * @return measurement abstract
     */
    protected abstract String getMeasurementDescription();

    /**
     * get measurement data type
     * @return measurement data type
     */
    protected abstract IResource getMeasurementDataType();

    /**
     * get measurement operation
     * @return measurement operation
     */
    protected abstract IResource getMeasurementOperation();

    /**
     * get measurement range
     * @return measurement range
     */
    protected abstract IMeasurementRange getMeasurementRange();

    /**
     * get measurement unit
     * @return measurement unit
     */
    protected abstract IResource getMeasurementUnit();

}
