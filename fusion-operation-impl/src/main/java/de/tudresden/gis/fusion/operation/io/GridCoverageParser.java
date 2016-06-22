package de.tudresden.gis.fusion.operation.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import de.tudresden.gis.fusion.data.feature.geotools.GTGridCoverage;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;
import de.tudresden.gis.fusion.operation.description.InputDescription;
import de.tudresden.gis.fusion.operation.description.OutputDescription;

public class GridCoverageParser extends AOperationInstance implements IParser {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	
	private final String OUT_COVERAGE = "OUT_COVERAGE";
	
	private Collection<IInputDescription> inputDescriptions = null;
	private Collection<IOutputDescription> outputDescriptions = null;
	
	@Override
	public void execute() throws ProcessException {
		
		URILiteral coverageResource = (URILiteral) getInput(IN_RESOURCE);
		
		InputStream stream;
		File tmpCoverage;
		GTGridCoverage coverage = null;
		
		try {
			tmpCoverage = File.createTempFile("coverage" + UUID.randomUUID(), ".tmp");
			stream = coverageResource.resolve().toURL().openStream();
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
			throw new ProcessException(ExceptionKey.INPUT_NOT_ACCESSIBLE, "Cannot read coverage file: " + e.getLocalizedMessage());
		}
		
		try {
			coverage = new GTGridCoverage(coverageResource.resolve().toString(), tmpCoverage);
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "Input coverage is not supported: " + e.getLocalizedMessage());
		}
        
		setOutput(OUT_COVERAGE, coverage);
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "Coverage parser";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Parser for grid coverage format";
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
			inputDescriptions.add(new InputDescription(IN_RESOURCE, IN_RESOURCE, "Link to input coverage)",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(IN_RESOURCE),
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
					OUT_COVERAGE, OUT_COVERAGE, "Output coverage",
					new IDataConstraint[]{
							ContraintFactory.getMandatoryConstraint(OUT_COVERAGE),
							ContraintFactory.getBindingConstraint(new Class<?>[]{GTGridCoverage.class})
					}));
		}
		return outputDescriptions;
	}

}
