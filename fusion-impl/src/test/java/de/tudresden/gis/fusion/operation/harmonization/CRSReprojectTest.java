package de.tudresden.gis.fusion.operation.harmonization;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.FactoryException;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.harmonization.CRSReproject;
import de.tudresden.gis.fusion.operation.retrieval.ShapefileParser;

public class CRSReprojectTest {

	@Test
	public void reproject() throws ProcessException, FactoryException {
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/OSM", "roads.shp").toURI())));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_highDensity.shp").toURI())));
		output = parser.execute(input);		
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		CRSReproject process = new CRSReproject();
		input.put("IN_REFERENCE", reference);
		input.put("IN_REFERENCE_CRS", new Resource(new IRI("CRS:84")));
		input.put("IN_TARGET", target);
		input.put("IN_TARGET_CRS", new Resource(new IRI("http://www.opengis.net/def/crs/EPSG/0/31465")));
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_TARGET"));
		Assert.assertTrue(output.get("OUT_TARGET") instanceof GTFeatureCollection);
		
		GTFeatureCollection reprojected = (GTFeatureCollection) output.get("OUT_TARGET");
		
		Assert.assertTrue(reprojected.size() == target.size());
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"target features reprojected: " + reprojected.size() + "\n\t" + 
				"new target feature bounds: " + boundsToString(reprojected.getSpatialProperty().getBounds()) + "\n\t" +
				"new target feature crs: EPSG:" + CRS.lookupEpsgCode(CRS.decode(reprojected.getSpatialProperty().getSRSName().asString()), true) + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
	@Test
	public void reprojectWithCRS() throws ProcessException, FactoryException {
		
		ShapefileParser parser = new ShapefileParser();
		
		Map<String,IData> input = new HashMap<String,IData>();
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/fusion_test", "atkis_highDensity.shp").toURI())));
		Map<String,IData> output = parser.execute(input);		
		GTFeatureCollection reference = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		input.put("IN_SHAPE_RESOURCE", new Resource(new IRI(new File("D:/GIS/Programmierung/testdata/OSM", "roads.shp").toURI())));
		output = parser.execute(input);		
		GTFeatureCollection target = (GTFeatureCollection) output.get("OUT_FEATURES");
		
		CRSReproject process = new CRSReproject();
		input.put("IN_REFERENCE", reference);
		input.put("IN_REFERENCE_CRS", new Resource(new IRI("http://www.opengis.net/def/crs/EPSG/0/31465")));
		input.put("IN_TARGET", target);
		input.put("IN_TARGET_CRS", new Resource(new IRI("CRS:84")));
		input.put("IN_CRS", new Resource(new IRI("http://www.opengis.net/def/crs/EPSG/0/4326")));
		output = process.execute(input);
		
		Assert.assertNotNull(output);
		Assert.assertTrue(output.containsKey("OUT_TARGET"));
		Assert.assertTrue(output.get("OUT_TARGET") instanceof GTFeatureCollection);
		
		GTFeatureCollection reprojReference = (GTFeatureCollection) output.get("OUT_REFERENCE");
		GTFeatureCollection reprojTarget = (GTFeatureCollection) output.get("OUT_TARGET");
		
		Assert.assertTrue(reprojReference.size() == reference.size());
		Assert.assertTrue(reprojTarget.size() == target.size());
		
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		System.out.print("executing " + process.getProcessIRI().asString() + "\n\t" +
				"reference features reprojected: " + reprojReference.size() + "\n\t" + 
				"new reference feature bounds: " + boundsToString(reprojReference.getSpatialProperty().getBounds()) + "\n\t" +
				"new reference feature crs: EPSG:" + CRS.lookupEpsgCode(CRS.decode(reprojReference.getSpatialProperty().getSRSName().asString()), true) + "\n\t" +
				"target features reprojected: " + reprojTarget.size() + "\n\t" +
				"new target feature bounds: " + boundsToString(reprojTarget.getSpatialProperty().getBounds()) + "\n\t" +
				"new target feature crs: EPSG:" + CRS.lookupEpsgCode(CRS.decode(reprojTarget.getSpatialProperty().getSRSName().asString()), true) + "\n\t" +
				"process runtime (ms): " + ((LongLiteral) process.getOutput("OUT_RUNTIME")).getValue() + "\n\t" +
				"memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
	}
	
	private String boundsToString(double[] bounds) {
		return "[" + bounds[0] + "," + bounds[1] + " ; " + bounds[2] + "," + bounds[3] + "]";
	}
}
