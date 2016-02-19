package de.tudresden.gis.fusion.operation.measurement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IDataCollection;
import de.tudresden.gis.fusion.data.MeasurementRange;
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

public class GeometryOverlap extends ARelationMeasurementOperation {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private boolean bDropRelations;
	
	private MeasurementDescription overlapDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_TOP_OVERLAPS.asString(),
			"overlap",
			"overlap between feature geometries",
			new MeasurementRange(new TreeSet<DecimalLiteral>(Arrays.asList(new DecimalLiteral(0d), new DecimalLiteral(100d))), true),
			RDFVocabulary.UOM_PERCENT.asResource());

	@Override
	public void execute() throws ProcessException {

		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) input(IN_SOURCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) input(IN_TARGET);
		
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
		double dOverlap = getOverlap(gReference, gTarget) * 100;
		//check for overlap		
		if(dOverlap >= 0) {
			return new RelationMeasurement(
					null, 
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					RDFVocabulary.PROPERTY_GEOM.asResource(),
					new DecimalLiteral(dOverlap), 
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
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Geometry overlap calculation";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Calculates feature relation based on geometry overlap";
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
