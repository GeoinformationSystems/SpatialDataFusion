package de.tudresden.gis.fusion.data.gdal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.feature.IFeatureProperty;
import de.tudresden.gis.fusion.data.feature.ISpatialProperty;
import de.tudresden.gis.fusion.data.feature.ITemporalProperty;
import de.tudresden.gis.fusion.data.feature.IThematicProperty;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.metadata.data.IFeatureDescription;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;

public class GDALCoverageReference extends Resource implements ICoverage {
	
	File file;
	
	public GDALCoverageReference(IIRI iri, File file) throws IOException {
		super(iri);
		if(!isSupported(file))
			throw new IOException("Format is not supported by GDAL");
	}
	
	@Override
	public String getFeatureId() {
		return getIdentifier().asString();
	}
	
	/**
	 * check if file is supported by GDAL installation
	 * @param file input coverage
	 * @return true, if file is supported (gdalinfo returned exit code 0)
	 */
	private boolean isSupported(File file){		
		boolean supported = false;
		try {			
			String[] command = new String[]{"cmd", "/c", "C:/Program Files/GDAL/gdalinfo.exe", file.getAbsolutePath()};			
//			ProcessBuilder builder = new ProcessBuilder(command).inheritIO();
			ProcessBuilder builder = new ProcessBuilder(command);
			Process p = builder.start();
			p.waitFor();
			supported = p.exitValue() == 0 ? true : false;
			p.destroy();			
		} catch (Exception e){
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
		return supported;
	}
	
	public File getFile(){
		return file;
	}

	@Override
	public IFeatureDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IThematicProperty> getThematicProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("thematic properties cannot be resolved");
	}

	@Override
	public Collection<ITemporalProperty> getTemporalProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("temporal properties cannot be resolved");
	}

	@Override
	public Collection<ISpatialProperty> getSpatialProperties() {
		//TODO: get properties
		throw new UnsupportedOperationException("spatial properties cannot be resolved");
	}

	@Override
	public ISpatialProperty getDefaultSpatialProperty() {
		//TODO: get properties
		throw new UnsupportedOperationException("default spatial property cannot be resolved");
	}

	@Override
	public IFeatureProperty getFeatureProperty(String identifier) {
		//TODO: get properties
		throw new UnsupportedOperationException("feature properties cannot be resolved");
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
