package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GMLParserTest {

    private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");

    private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
    private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

	@Test
	public void readGMLFile_V21() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs100.xml").toURI()));
	}

	@Test
	public void readGMLFile_V31() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs110.xml").toURI()));
	}

	@Test
	public void readGMLFile_V32() {
		readGML(new URILiteral(new File("D:/Geodaten/Testdaten/gml", "wfs20.xml").toURI()));
	}

	@Test
	public void readWFS11() {
		readGML(new URILiteral(URI.create("http://cobweb.gis.geo.tu-dresden.de/wfs?service=wfs&version=1.1.0&request=GetFeature&typename=sampleObs")));
	}

	private void readGML(URILiteral resource) {

		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, resource);

		GMLParser parser = new GMLParser();
		Map<IIdentifier,IData> output = parser.execute(input);

		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey(OUT_FEATURES));
		Assert.assertTrue(output.get(OUT_FEATURES) instanceof GTFeatureCollection);

		GTFeatureCollection features = (GTFeatureCollection) output.get(OUT_FEATURES);
		Assert.assertTrue(features.size() > 0);

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"features read from gml: " + features.size() + "\n\t" +
				"bounds: " + features.getBounds() + "\n\t" +
				"feature crs: " + (features.getReferenceSystem() != null ? features.getReferenceSystem().getName() : "not set") + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}

}
