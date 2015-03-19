package de.tudresden.gis.fusion.operation.harmonization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IFeature;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTFeature;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class CRSReproject extends AOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_REFERENCE_CRS = "IN_REFERENCE_CRS";
	private final String IN_TARGET_CRS = "IN_TARGET_CRS";
	private final String IN_CRS = "IN_CRS";
	private final String OUT_REFERENCE = "OUT_REFERENCE";
	private final String OUT_TARGET = "OUT_TARGET";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.HARMONIZATION.resource(),
			EProcessType.OP_HAR_CRS.resource()
	};
	
	@Override
	public void execute() throws ProcessException {

		//get inputs
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		URILiteral inReferenceCRS = (URILiteral) getInput(IN_REFERENCE_CRS);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		URILiteral inTargetCRS = (URILiteral) getInput(IN_TARGET_CRS);
		URILiteral inCRS = (URILiteral) getInput(IN_CRS);
		
		//get reference and target crs
		CoordinateReferenceSystem crsReference = inReferenceCRS == null ? 
				getCRSFromIRI(inReference.getSpatialProperty().getSRS().getIdentifier().toString()) : 
				getCRSFromIRI(inReferenceCRS.getIdentifier());
		CoordinateReferenceSystem crsTarget = inTargetCRS == null ? 
				getCRSFromIRI(inTarget.getSpatialProperty().getSRS().getIdentifier().toString()) : 
				getCRSFromIRI(inTargetCRS.getIdentifier());
		
		//get final crs
		CoordinateReferenceSystem crsFinal = inCRS == null ? crsReference : getCRSFromIRI(inCRS.getIdentifier());
		
		//transform
		IFeatureCollection outReference = reproject(inReference, crsReference, crsFinal);
		IFeatureCollection outTarget = reproject(inTarget, crsTarget, crsFinal);
		outReference = inReference;
		
		//return
		setOutput(OUT_REFERENCE, outReference);
		setOutput(OUT_TARGET, outTarget);
		
	}
	
	private CoordinateReferenceSystem getCRSFromIRI(String identifier){
		try {
			return CRS.decode(identifier);
		} catch (FactoryException e1) {
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "input crs cannot be detected");
		}
	}
	
	/**
	 * set or transform crs for simple feature collection
	 * @param fc features
	 * @return transformed features
	 * @throws ProcessException 
	 * @throws TransformException 
	 * @throws FactoryException 
	 * @throws MismatchedDimensionException 
	 * @throws IOException
	 */
	private IFeatureCollection reproject(IFeatureCollection inFeature, CoordinateReferenceSystem referenceCRS, CoordinateReferenceSystem targteCRS) throws ProcessException {
		
		//check if feature colletion is set
		if(inFeature == null || inFeature.size() == 0)
			return null;
		
		//check if transformation is required/applicable
		if(referenceCRS == null || targteCRS == null || referenceCRS.equals(targteCRS))
			return inFeature;
		
		//init new collection
		List<SimpleFeature> features_proj = new ArrayList<SimpleFeature>();
				
		//get transformation
		MathTransform transformation = getTransformation(referenceCRS, targteCRS);
		
		//iterate collection and transform
	    for(IFeature feature : inFeature) {
	    	if(feature instanceof GTFeature)
	    		features_proj.add(reproject(((GTFeature) feature).getFeature(), targteCRS, transformation));
		}
	    
		//return
		return new GTFeatureCollection(new IRI(inFeature.getCollectionId()), features_proj, inFeature.getDescription());
	}

	/**
	 * set or transform crs for simple feature
	 * @param fc feature
	 * @return transformed feature
	 * @throws ProcessException 
	 * @throws IOException
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
	 * @throws ProcessException 
	 * @throws IOException
	 */
	private Geometry reprojectGeometry(Geometry geom, MathTransform transformation) {
		if(geom == null) return null;
		try {
			return JTS.transform(geom, transformation);
		} catch (MismatchedDimensionException mde) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, mde);
		} catch (TransformException te){
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, te);
		}
	}
	
	/**
	 * get transformation
	 * @param sourceCRS input crs
	 * @param targetCRS output crs
	 * @return transformation
	 * @throws ProcessException 
	 * @throws IOException
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
				throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
			}			
		}	
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
		return "Reprojects coordinate reference system of input features";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_REFERENCE, "Reference features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_TARGET, "Target features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_REFERENCE_CRS, "CRS of reference features",
					new IIORestriction[]{
							ERestrictions.BINDING_URIRESOURCE.getRestriction()
					}
			),
			new IODescription(
					IN_TARGET_CRS, "CRS of target feature",
					new IIORestriction[]{
							ERestrictions.BINDING_URIRESOURCE.getRestriction()
					}
			),
			new IODescription(
					IN_CRS, "Target CRS, if not set the crs of the reference features is set as target",
					new IIORestriction[]{
							ERestrictions.BINDING_URIRESOURCE.getRestriction()
					}
		)};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_REFERENCE, "Reference features",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
					}
			),
			new IODescription(
					OUT_TARGET, "Target features",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
					}
			)
		};
	}
}
