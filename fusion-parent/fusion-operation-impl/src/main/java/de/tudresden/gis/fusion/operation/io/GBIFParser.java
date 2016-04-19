package de.tudresden.gis.fusion.operation.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.tudresden.gis.fusion.data.description.MeasurementDescription;
import de.tudresden.gis.fusion.data.feature.IObservation;
import de.tudresden.gis.fusion.data.literal.TemporalMeasurement;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.observation.ObservationCollection;
import de.tudresden.gis.fusion.data.observation.SpeciesEntity;
import de.tudresden.gis.fusion.data.observation.SpeciesMeasurement;
import de.tudresden.gis.fusion.data.observation.SpeciesObservation;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IParser;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class GBIFParser extends AOperationInstance implements IParser {
	
	private final String PROPERTY_ID = "gbifID";
	private final String PROPERTY_COUNT = "individualCount";
	private final String PROPERTY_GEOGR_LAT = "decimalLatitude";
	private final String PROPERTY_GEOGR_LON = "decimalLongitude";
	private final String PROPERTY_DAY = "day";
	private final String PROPERTY_MONTH = "month";
	private final String PROPERTY_YEAR = "year";
	private final String PROPERTY_SCIENTIFIC_NAME = "scientificName";
	
	private final String IN_RESOURCE = "IN_RESOURCE";	
	private final String OUT_OBSERVATIONS = "OUT_OBSERVATIONS";
	
	private Map<String,Integer> index;
	private String separator = "\t";
	private GeometryFactory geometryFactory = new GeometryFactory();
	
	private final MeasurementDescription description = new MeasurementDescription(
			RDFVocabulary.MEASUREMENT_DESCRIPTION.asString(), 
			"Species occurrence", 
			"Occurrence of a species from GBIF observation", 
			SpeciesMeasurement.positiveRange(), 
			RDFVocabulary.UOM_NUMBER.asResource());

	@Override
	public void execute() throws ProcessException {

		URILiteral gbifResource = (URILiteral) input(IN_RESOURCE);
		
		ObservationCollection observations = null;

		URL url;
		try {
			url = gbifResource.resolve().toURL();
		} catch (MalformedURLException e) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "GBIF source is no valid URL");
		}		
		//parse file
		if(url.getProtocol().toLowerCase().startsWith("file"))
			observations = new ObservationCollection(gbifResource.getValue(), parseGBIF(url), null);
		else
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Unsupported GBIF source");
        
		setOutput(OUT_OBSERVATIONS, observations);
	}
	
	private Collection<IObservation> parseGBIF(URL url) {
		File file = new File(url.getFile());
		if(!file.exists() || file.isDirectory())
			return null;
		try {
			return parseGBIF(file);			
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.PROCESS_EXCEPTION, "Could not parse GBIF observations", e);
		}
	}

	private Collection<IObservation> parseGBIF(File file) throws IOException {
		Collection<IObservation> observations = new HashSet<IObservation>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		boolean header = true;
		while ((line = reader.readLine()) != null) {
			if(header){
				initIndexMap(line);
				header = false;
				continue;
			}
			else
				observations.add(parseGBIFObservation(line));
		}
		reader.close();
		return observations;
	}

	private void initIndexMap(String line) throws IOException {
		index = new HashMap<String,Integer>();
		String[] properties = splitLine(line);
		for(int i=0; i<properties.length; i++){
			index.put(properties[i], i);
		}
		//validate
		if(!index.containsKey(PROPERTY_ID) || 
				!index.containsKey(PROPERTY_DAY) ||
				!index.containsKey(PROPERTY_MONTH) ||
				!index.containsKey(PROPERTY_YEAR) ||
				!index.containsKey(PROPERTY_COUNT) ||
				!index.containsKey(PROPERTY_GEOGR_LAT) ||
				!index.containsKey(PROPERTY_GEOGR_LON) ||
				!index.containsKey(PROPERTY_SCIENTIFIC_NAME))
			throw new IOException("Mandatory element is missing from GBIF");
	}

	private SpeciesObservation parseGBIFObservation(String line) {
		String[] values = splitLine(line);
		//set observation properties
		String identifier = values[index.get(PROPERTY_ID)];
		SpeciesEntity entity = new SpeciesEntity(identifier);
		SpeciesMeasurement measurement = new SpeciesMeasurement(
				new IdentifiableResource(values[index.get(PROPERTY_SCIENTIFIC_NAME)]), 
				getPoint(values),
				getCount(values[index.get(PROPERTY_COUNT)]),
				description);
		TemporalMeasurement time = getTime(values);
		//create observation
		return new SpeciesObservation(identifier, entity, measurement, time);
		
	}
	
	private TemporalMeasurement getTime(String[] values){
		if(values[index.get(PROPERTY_YEAR)].isEmpty() || values[index.get(PROPERTY_MONTH)].isEmpty() || values[index.get(PROPERTY_DAY)].isEmpty())
			return null;
		return new TemporalMeasurement(LocalDateTime.of(
				Integer.valueOf(values[index.get(PROPERTY_YEAR)]), 
				Integer.valueOf(values[index.get(PROPERTY_MONTH)]), 
				Integer.valueOf(values[index.get(PROPERTY_DAY)]), 
				0, 
				0), null);
	}
	
	private Point getPoint(String[] values){
		if(values[index.get(PROPERTY_GEOGR_LON)].isEmpty() || values[index.get(PROPERTY_GEOGR_LAT)].isEmpty())
			return null;
		return geometryFactory.createPoint(new Coordinate(
				Double.valueOf(values[index.get(PROPERTY_GEOGR_LON)]),
				Double.valueOf(values[index.get(PROPERTY_GEOGR_LAT)])));
	}
	
	private int getCount(String count){
		if(count == null || count.isEmpty())
			return 1;
		else
			return Integer.valueOf(count);
	}
	
	private String[] splitLine(String line){
		return line.split(separator);
	}

	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "GBIF observation parser";
	}

	@Override
	public String getTextualProcessDescription() {
		// TODO Auto-generated method stub
		return "Parser for GBIF species observation format";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<IInputDescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
