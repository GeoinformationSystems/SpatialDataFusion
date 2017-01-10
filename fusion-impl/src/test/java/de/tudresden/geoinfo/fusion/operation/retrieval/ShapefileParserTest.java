package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShapefileParserTest {

	private final static IIdentifier IN_RESOURCE = new Identifier("IN_RESOURCE");
	private final static IIdentifier IN_WITH_INDEX = new Identifier("IN_WITH_INDEX");

	private final static IIdentifier OUT_FEATURES = new Identifier("OUT_FEATURES");
	private final static IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");
	
	@Test
	public void readShapefile() {
		readShapefile(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()), false);
	}

	@Test
	public void readShapefileWithIndex() {
		readShapefile(new URILiteral(new File("D:/Geodaten/Testdaten/shape", "atkis_dd.shp").toURI()), true);
	}

	private void readShapefile(URILiteral resource, boolean index) {
		
		Map<IIdentifier,IData> input = new HashMap<>();
		input.put(IN_RESOURCE, resource);
		input.put(IN_WITH_INDEX, new BooleanLiteral(index));
		
		ShapefileParser parser = new ShapefileParser();
		Map<IIdentifier,IData> output = parser.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey(OUT_FEATURES));
		if(index)
			Assert.assertTrue(output.get(OUT_FEATURES) instanceof GTIndexedFeatureCollection);
		else
			Assert.assertTrue(output.get(OUT_FEATURES) instanceof GTFeatureCollection);
		
		GTFeatureCollection collection = (GTFeatureCollection) output.get(OUT_FEATURES);
		
		Assert.assertTrue(collection.resolve().size() > 0);
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("TEST: " + parser.getIdentifier() + "\n\t" +
				"features read from Shapefile: " + collection.resolve().size() + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) output.get(OUT_RUNTIME)).resolve() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");	
	}

}
