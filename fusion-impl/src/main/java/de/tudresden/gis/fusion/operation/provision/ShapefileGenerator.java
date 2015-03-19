package de.tudresden.gis.fusion.operation.provision;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class ShapefileGenerator extends AOperation implements IDataProvision {

	private final String IN_FEATURES = "IN_FEATURES";	
	private final String OUT_RESOURCE = "OUT_RESOURCE";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.PROVISION.resource()
	};
	
	@Override
	public void execute() {
		
		//get input features
		IFeatureCollection features = (IFeatureCollection) getInput(IN_FEATURES);
		
		//write file
		URILiteral shape;
		try {
			shape = writeShapefile(features);
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
		
		//return file
		setOutput(OUT_RESOURCE, shape);
		
	}
	
	private URILiteral writeShapefile(IFeatureCollection features) throws IOException {
		
		SimpleFeatureCollection collection = DataUtilities.getGTFeatureCollection(features);
		if(collection == null)
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
		
		File file = DataUtilities.createTmpFile("shape_" + System.currentTimeMillis(), ".shp");
		
		ShapefileDataStore shapeDataStore = new ShapefileDataStore(file.toURI().toURL());
        shapeDataStore.createSchema(collection.getSchema());

        shapeDataStore.createSchema(collection.features().next().getFeatureType());
        FeatureWriter<SimpleFeatureType, SimpleFeature> fw = shapeDataStore.getFeatureWriter(shapeDataStore.getTypeNames()[0],
                Transaction.AUTO_COMMIT);
        SimpleFeatureIterator it = collection.features();
        while (it.hasNext()) {
            SimpleFeature feature = it.next();
            SimpleFeature newFeature = fw.next();
            
            newFeature.setAttributes(feature.getAttributes());
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            newFeature.setDefaultGeometry(geometry);

            fw.write();
        }
        fw.close();
        
        shapeDataStore.dispose();

        return new URILiteral(file.toURI());
      
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
		return "Generator for ESRI Shapefiles";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_FEATURES, "Input features",
					new IIORestriction[]{
							ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			)
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_RESOURCE, "Output shapefile",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_URIRESOURCE.getRestriction()
					}
			)
		};
	}

}
