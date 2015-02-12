package de.tudresden.gis.fusion.operation.harmonization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class CRSReproject extends AbstractOperation {
	
	private final String IN_REFERENCE = "IN_REFERENCE";
	private final String IN_TARGET = "IN_TARGET";
	private final String IN_REFERENCE_CRS = "IN_REFERENCE_CRS";
	private final String IN_TARGET_CRS = "IN_TARGET_CRS";
	private final String IN_CRS = "IN_CRS";
	private final String OUT_REFERENCE = "OUT_REFERENCE";
	private final String OUT_TARGET = "OUT_TARGET";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#CRSReproject";
	
	@Override
	public void execute() throws ProcessException {

		//get inputs
		IFeatureCollection inReference = (IFeatureCollection) getInput(IN_REFERENCE);
		IResource inReferenceCRS = (IResource) getInput(IN_REFERENCE_CRS);
		IFeatureCollection inTarget = (IFeatureCollection) getInput(IN_TARGET);
		IResource inTargetCRS = (IResource) getInput(IN_TARGET_CRS);
		IResource inCRS = (IResource) getInput(IN_CRS);
		
		//get reference and target crs
		CoordinateReferenceSystem crsReference = inReferenceCRS == null ? 
				getCRSFromIRI(inReference.getSpatialProperty().getSRSName()) : 
				getCRSFromIRI(inReferenceCRS.getIdentifier());
		CoordinateReferenceSystem crsTarget = inTargetCRS == null ? 
				getCRSFromIRI(inTarget.getSpatialProperty().getSRSName()) : 
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
	
	private CoordinateReferenceSystem getCRSFromIRI(IIRI iri){
		try {
			return CRS.decode(iri.asString());
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
		return new GTFeatureCollection(inFeature.getIdentifier(), features_proj, inFeature.getDescription());
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
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Reprojects coordinate reference system of input features";
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
