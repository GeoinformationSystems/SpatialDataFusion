package de.tudresden.gis.fusion.operation.geotools;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.Range;
import de.tudresden.gis.fusion.data.description.DataProvenance;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeatureView;
import de.tudresden.gis.fusion.data.feature.relation.IRelation;
import de.tudresden.gis.fusion.data.feature.relation.IRelationMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class GeometryOverlap extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private boolean bDropRelations;
	
	private MeasurementDescription overlapDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_TOP_OVERLAPS.identifier(),
			"overlap",
			"overlap between feature geometries",
			new Range<Double>(new Double[]{0d, 100d}, true),
			RDFVocabulary.TYPE_UOM_PERCENT.resource(),
			new DataProvenance(this.processDescription()));

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) input(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) input(IN_TARGET);
		
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
		double dOverlap = getOverlap(gReference, gTarget) * 100;
		//check for overlap		
		if(dOverlap >= 0) {
			return new RelationMeasurement<Double>(
					null, 
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					dOverlap, 
					overlapDescription);
		}
		else
			return null;
	}
	
	/**
	 * calculate overlap between input geometries
	 * @param gReference reference geometry
	 * @param gTarget target geometry
	 * @return overlap [0,1]
	 */
	private double getOverlap(Geometry gReference, Geometry gTarget) {
		//get intersection
		Geometry intersection = gReference.intersection(gTarget);
		if(intersection.isEmpty())
			return -999;
		//check for area
		if(intersection.getArea() > 0)
			return intersection.getArea() / gReference.getArea();
		//check for length
		if(intersection.getLength() > 0){
			if(gReference.getArea() > 0)
				return 0;
			else
				return intersection.getLength() / gReference.getLength();
		}
		
		else
			return 0;
	}
	
	@Override
	public IRI processIdentifier() {
		return new IRI(this.getClass().getSimpleName());
	}

	@Override
	public String processTitle() {
		return "Geometry overlap calculation";
	}

	@Override
	public String processAbstract() {
		return "Calculates feature relation based on geometry overlap";
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
