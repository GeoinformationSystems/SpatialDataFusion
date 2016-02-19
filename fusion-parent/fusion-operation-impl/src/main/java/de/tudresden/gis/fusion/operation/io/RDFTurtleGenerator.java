package de.tudresden.gis.fusion.operation.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.literal.URILiteral;
import de.tudresden.gis.fusion.data.rdf.ISubjectCollection;
import de.tudresden.gis.fusion.data.rdf.ISubject;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleEncoder;
import de.tudresden.gis.fusion.operation.AOperationInstance;
import de.tudresden.gis.fusion.operation.IGenerator;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
import de.tudresden.gis.fusion.operation.description.IInputDescription;
import de.tudresden.gis.fusion.operation.description.IOutputDescription;

public class RDFTurtleGenerator extends AOperationInstance implements IGenerator {
	
	private final String IN_RDF = "IN_RDF";
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	
	private final String OUT_RESOURCE = "OUT_RESOURCE";
	
	@Override
	public void execute() throws ProcessException {

		//get base and prefixes
		URI base = inputContainsKey(IN_URI_BASE) ? URI.create(((StringLiteral) input(IN_URI_BASE)).resolve()) : null;
		Map<URI,String> prefixes = new LinkedHashMap<URI,String>();
		if(inputContainsKey(IN_URI_PREFIXES)){
			String[] prefixesArray = ((StringLiteral) input(IN_URI_PREFIXES)).resolve().split(";");
			for(int i=0; i<prefixesArray.length; i+=2){
				prefixes.put(URI.create(prefixesArray[i]), prefixesArray[i+1]);
			}
		}
		
		//get input
		IData data = input(IN_RDF);
		File file = getFile();
		
		if(data instanceof ISubjectCollection || data instanceof ISubject)
			writeTriples(data, base, prefixes, file);
		else
			throw new ProcessException(ExceptionKey.INPUT_NOT_APPLICABLE, "cannot convert input to RDF");
		
		//return file
		setOutput(OUT_RESOURCE, new URILiteral(file.toURI()));
	}
	
	/**
	 * create temporary file
	 * @return temporary file
	 */
	private File getFile() {
		try {
			return File.createTempFile("relations_" + UUID.randomUUID(), ".rdf");
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.INPUT_NOT_ACCESSIBLE, "Could not create file");
		}
	}
	
	/**
	 * write RDF representation
	 * @param writer target buffer
	 * @param rdf RDF representation
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param file output file
	 */
	private void writeTriples(IData rdf, URI base, Map<URI,String> prefixes, File file) {
		
		BufferedWriter writer = null;
		try {
			//create writer
			writer = new BufferedWriter(new FileWriter(file));
		
			if(base != null)
				writer.write("@base <" + base + "> .\n");
			if(prefixes != null && prefixes.size() > 0){
				for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
					writer.write("@prefix " + prefix.getValue() + ": <" + prefix.getKey() + "> .\n");
				}
			}
			if(rdf instanceof ISubjectCollection){
				List<String> rdfInserts = RDFTurtleEncoder.encodeTripleResource((ISubjectCollection) rdf, base, prefixes, 1000);
				for(String insert : rdfInserts){
					writer.append(insert);
				}
			}
			else
				writer.append(RDFTurtleEncoder.encodeTripleResource((ISubject) rdf, base, prefixes));
			
		} catch (IOException e){
			throw new ProcessException(ExceptionKey.INPUT_NOT_ACCESSIBLE, "Could not establish file writer", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new ProcessException(ExceptionKey.INPUT_NOT_ACCESSIBLE, "Could not close file writer", e);
			}
		}	
	}
	
	@Override
	public String getProcessIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getProcessTitle() {
		return "RDF Turtle generator";
	}

	@Override
	public String getTextualProcessDescription() {
		return "Generator for W3C RDF Turtle format";
	}

	@Override
	public Collection<IProcessConstraint> getProcessConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IInputDescription> getInputDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IOutputDescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
