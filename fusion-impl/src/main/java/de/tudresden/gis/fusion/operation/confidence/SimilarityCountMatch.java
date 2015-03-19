package de.tudresden.gis.fusion.operation.confidence;

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
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EMeasurementType;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.ConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IConfidenceMeasurementDescription;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.metadata.data.MeasurementRange;
import de.tudresden.gis.fusion.operation.AConfidenceMeasurementOperation;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class SimilarityCountMatch extends AConfidenceMeasurementOperation {

	//process definitions
	private final String IN_THRESHOLD = "IN_THRESHOLD";
	private final String IN_RELATIONS = "IN_RELATIONS";
	
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RELATION.resource(),
	};
	
	private final IIRI MEASUREMENT_ID = new IRI(Namespace.uri_measurement() + "/" + this.getProcessTitle());
	private final String MEASUREMENT_DESC = "Count of relation measurements";
	private final IIdentifiableResource[] MEASUREMENT_CLASSIFICATION = new IIdentifiableResource[]{
			EMeasurementType.SUM.resource()
	};
	
	int iThreshold;
		
	@Override
	public void execute() {
		
		//get input
		GTFeatureRelationCollection inRelations = (GTFeatureRelationCollection) getInput(IN_RELATIONS);
		iThreshold = ((IntegerLiteral) getInput(IN_THRESHOLD)).getValue();
		
		GTFeatureRelationCollection outRelations = countSimilarityMeasures(inRelations);
			
		//return
		setOutput(OUT_RELATIONS, outRelations);
		
	}
	
	private GTFeatureRelationCollection countSimilarityMeasures(GTFeatureRelationCollection relations){
		
		GTFeatureRelationCollection outRelations = new GTFeatureRelationCollection();
		for(IFeatureRelation relation : relations){
			int iCount = countSimilarityMeasurements(relation);
			if(iCount >= iThreshold){
				relation.addRelationMeasurement(
					new ConfidenceMeasurement( 
						new IntegerLiteral(iCount),
						this.PROCESS_RESOURCE,
						this.getMeasurementDescription(MEASUREMENT_ID)
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
		return "Calculates similarity measurements for input relations, deletes relations with count < threshold";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_THRESHOLD, "Count threshold for relations, if count is lower than threshold the relation is dropped",
					new IntegerLiteral(0),
					new IIORestriction[]{
						ERestrictions.BINDING_INTEGER.getRestriction()
					}
			),
			new IODescription(
				IN_RELATIONS, "Input relations for which similarity measurements are counted",
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
				new MeasurementRange<Integer>(
					new IntegerLiteral[]{new IntegerLiteral(0), new IntegerLiteral(Integer.MAX_VALUE)}, 
					true
				),
				DataUtilities.toSet(MEASUREMENT_CLASSIFICATION)
			)
		};
	}	
}
