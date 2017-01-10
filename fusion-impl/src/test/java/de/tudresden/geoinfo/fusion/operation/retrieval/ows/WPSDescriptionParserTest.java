package de.tudresden.geoinfo.fusion.operation.retrieval.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescription;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WPSDescriptionParserTest {

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
	private final static IIdentifier OUT_DESCRIPTION = new Identifier("OUT_DESCRIPTION");
	private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readWPSDescription() {

		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?Request=describeprocess&Service=WPS&version=1.0.0&identifier=de.tudresden.gis.fusion.algorithm.BoundingBoxDistance")));

		WPSDescriptionParser parser = new WPSDescriptionParser();
		Map<IIdentifier,IData> output = parser.execute(input);

		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey(OUT_DESCRIPTION));
		Assert.assertTrue(output.get(OUT_DESCRIPTION) instanceof WPSProcessDescription);

		WPSProcessDescription description = (WPSProcessDescription) output.get(OUT_DESCRIPTION);
		Assert.assertTrue(description.getProcessIdentifier().size() == 1);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
