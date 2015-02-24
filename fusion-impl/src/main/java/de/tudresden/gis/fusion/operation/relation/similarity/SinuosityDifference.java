package de.tudresden.gis.fusion.operation.relation.similarity;

import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractRelationMeasurement;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class SinuosityDifference extends AbstractRelationMeasurement {

	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#SinuosityDifference";
	private final String RELATION_SIN_DIFF = "http://tu-dresden.de/uw/geo/gis/fusion/similarity/spatial#difference_sinuosity";
	
	private double dThreshold;
	
	@Override
	public void execute() {
		
		//get input
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		dThreshold = ((DecimalLiteral) getInput(IN_THRESHOLD)).getValue();
		setDropRelations((BooleanLiteral) getInput(IN_DROP_RELATIONS));
		
		//execute
		IFeatureRelationCollection relations = (inputContainsKey(IN_RELATIONS) ?
				calculateRelation(inReference, inTarget, (IFeatureRelationCollection) getInput(IN_RELATIONS)) :
				calculateRelation(inReference, inTarget));
			
		//return
		setOutput(OUT_RELATIONS, relations);
		
	}
	
	@Override
	protected SimilarityMeasurement calculateSimilarity(IFeature reference, IFeature target) {
		//get geometries
		Geometry gReference = (Geometry) reference.getDefaultSpatialProperty().getValue();
		Geometry gTarget = (Geometry) target.getDefaultSpatialProperty().getValue();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get length difference
		double difference = getSinuosity(gReference) - getSinuosity(gTarget);
		if(Math.abs(difference) <= dThreshold){
			return new SimilarityMeasurement(
					new DecimalLiteral(difference), 
					this.getMeasurementDescription(new RelationType(new IRI(RELATION_SIN_DIFF)))
			);
		}
		else return null;
	}
	
	/**
	 * calculate sinuosity from geometry
	 * @param geometry  input geometrx
	 * @return sinuosity
	 */
	private double getSinuosity(Geometry geometry){
		//get coordinates
		Coordinate[] coords = geometry.getCoordinates();
		//return 0, if geometry consists of less than 2 points
		if(coords.length <= 1) return 0;
		//calculate sinuosity
		double basis = coords[0].distance(coords[coords.length-1]);
		double length = geometry.getLength();
		//return sinuosity
		if(basis > 0 && length > 0) return length / basis;
		else return 0;
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
		return "Calculates sinuosity difference between linear input geometries";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
						new IRI(IN_REFERENCE), "Reference features",
						new IDataRestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.GEOMETRY_NoPOINT.getRestriction()
						})
		);
		inputs.add(new IODescription(
					new IRI(IN_TARGET), "Target features",
					new IDataRestriction[]{
						ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
						ERestrictions.GEOMETRY_NoPOINT.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_THRESHOLD), "Sinuosity difference threshold for relations",
					new DecimalLiteral(2),
					new IDataRestriction[]{
						ERestrictions.BINDING_DECIMAL.getRestriction()
					})
		);
		inputs.add(new IODescription(
				new IRI(IN_DROP_RELATIONS), "relations that do not satisfy the threshold are dropped",
				new BooleanLiteral(false),
				new IDataRestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
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
					"Sinuosity difference between linear geometries (reference - target)", 
					new RelationType(new IRI(RELATION_SIN_DIFF)),
					new MeasurementRange<Double>(
							new DecimalLiteral[]{new DecimalLiteral(Double.MIN_VALUE), new DecimalLiteral(Double.MAX_VALUE)}, 
							true
					))
		);
		return measurements;
	}

}
