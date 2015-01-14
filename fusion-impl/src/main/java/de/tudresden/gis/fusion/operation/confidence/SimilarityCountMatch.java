package de.tudresden.gis.fusion.operation.confidence;

import java.util.ArrayList;
import java.util.Collection;

import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IRelationMeasurement;
import de.tudresden.gis.fusion.data.complex.ConfidenceMeasurement;
import de.tudresden.gis.fusion.data.complex.SimilarityMeasurement;
import de.tudresden.gis.fusion.data.geotools.GTFeatureRelationCollection;
import de.tudresden.gis.fusion.data.metadata.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.RelationType;
import de.tudresden.gis.fusion.metadata.IODescription;
import de.tudresden.gis.fusion.metadata.MeasurementDescription;
import de.tudresden.gis.fusion.metadata.MeasurementRange;
import de.tudresden.gis.fusion.operation.AbstractMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class SimilarityCountMatch extends AbstractMeasurementOperation {

	//process definitions
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#SimilarityCountMatch";
	private final String CONFIDENCE_SIMILARITY_COUNT = "http://tu-dresden.de/uw/geo/gis/fusion/confidence/statisticalConfidence#count_similarity";
		
	@Override
	public void execute() {
		
		//get input
		GTFeatureRelationCollection inRelations = (GTFeatureRelationCollection) getInput(IN_RELATIONS);
		IntegerLiteral inThreshold = (IntegerLiteral) getInput(IN_THRESHOLD);
		
		//set defaults
		int iThreshold = inThreshold.getValue();
		
		GTFeatureRelationCollection outRelations = countSimilarityMeasures(inRelations, iThreshold);
			
		//return
		setOutput(OUT_RELATIONS, outRelations);
		
	}
	
	private GTFeatureRelationCollection countSimilarityMeasures(GTFeatureRelationCollection relations, int iThreshold){
		
		GTFeatureRelationCollection outRelations = new GTFeatureRelationCollection();
		for(IFeatureRelation relation : relations){
			int count = countSimilarityMeasurements(relation);
			if(count >= iThreshold){
				relation.addRelationMeasurement(
						new ConfidenceMeasurement( 
								new IntegerLiteral(count),
								this.getMeasurementDescription(new RelationType(new IRI(CONFIDENCE_SIMILARITY_COUNT)))
						)	
				);
				outRelations.addRelation(relation);
			}
		}
		return outRelations;
	}
	
	private int countSimilarityMeasurements(IFeatureRelation relation){
		int i = 0;
		for(IRelationMeasurement measurement : relation.getMeasurements()){
			if(measurement instanceof SimilarityMeasurement)
				i++;
		}
		return i;
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
		return "Calculates similarity measurements for input relations, deletes relations with count < threshold";
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		Collection<IIODescription> inputs = new ArrayList<IIODescription>();
		inputs.add(new IODescription(
					new IRI(IN_THRESHOLD), "Count threshold for relations",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
						ERestrictions.BINDING_INTEGER.getRestriction()
					})
		);
		inputs.add(new IODescription(
					new IRI(IN_RELATIONS), "Input relations for which similarity measurements are counted",
					new IDataRestriction[]{
						ERestrictions.MANDATORY.getRestriction(),
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
					"Number of similarity measurements for this relation", 
					new RelationType(new IRI(CONFIDENCE_SIMILARITY_COUNT)),
					new MeasurementRange<Integer>(
							new IntegerLiteral[]{new IntegerLiteral(0), new IntegerLiteral(Integer.MAX_VALUE)},
							true
					))
		);
		return measurements;
	}	
}
