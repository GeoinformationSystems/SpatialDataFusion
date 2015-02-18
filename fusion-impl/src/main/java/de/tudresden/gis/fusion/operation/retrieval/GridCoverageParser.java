package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.gdal.GDALCoverageReference;
import de.tudresden.gis.fusion.data.geotools.GTGridCoverage2D;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class GridCoverageParser  extends AbstractOperation implements IDataRetrieval {

	public static final String IN_COVERAGE_RESOURCE = "IN_COVERAGE_RESOURCE";
	public static final String OUT_COVERAGE = "OUT_COVERAGE";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#GDALCoverageParser";
	
	@Override
	protected void execute() {
		
		IDataResource coverageResource = (IDataResource) getInput(IN_COVERAGE_RESOURCE);
		
		InputStream stream;
		File tmpCoverage;
		ICoverage coverage = null;
		
		try {
			
			tmpCoverage = File.createTempFile("tmpCoverage" + UUID.randomUUID(), ".tmp");
			stream = coverageResource.getIdentifier().asURI().toURL().openStream();
			FileOutputStream outputStream = new FileOutputStream(tmpCoverage);
			byte buf[] = new byte[4096];
			int len;
			while ((len = stream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();
			outputStream.close();
			stream.close();
			
		} catch (IOException e){
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		}
		
		try {
			coverage = new GTGridCoverage2D(coverageResource.getIdentifier(), tmpCoverage);
		} catch (Exception e1) {
			try {
				coverage = new GDALCoverageReference(coverageResource.getIdentifier(), tmpCoverage);
			} catch (Exception e2){
				throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Input coverage is not supported.");
			}
		}

		//set output
		setOutput(OUT_COVERAGE, coverage);
		
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
	
	@Override
	public void setFilter(IFilter filter) {
		// TODO Auto-generated method stub
		
	}

}