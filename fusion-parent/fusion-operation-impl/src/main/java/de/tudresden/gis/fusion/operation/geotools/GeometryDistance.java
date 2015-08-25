package de.tudresden.gis.fusion.operation.geotools;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.description.DataProvenance;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
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
			RDFVocabulary.TYPE_MEAS_TOP_INTERSECTS.identifier(),
			"intersection",
			"intersection between feature geometries",
			BooleanLiteral.maxRange(),
			RDFVocabulary.TYPE_UOM_UNDEFINED.resource(),
			new DataProvenance(this.processDescription()));
	
	private MeasurementDescription distanceDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_GEOM_DISTANCE.identifier(),
			"distance",
			"minimum distance between feature geometries",
			DecimalLiteral.positiveRange(),
			RDFVocabulary.TYPE_UOM_UNKNOWN.resource(),
			new DataProvenance(this.processDescription()));

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) input(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) input(IN_TARGET);
		dThreshold = ((DecimalLiteral) input(IN_THRESHOLD)).value();
		bDropRelations = inputContainsKey(IN_DROP_RELATIONS) ? ((BooleanLiteral) input(IN_DROP_RELATIONS)).value() : false;
		
		//execute
		IDataCollection<IRelation<IFeatureView>> relations = 
				inputContainsKey(IN_RELATIONS) ?
						relations(inSource, inTarget, (FeatureRelationCollection) input(IN_RELATIONS), bDropRelations) :
						relations(inSource, inTarget);
			
		//return
		setOutput(OUT_RELATIONS, relations);
				
	}
	
	@Override
	protected IRelationMeasurement<? extends Comparable<?>> measurement(IFeatureView reference, IFeatureView target){
		//get geometries
		Geometry gReference = ((GTFeature) reference).geometry();
		Geometry gTarget = ((GTFeature) target).geometry();
		if(gReference.isEmpty() || gTarget.isEmpty())
			return null;
		//get overlap
		boolean bIntersect = getIntersect(gReference, gTarget);
		//check for overlap		
		if(bIntersect) {
			return new RelationMeasurement<Boolean>(
					null, 
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					bIntersect, 
					intersectDescription);
		}
		else {
			//get distance
			double dDistance = getDistance(gReference, gTarget);
			//check for overlap
			if(dDistance <= dThreshold)
				return new RelationMeasurement<Double>(
						null, 
						RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
						RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
						dDistance, 
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
	public IRI processIdentifier() {
		return new IRI(this.getClass().getSimpleName());
	}

	@Override
	public String processTitle() {
		return "Geometry distance calculation";
	}

	@Override
	public String processAbstract() {
		return "Calculates feature relation based on minimum geometry distance";
	}

	@Override
	public Collection<IProcessConstraint> processConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> inputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> outputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
