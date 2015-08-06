package de.tudresden.gis.fusion.operation.confidence;

import java.util.HashSet;
import java.util.Set;

import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.complex.ConfidenceMeasurement;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.MeasurementValue;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EMeasurementType;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.ConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementRange;
import de.tudresden.gis.fusion.metadata.data.SimilarityMeasurementDescription;
import de.tudresden.gis.fusion.operation.AConfidenceMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class SimilarityWeighting extends AConfidenceMeasurementOperation {
	
	//process definitions
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_MEASUREMENTS = "IN_MEASUREMENTS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle());
	private final String MEASUREMENT_DESC = "Reliability of a match determined by weighted sum of given similarity measurements";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.SUM.resource()
	};

	@Override
	protected void execute() {

		//get input
		GTFeatureRelationCollection inRelations = (GTFeatureRelationCollection) getInput(IN_RELATIONS);
		StringLiteral measurementsCSV = (StringLiteral) getInput(IN_MEASUREMENTS);
		
		//set measurements
		Set<WeightMeasurement> measurements = new HashSet<WeightMeasurement>();
		String[] csvArray = measurementsCSV.getIdentifier().split(";");
		for(int i=0; i<csvArray.length; i+=3){
			measurements.add(new WeightMeasurement(csvArray[i], csvArray[i+1], csvArray[i+2]));
		}
		
		GTFeatureRelationCollection outRelations = weightMeasurements(inRelations, measurements);
			
		//return
		setOutput(OUT_RELATIONS, outRelations);
				
	}

	/**
	 * calculate the relaibility of a match by weighting of similarity measurements
	 * @param inRelations input relations
	 * @param measurements measurements with weights
	 * @param dThreshold threshold weight
	 * @return final relations with normalized reliability measurement (0..1)
	 */
	private GTFeatureRelationCollection weightMeasurements(GTFeatureRelationCollection relations, Set<WeightMeasurement> measurements) {
		
		GTFeatureRelationCollection outRelations = new GTFeatureRelationCollection();
		
		//relativize weights
		relativizeWeights(measurements);
				
		for(IFeatureRelation relation : relations){
			double weight = getWeight(relation, measurements);
			if(weight < 0.8)
				continue;
			relation.addRelationMeasurement(
				new ConfidenceMeasurement( 
					new DecimalLiteral(weight),
					this.PROCESS_RESOURCE,
					this.getMeasurementDescription(MEASUREMENT_ID)
				)
			);
			outRelations.addRelation(relation);
		}
		return outRelations;
	}

	/**
	 * get sum of weights
	 * @param relation input relations
	 * @param measurements similarity measurements to count for
	 * @return
	 */
	private double getWeight(IFeatureRelation relation, Set<WeightMeasurement> measurements) {
		double weight = 0;
		for(IRelationMeasurement rMeasurement : relation.getMeasurements()){
			if(rMeasurement instanceof SimilarityMeasurement){
				for(WeightMeasurement wMeasurement : measurements){
					if(wMeasurement.getIdentifier().equals(((SimilarityMeasurementDescription) ((SimilarityMeasurement) rMeasurement).getDescription()).getIdentifier()))
						if(wMeasurement.getThreshold().compareToAny(rMeasurement.getMeasurementValue()) >= 0)
							weight += wMeasurement.getWeight();
				}
			}
				
		}
		return weight;
	}

	/**
	 * relative weights
	 * @param measurements measurements with relativized weights
	 * @return 
	 */
	private void relativizeWeights(Set<WeightMeasurement> measurements) {
		//get sum of weights
//		double sum = getSum(measurements);
		double sum = 100;
		//relativize
		for(WeightMeasurement measurement : measurements){
			measurement.setWeight(measurement.getWeight() / sum);
		}
	}

	/**
	 * calculate sum of numeric collection
	 * @param measurements numeric collection
	 * @return sum
	 */
	private double getSum(Set<WeightMeasurement> measurements) {
		double sum = 0;
		for(WeightMeasurement measurement : measurements){
			sum += measurement.getWeight();
		}
		return sum;
	}

	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessAbstract() {
		return "Determines reliability of a match by weighting supported similarity measurements, deletes relations with weight < threshold";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_MEASUREMENTS, "Measurements to be weighted (CSV formatted: Measurement Description URI, weight;...)",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
				IN_RELATIONS, "Input relations for which similarity measurements are weighted",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
		)};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_RELATIONS, "Output relations",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReRELATIOnCOLLECTION.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected IConfidenceMeasurementDescription[] getSupportedMeasurements() {		
		return new ConfidenceMeasurementDescription[]{
			new ConfidenceMeasurementDescription(
				MEASUREMENT_ID, MEASUREMENT_DESC,
				new MeasurementRange<Double>(
					new DecimalLiteral[]{new DecimalLiteral(0), new DecimalLiteral(1)}, 
					true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}
	
	private class WeightMeasurement {
		
		private IIRI identifier;
		private double weight;
		private MeasurementValue threshold;
		
		public WeightMeasurement(String identifier, String threshold, String weight){
			this.identifier = new IRI(identifier);
			this.weight = Double.parseDouble(weight);
			this.threshold = new MeasurementValue(threshold);
		}
		
		public IIRI getIdentifier(){ return identifier;	}
		public double getWeight(){ return weight; }
		public MeasurementValue getThreshold(){ return threshold; }
		
		public void setWeight(double weight){ this.weight = weight; }
		
	}

}
