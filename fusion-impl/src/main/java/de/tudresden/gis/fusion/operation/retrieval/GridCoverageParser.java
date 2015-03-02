package de.tudresden.gis.fusion.operation.retrieval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.gdal.GDALCoverageReference;
import de.tudresden.gis.fusion.data.geotools.GTGridCoverage2D;
import de.tudresden.gis.fusion.data.rdf.IIRI;
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
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;

public class GridCoverageParser extends AOperation implements IDataRetrieval {

	public static final String IN_RESOURCE = "IN_RESOURCE";
	
	public static final String OUT_COVERAGE = "OUT_COVERAGE";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};
	
	@Override
	public void execute() {
		
		URILiteral coverageResource = (URILiteral) getInput(IN_RESOURCE);
		
		IIRI identifier = new IRI(coverageResource.getIdentifier());
		InputStream stream;
		File tmpCoverage;
		ICoverage coverage = null;
		
		try {
			
			tmpCoverage = File.createTempFile("tmpCoverage" + UUID.randomUUID(), ".tmp");
			stream = identifier.asURL().openStream();
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
			coverage = new GTGridCoverage2D(identifier, tmpCoverage);
		} catch (Exception e1) {
			try {
				coverage = new GDALCoverageReference(identifier, tmpCoverage);
			} catch (Exception e2){
				throw new ProcessException(ExceptionKey.NO_APPLICABLE_INPUT, "Input coverage is not supported.");
			}
		}

		//set output
		setOutput(OUT_COVERAGE, coverage);
		
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessDescription() {
		return "Parser for Coverages";
	}
	
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
				new IODescription(
					IN_RESOURCE, "Coverage resource",
					new IIORestriction[]{
						ERestrictions.BINDING_URIRESOURCE.getRestriction(),
						ERestrictions.MANDATORY.getRestriction()
					}
				),
		};			
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription (
					OUT_COVERAGE, "Output coverage",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_ICOVERAGE.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	public void setFilter(IFilter filter) {
		// TODO Auto-generated method stub
		
	}

}