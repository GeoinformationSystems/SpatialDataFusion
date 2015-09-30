package de.tudresden.gis.fusion.operation.geotools;

import java.util.Collection;
import java.util.Map;

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

public class GeometryDistance extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private double dThreshold;
	private boolean bDropRelations;
	
	private MeasurementDescription intersectDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_TOP_INTERSECTS.asString(),
			"intersection",
			"intersection between feature geometries",
			BooleanLiteral.maxRange(),
			RDFVocabulary.UOM_UNDEFINED.asResource());
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DISTANCE.asString(),
			"distance",
			"minimum distance between feature geometries",
			DecimalLiteral.positiveRange(),
			RDFVocabulary.UOM_UNKNOWN.asResource());

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
		//get overlap
		boolean bIntersect = getIntersect(gReference, gTarget);
		//check for overlap		
		if(bIntersect) {
			return new RelationMeasurement(
					null, 
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					new BooleanLiteral(bIntersect), 
					intersectDescription);
		}
		else {
			//get distance
			double dDistance = getDistance(gReference, gTarget);
			//check for overlap
			if(dDistance <= dThreshold)
				return new RelationMeasurement(
						null, 
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						RDFVocabulary.PROPERTY_GEOM.asResource(),
						new DecimalLiteral(dDistance), 
						distanceDescription);
			//return null if distance > threshold
			else
				return null;
		}
	}
	
	/**
	 * check geometry intersection
	 * @param gReference input reference
	 * @param gTarget input target
	 * @return true, if geometries intersect
	 */
	private boolean getIntersect(Geometry gReference, Geometry gTarget){
		return gReference.intersects(gTarget);
	}
	
	/**
	 * get distance between geometries
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return distance (uom defined by input geometries)
	 */
	private double getDistance(Geometry gReference, Geometry gTarget){
		return gReference.distance(gTarget);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Geometry distance calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on minimum geometry distance";
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
