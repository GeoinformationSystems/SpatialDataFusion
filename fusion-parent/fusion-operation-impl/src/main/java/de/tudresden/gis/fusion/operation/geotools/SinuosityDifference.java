package de.tudresden.gis.fusion.operation.geotools;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.literal.DecimalLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class SinuosityDifference extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private double dThreshold;
	private boolean bDropRelations;
	
	private MeasurementDescription differenceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DIFFERENCE_SINUOSITY.asString(),
			"sinuosity difference",
			"sinuosity difference between feature geometries",
			DecimalLiteral.maxRange(),
			RDFVocabulary.UOM_UNDEFINED.asResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) input(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) input(IN_TARGET);
		dThreshold = ((DecimalLiteral) input(IN_THRESHOLD)).resolve();
		bDropRelations = inputContainsKey(IN_DROP_RELATIONS) ? ((BooleanLiteral) input(IN_DROP_RELATIONS)).resolve() : false;
		
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
		//get geometries
		Geometry gReference = ((GTFeature) reference).getDefaultGeometry();
		Geometry gTarget = ((GTFeature) target).getDefaultGeometry();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get angle
		double dDiff = getSinuosityDiff(gReference, gTarget);
		//check for overlap		
		if(Math.abs(dDiff) <= dThreshold) {
			return new RelationMeasurement(
					null, 
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					new DecimalLiteral(dDiff), 
					differenceDescription);
		}
		else return null;
	}
	
	/**
	 * get sinuosity difference
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return reference sinuosity - target sinuosity
	 */
	private double getSinuosityDiff(Geometry gReference, Geometry gTarget) {
		return getSinuosity(gReference) - getSinuosity(gTarget);
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
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Geometry sinuosity difference calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on sinuosity difference of geometries";
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
