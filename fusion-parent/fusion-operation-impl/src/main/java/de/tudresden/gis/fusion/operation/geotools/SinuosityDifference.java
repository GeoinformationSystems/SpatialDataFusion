package de.tudresden.gis.fusion.operation.geotools;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
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
			RDFVocabulary.TYPE_MEAS_GEOM_DIFFERENCE_SINUOSITY.identifier(),
			"sinuosity difference",
			"sinuosity difference between feature geometries",
			DecimalLiteral.maxRange(),
			RDFVocabulary.TYPE_UOM_UNDEFINED.resource(),
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
		//get angle
		double dDiff = getSinuosityDiff(gReference, gTarget);
		//check for overlap		
		if(Math.abs(dDiff) <= dThreshold) {
			return new RelationMeasurement<Double>(
					null, 
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					RDFVocabulary.TYPE_PROPERTY_GEOM.resource(),
					dDiff, 
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
	public IRI processIdentifier() {
		return new IRI(this.getClass().getSimpleName());
	}

	@Override
	public String processTitle() {
		return "Geometry sinuosity difference calculation";
	}

	@Override
	public String processAbstract() {
		return "Calculates feature relation based on sinuosity difference of geometries";
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
