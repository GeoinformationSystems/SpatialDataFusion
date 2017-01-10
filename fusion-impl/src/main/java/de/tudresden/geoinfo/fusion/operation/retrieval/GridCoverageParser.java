package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GridCoverageParser extends AbstractOperation {

	private static final IIdentifier PROCESS = new Identifier(GridCoverageParser.class.getSimpleName());

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

	private final static IIdentifier OUT_COVERAGE = new Identifier("OUT_COVERAGE");

	/**
	 * constructor
	 */
	public GridCoverageParser() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector resourceConnector = getInputConnector(IN_RESOURCE);
		//get data
		URI resourceURI = ((URILiteral) resourceConnector.getData()).resolve();
		//parse coverage
		GTGridFeature coverage;
		try {
			coverage = parseCoverage(resourceURI.toURL());
		} catch (IOException e) {
			throw new RuntimeException("Could not parse coverage", e);
		}
		//set output connector
		connectOutput(OUT_COVERAGE, coverage);
	}

	/**
	 * parse coverage
	 * @param resourceURL coverage URL
	 * @return coverage
	 * @throws IOException if reading of the coverage fails
	 */
	private GTGridFeature parseCoverage(URL resourceURL) throws IOException {
		InputStream stream;
		File tmpCoverage;
		tmpCoverage = File.createTempFile("coverage" + UUID.randomUUID(), ".tmp");
		stream = resourceURL.openStream();
		FileOutputStream outputStream = new FileOutputStream(tmpCoverage);
		byte buf[] = new byte[4096];
		int len;
		while ((len = stream.read(buf)) > 0) {
			outputStream.write(buf, 0, len);
		}
		outputStream.flush();
		outputStream.close();
		stream.close();
		return new GTGridFeature(new Identifier(resourceURL.toString()), tmpCoverage, null, null);
	}

	@Override
	public Map<IIdentifier,IInputConnector> initInputConnectors() {
		Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
		inputConnectors.put(IN_RESOURCE, new InputConnector(
				IN_RESOURCE,
				new MetadataForConnector(IN_RESOURCE.toString(), "Link to input coverage"),
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null,
				null));
		return inputConnectors;
	}

	@Override
	public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
		Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
		outputConnectors.put(OUT_COVERAGE, new OutputConnector(
                OUT_COVERAGE,
				new MetadataForConnector(OUT_COVERAGE.toString(), "Output coverage"),
				new IDataConstraint[]{
						new BindingConstraint(GTGridFeature.class),
						new MandatoryConstraint()},
				null));
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "Coverage Parser";
	}

	@Override
	public String getProcessAbstract() {
		return "Parser for Coverage format";
	}

}
