package de.tudresden.gis.fusion.operation.harmonization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.geotools.data.DataUtilities;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.feature.geotools.GTFeature;
import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class CRSReproject extends AOperationInstance {
	
	private final String IN_SOURCE = "IN_SOURCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_CRS = "IN_CRS";
	
	private final String OUT_SOURCE = "OUT_SOURCE";
	private final String OUT_TARGET = "OUT_TARGET";
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	@Override
	public void execute() throws ProcessException {
		
		//get input
		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
		GTFeatureCollection inTarget = inputContainsKey(IN_TARGET) ? (GTFeatureCollection) getInput(IN_TARGET) : null;
		
		//get source and target crs
		CoordinateReferenceSystem crsSource = inSource.collection().getSchema().getCoordinateReferenceSystem();
		CoordinateReferenceSystem crsTarget = inTarget != null ? inTarget.collection().getSchema().getCoordinateReferenceSystem() : null;
		
		//get final crs
		CoordinateReferenceSystem crsFinal;
		if(inputContainsKey(IN_CRS))
			crsFinal = decodeCRS(((URILiteral) getInput(IN_CRS)).resolve().toString());
		else if(crsTarget != null)
			crsFinal = crsTarget;
		else
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Could not determine target CRS");
		
		//transform
		GTFeatureCollection outSource = reproject(inSource, crsSource, crsFinal);
		GTFeatureCollection outTarget = crsTarget != null ? reproject(inTarget, crsTarget, crsFinal) : null;
		
		//return
		setOutput(OUT_SOURCE, outSource);
		if(outTarget != null)
			setOutput(OUT_TARGET, outTarget);
	}
	
	/**
	 * decode CRS String
	 * @param identifier CRS String
	 * @return decodes CRS
	 */
	private CoordinateReferenceSystem decodeCRS(String identifier){
		try {
			return CRS.decode(identifier);
		} catch (FactoryException e1) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "IN_CRS cannot be resolved");
		}
	}
	
	private GTFeatureCollection reproject(GTFeatureCollection features, CoordinateReferenceSystem featureCRS, CoordinateReferenceSystem finalCRS) throws ProcessException {
		
		//check if feature colletion is set
		if(features == null || features.size() == 0)
			return null;
		
		//check if transformation is required/applicable
		if(featureCRS == null || finalCRS == null || featureCRS.equals(finalCRS))
			return features;
		
		//init new collection
		List<SimpleFeature> features_proj = new ArrayList<SimpleFeature>();
				
		//get transformation
		MathTransform transformation = getTransformation(featureCRS, finalCRS);
		
		//iterate collection and transform
	    for(GTFeature feature : features) {
	    	features_proj.add(reproject((SimpleFeature) feature.resolve(), finalCRS, transformation));
		}
	    
		//return
		return new GTFeatureCollection(features.getIdentifier(), DataUtilities.collection(features_proj), features.getDescription());
	}

	/**
	 * set or transform crs for simple feature
	 * @param fc feature
	 * @return transformed feature
	 */
	private SimpleFeature reproject(SimpleFeature feature, CoordinateReferenceSystem targetCRS, MathTransform transformation) throws ProcessException {

		//get source CRS
		CoordinateReferenceSystem sourceCRS = feature.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
		
		//return feature if sourceCRS = targetCRS or one of the crs is null
		if(sourceCRS == null || targetCRS == null || sourceCRS.equals(targetCRS)) 
			return feature;
		
		//get transformation
		if(transformation == null)
			transformation = getTransformation(sourceCRS, targetCRS);
		
		//build new featuretype
		SimpleFeatureTypeBuilder ftBuilder= new SimpleFeatureTypeBuilder();	
		//get input type
		SimpleFeatureType inputType = feature.getFeatureType();
		//configure ft builder
		ftBuilder.setName(inputType.getName());
		for(AttributeDescriptor desc : inputType.getAttributeDescriptors()){
			//set crs for geometry types
			if(desc instanceof GeometryDescriptor){
				AttributeTypeBuilder aBuilder = new AttributeTypeBuilder();
				aBuilder.init(desc);
				aBuilder.setCRS(targetCRS);
				aBuilder.setBinding(desc.getType().getBinding());
				ftBuilder.add(aBuilder.buildDescriptor(desc.getName(), aBuilder.buildGeometryType()));
			}
			//add non-geometry attribute
			else
				ftBuilder.add(desc);
		}
		//set default geometry name
		ftBuilder.setDefaultGeometry(inputType.getGeometryDescriptor().getLocalName());
		
		//build new feature
		SimpleFeatureType newType = ftBuilder.buildFeatureType();
		SimpleFeatureBuilder fBuilder= new SimpleFeatureBuilder(newType);
		
		for(Property attribute : feature.getProperties()){
			//transform geometry attribute if required
			if(attribute instanceof GeometryAttribute){
				GeometryAttribute geomAttribute = (GeometryAttribute) attribute;
				Geometry geometrySource = (Geometry) geomAttribute.getValue();
				Geometry geometryTarget = reprojectGeometry(geometrySource, transformation);
				geomAttribute.setValue(geometryTarget);
				fBuilder.set(attribute.getName(), geomAttribute.getValue());
			}
			//add non-geometry feature attribute
			else
				fBuilder.set(attribute.getName(), attribute.getValue());
		}
		
		//return new feature
		return fBuilder.buildFeature(feature.getID());
	}
	
	/**
	 * transform geometry
	 * @param geom input geometry
	 * @param sourceCRS source crs
	 * @param targetCRS target crs
	 * @return transformed geometry
	 */
	private Geometry reprojectGeometry(Geometry geom, MathTransform transformation) {
		if(geom == null) return null;
		try {
			return JTS.transform(geom, transformation);
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not reproject geometry", e);
		}
	}
	
	/**
	 * get transformation
	 * @param sourceCRS input crs
	 * @param targetCRS output crs
	 * @return transformation
	 */
	private MathTransform getTransformation(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) throws ProcessException {
		try {
			return CRS.findMathTransform(sourceCRS, targetCRS);
		} catch (FactoryException fe) {
			try {
				sourceCRS = CRS.decode(CRS.lookupIdentifier(sourceCRS, true));
				targetCRS = CRS.decode(CRS.lookupIdentifier(targetCRS, true));
				return CRS.findMathTransform(sourceCRS, targetCRS);
			} catch (FactoryException e) {
				throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not find CRS transformation", e);
			}			
		}	
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "CRS reproject";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Reprojects coordinate reference system for input feature";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		if(inputDescriptions == null){
			inputDescriptions = new HashSet<IInputDescription>();
			inputDescriptions.add(new InputDescription(IN_SOURCE, IN_SOURCE, "Source features)",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_SOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_TARGET, IN_TARGET, "Target features)",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			inputDescriptions.add(new InputDescription(IN_CRS, IN_CRS, "Final coordinate reference system)",
					new IDataConstraint[]{
							ContraintFactory.getBindingConstraint(new Class<?>[]{URILiteral.class})
					}));
		}
		return inputDescriptions;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		if(outputDescriptions == null){
			outputDescriptions = new HashSet<IOutputDescription>();
			outputDescriptions.add(new OutputDescription(
					OUT_SOURCE, OUT_SOURCE, "Transformed source features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_SOURCE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
			outputDescriptions.add(new OutputDescription(
					OUT_TARGET, OUT_TARGET, "Transformed target features",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_TARGET),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
					}));
		}
		return outputDescriptions;
	}
}