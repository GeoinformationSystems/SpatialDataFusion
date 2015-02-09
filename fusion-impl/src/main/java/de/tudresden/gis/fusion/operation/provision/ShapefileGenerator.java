package de.tudresden.gis.fusion.operation.provision;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class ShapefileGenerator extends AbstractOperation implements IDataProvision {

	private final String IN_FEATURES = "IN_FEATURES";	
	private final String OUT_FILE = "OUT_FILE";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#ShapefileGenerator";
	
	@Override
	protected void execute() {
		
		//get input features
		IComplexData features = (IComplexData)getInput(IN_FEATURES);
		
		if(!(features instanceof IFeatureCollection))
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
		
		//write file
		IDataResource shape;
		try {
			shape = writeShapefile((IFeatureCollection) features);
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
		
		//return file
		setOutput(OUT_FILE, shape);
		
	}
	
	private IDataResource writeShapefile(IFeatureCollection features) throws IOException {
		
		SimpleFeatureCollection collection = DataUtilities.getGTFeatureCollection(features);
		if(collection == null)
			throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT);
		
		File file = getFile();
		
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

        return new Resource(new IRI(file.toURI()));
      
	}
	
	private File getFile() {
		try {
			return File.createTempFile("shape_" + System.currentTimeMillis(), ".shp");
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		}
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
