package de.tudresden.gis.fusion.operation.resolving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelation;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.IMeasurementValue;
import de.tudresden.gis.fusion.data.IRelationType;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class AttributeTransfer extends AbstractOperation {
	
	//process definitions
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_TARGET_ATT = "IN_TARGET_ATT";
	private final String IN_RELATIONS = "IN_RELATIONS";
	private final String IN_TARGET_RELATION = "IN_TARGET_RELATION";
	
	private final String OUT_REFERENCE = "OUT_REFERENCE";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#AttributeTransfer";
	
	private final String FLAG_NEW = "_new";

	@Override
	public void execute() {
		
		//get input
		GTFeatureCollection inReference = (GTFeatureCollection) getInput(IN_REFERENCE);
		GTFeatureCollection inTarget = (GTFeatureCollection) getInput(IN_TARGET);
		StringLiteral inTargetAtt = (StringLiteral) getInput(IN_TARGET_ATT);
		IRelationType inTargetRel = (IRelationType) getInput(IN_TARGET_RELATION);
		IFeatureRelationCollection inRelations = (IFeatureRelationCollection) getInput(IN_RELATIONS);
		
		//set defaults
		List<String> attributeSet;
		//get all attriubtes of first feature (assumes same schema for all features)
		if(inTargetAtt == null){
			attributeSet = new ArrayList<String>();
			Collection<IThematicProperty> properties = inTarget.iterator().next().getThematicProperties();
			for(IThematicProperty property : properties){
				attributeSet.add(property.getIdentifier());
			}
		}
		else if(inTargetAtt.getIdentifier().contains("["))
			attributeSet = Arrays.asList(DataUtilities.disassembleJSONArray(inTargetAtt.getIdentifier()));
		else
			attributeSet = Arrays.asList(new String[]{inTargetAtt.getIdentifier()});
		
		//execute
		IFeatureCollection outReference = transferAttribute(inReference, inTarget, attributeSet, inRelations, inTargetRel);
			
		//return
		setOutput(OUT_REFERENCE, outReference);
		
	}
	
	private IFeatureCollection transferAttribute(GTFeatureCollection inReference, GTFeatureCollection inTarget, List<String> attributeSet, IFeatureRelationCollection inRelations, IRelationType inTargetRel) {
		//init feature list
    	List<SimpleFeature> outTargetList = new ArrayList<SimpleFeature>();
    	//get GT SimpleFeatureCollections
    	SimpleFeatureCollection gtReference = inReference.getSimpleFeatureCollection();
    	SimpleFeatureCollection gtTarget = inTarget.getSimpleFeatureCollection();
    	//get new feature type
    	SimpleFeatureType newFType = buildNewFT(gtReference.getSchema(), gtTarget.getSchema(), attributeSet);
    	//iterate collection and add attributes
    	for(IFeature feature : inReference) {
        	//get related features
    		IFeature matchingTarget = getRelatedFeature(feature, inRelations, inTargetRel);
    		matchingTarget = inTarget.getFeatureById(matchingTarget.getIdentifier());
    		//build new feature
    		SimpleFeature newFeature = buildFeature(((GTFeature) feature).getFeature(), newFType, (GTFeature) matchingTarget, attributeSet);
    		//add feature to new collection
    		outTargetList.add(newFeature);
		}
    	//return
    	return new GTFeatureCollection(inReference.getIdentifier(), outTargetList);
	}

	/**
	 * build new feature type
	 * @param ftReference reference feature type
	 * @param ftTarget target feature type
	 * @param aAttributes attributes to be transferred from target to reference
	 * @return new feature type (reference + added attributes from target)
	 */
    private SimpleFeatureType buildNewFT(SimpleFeatureType ftReference, SimpleFeatureType ftTarget, List<String> attributeSet){
    	//get ft builder
		SimpleFeatureTypeBuilder ftBuilder= new SimpleFeatureTypeBuilder();
		//set name and default geometry name
		ftBuilder.setName(ftReference.getName());
		ftBuilder.setDefaultGeometry(ftReference.getGeometryDescriptor().getLocalName());
		//copy input type
		ftBuilder.addAll(ftReference.getAttributeDescriptors());
		//add new attribute properties
		for(String att : attributeSet){
			AttributeDescriptor dAtt = ftTarget.getDescriptor(att);
			if(dAtt == null) continue;
			else {
				if(ftReference.getDescriptor(att) != null)
					dAtt = changeATName(dAtt, dAtt.getLocalName() + FLAG_NEW);
				ftBuilder.add(dAtt);
			}
		}
		//return
		return ftBuilder.buildFeatureType();
    }

    /**
     * build new attribute descriptor with changed name
     * @param dAtt input descriptor
     * @param name new name
     * @return attribute descriptor with new name
     */
    private AttributeDescriptor changeATName(AttributeDescriptor dAtt, String name) {
    	AttributeTypeBuilder atBuilder = new AttributeTypeBuilder();
    	atBuilder.init(dAtt);
    	return atBuilder.buildDescriptor(name);
	}

	/**
     * get related feature for reference
     * @param feature reference feature
     * @param inRelations input relations
     * @param inTargetRel target relation type
     * @return best matching target feature
     */
	private IFeature getRelatedFeature(IFeature feature, IFeatureRelationCollection inRelations, IRelationType inTargetRel) {
		//get feature id
    	String sID = feature.getIdentifier().asString();
    	//get relations with corresponding reference id and specified relation type
    	List<IFeatureRelation> matchingTarget = new ArrayList<IFeatureRelation>();
    	for(IFeatureRelation relation : inRelations){
    		if(relation.getReference().getIdentifier().asString().endsWith(sID) && relation.containsRelationType(inTargetRel))
    			matchingTarget.add(relation);
    	}
    	if(matchingTarget.size() == 0) return null;
    	if(matchingTarget.size() == 1) return matchingTarget.get(0).getTarget();
    	if(matchingTarget.size() > 1) return getBestMatch(matchingTarget, inTargetRel);
    	return null;
	}
	
	/**
	 * get best matching candidate from relation collection
	 * @param <T>
	 * @param matchingTarget input relations
	 * @param inTargetRel target relation type to compare
	 * @return best matching target feature
	 */
	private <T> IFeature getBestMatch(List<IFeatureRelation> matchingTarget, IRelationType inTargetRel){
		IFeatureRelation rBest = null;
		for(IFeatureRelation rCurrent : matchingTarget){
			if(rBest == null) rBest = rCurrent;
			else {
				@SuppressWarnings("unchecked")
				IMeasurementValue<T> mCurrent = (IMeasurementValue<T>) rCurrent.getMeasurement(inTargetRel).getMeasurementValue();
				@SuppressWarnings("unchecked")
				IMeasurementValue<T> mBest = (IMeasurementValue<T>) rBest.getMeasurement(inTargetRel).getMeasurementValue();
				if(mCurrent.compareTo(mBest) > 0)
					rBest = rCurrent;
			}
		}
		return rBest.getTarget();
	}

	/**
	 * build new feature
	 * @param feature reference feature
	 * @param newFType new feature type for reference
	 * @param matchingTarget matching target feature
	 * @param aAttributes attributes that shall be transferred
	 * @return refernece feature following the new feature type with transferred attributes
	 */
	private SimpleFeature buildFeature(SimpleFeature feature, SimpleFeatureType newFType, GTFeature matchingTarget, List<String> attributeSet) {
		//get feature builder
    	SimpleFeatureBuilder fBuilder= new SimpleFeatureBuilder(newFType);
    	//copy reference feature
    	fBuilder.init(feature);
    	//add target attributes
    	for(String identifier : attributeSet){
    		String identifier_final = identifier;
    		if(feature.getAttribute(identifier) != null)
    			identifier_final += FLAG_NEW;
    		//set attribute
    		if(matchingTarget != null && matchingTarget.getFeatureProperty(identifier) != null){
    			fBuilder.set(identifier_final, matchingTarget.getFeatureProperty(identifier).getValue());
    		}
    		else
    			fBuilder.set(identifier_final, null);
		}
		//return new feature
		return fBuilder.buildFeature(feature.getID());
	}

	@Override
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProcessDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
