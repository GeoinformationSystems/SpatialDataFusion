package de.tudresden.gis.fusion.operation.geotools.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.geotools.GTGridCoverage;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class GridCoverageParser extends AOperationInstance implements IParser {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	
	private final String OUT_COVERAGE = "OUT_COVERAGE";
	
	@Override
	public void execute() throws ProcessException {
		
		URILiteral coverageResource = (URILiteral) input(IN_RESOURCE);
		
		InputStream stream;
		File tmpCoverage;
		GTGridCoverage coverage = null;
		
		try {
			tmpCoverage = File.createTempFile("tmpCoverage" + UUID.randomUUID(), ".tmp");
			stream = coverageResource.value().toURL().openStream();
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
			throw new ProcessException(ExceptionKey.INPUT_NOT_ACCESSIBLE, "Cannot read coverage file");
		}
		
		try {
			coverage = new GTGridCoverage(new IRI(coverageResource.value().toString()), tmpCoverage);
		} catch (Exception e1) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Input coverage is not supported");
		}
        
		setOutput(OUT_COVERAGE, coverage);
	}
	
	@Override
	public IRI processIdentifier() {
		return new IRI(this.getClass().getSimpleName());
	}

	@Override
	public String processTitle() {
		return "Coverage parser";
	}

	@Override
	public String processAbstract() {
		return "Parser for grid coverage format";
	}

	@Override
	public Collection<IProcessConstraint> processConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> inputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> outputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
