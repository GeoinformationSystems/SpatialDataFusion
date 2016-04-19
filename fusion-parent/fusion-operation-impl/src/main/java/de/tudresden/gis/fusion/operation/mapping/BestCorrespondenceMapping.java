package de.tudresden.gis.fusion.operation.mapping;

import java.util.Collection;
import java.util.HashSet;
import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IFeature;
import de.tudresden.gis.fusion.data.feature.relation.IFeatureRelation;
import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class BestCorrespondenceMapping extends AOperationInstance {
	
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String OUT_RELATIONS = "OUT_RELATIONS";
	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
	
	private boolean bDropRelations;
	
	private MeasurementDescription measurementDescription = new MeasurementDescription(
			RDFVocabulary.TYPE_MEAS_CONF_BEST.asString(),
			"best correspondence",
			"relation with best correspondence",
			BooleanLiteral.maxRange(),
			RDFVocabulary.UOM_UNDEFINED.asResource());

	@Override
	public void execute() throws ProcessException {
		
		//get input
		FeatureRelationCollection relations = (FeatureRelationCollection) input(IN_RELATIONS);
		bDropRelations = inputContainsKey(IN_DROP_RELATIONS) ? ((BooleanLiteral) input(IN_DROP_RELATIONS)).resolve() : false;
		
		//add best correspondence relations
		addBestCorrespondences(relations);
		
		//create new list, if IN_DROP_RELATIONS == true
		if(bDropRelations)
			relations = filterRelations(relations);
		
		//set output
		setOutput(OUT_RELATIONS, relations);
	}
	
	private FeatureRelationCollection filterRelations(FeatureRelationCollection relations) {
		
		FeatureRelationCollection nRelations = new FeatureRelationCollection();
		for(IFeatureRelation relation : relations){
			nRelations.add(relation);
		}
		return nRelations;
		
	}

	/**
	 * adds best correspondences
	 * @param relations input relations
	 */
	private void addBestCorrespondences(FeatureRelationCollection relations) {
		
		//get unique reference feature ids
		Collection<IFeature> sourceColl = relations.getSourceFeatures();
		
		//get all relations for source feature
		for(IFeature source : sourceColl){
			Collection<IFeatureRelation> targetRelations = relations.getRelations(source);
			addBestCorrespondences(source, targetRelations);
		}
		
	}

	/**
	 * add best correspondences
	 * @param source source feature
	 * @param targets related target features
	 */
	private void addBestCorrespondences(IFeature source, Collection<IFeatureRelation> relations) {
		
		if(relations.size() == 0)
			return;
		
		//select best correspondence if targets.size == 1
		if(relations.size() == 1){
			addBestCorrespondence(relations.iterator().next());
			return;
		}
		
		//get target with most relation measurements
		Collection<IFeatureRelation> maxMeasurements = new HashSet<IFeatureRelation>();
		int max = 0;
		for(IFeatureRelation relation : relations){
			//add relation, if number of measurements equals current max
			if(relation.getRelationMeasurements().size() == max)
				maxMeasurements.add(relation);
			//replace relation, if number of measurements is higher than current max
			if(relation.getRelationMeasurements().size() > max){
				maxMeasurements.clear();
				maxMeasurements.add(relation);
			}
		}
		if(maxMeasurements.size() == 0)
			return;
		if(maxMeasurements.size() == 1){
			addBestCorrespondence(maxMeasurements.iterator().next());
			return;
		}
		
		//select best correspondence from set of relation measurements
		selectBestCorrespondence(maxMeasurements);
		
	}

	/**
	 * select best correspondence from set of measurements
	 * @param maxMeasurements input measurements
	 */
	private void selectBestCorrespondence(Collection<IFeatureRelation> maxMeasurements) {
		
		//TODO: implement real selection
		for(IFeatureRelation relation : maxMeasurements){
			addBestCorrespondence(relation);
		}
		
	}

	/**
	 * add best correspondence measurement to relation
	 * @param relation input relation
	 */
	private void addBestCorrespondence(IFeatureRelation relation) {
		relation.addMeasurement(new RelationMeasurement(
				RDFVocabulary.FEATURE_REPRESENTATION.asResource(),
				RDFVocabulary.FEATURE_REPRESENTATION.asResource(),
				new BooleanLiteral(true), 
				measurementDescription));		
	}

	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Mapping of Best Correspondences";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Filters a set of feature relations in order to create best correspondences";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
