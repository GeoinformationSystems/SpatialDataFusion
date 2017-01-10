package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTGridFeature;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GridCoverageParserTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

    private final static IIdentifier OUT_COVERAGE = new Identifier("OUT_COVERAGE");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readGeoTIFF() {
		readCoverage(new URILiteral(new File("D:/Geodaten/Testdaten/tif/dem.tif").toURI()));
	}

	private void readCoverage(URILiteral resource) {

		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, resource);

		GridCoverageParser parser = new GridCoverageParser();
		Map<IIdentifier,IData> output = parser.execute(input);

		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey(OUT_COVERAGE));
		Assert.assertTrue(output.get(OUT_COVERAGE) instanceof GTGridFeature);

		GTGridFeature grid = (GTGridFeature) output.get(OUT_COVERAGE);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"bounds: " + grid.getRepresentation().getBounds() + "\n\t" +
				"feature crs: " + (grid.resolve().getCoordinateReferenceSystem() != null ? grid.resolve().getCoordinateReferenceSystem().getName() : "not set") + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
