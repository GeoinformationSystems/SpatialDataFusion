package de.tudresden.gis.fusion.operation.provision;

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

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.IDataResource;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.RDFTurtleEncoder;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.AbstractOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class RDFTurtleGenerator extends AbstractOperation implements IDataProvision {
	
	private final String IN_DATA = "IN_DATA";
	private final String URI_BASE = "URI_BASE";
	private final String URI_PREFIXES = "URI_PREFIXES";
	
	private final String OUT_FILE = "OUT_FILE";
	
	private final String PROCESS_ID = "http://tu-dresden.de/uw/geo/gis/fusion/process/demo#RDFTurtleGenerator";
	
	@Override
	public void execute() throws ProcessException {
		
		//get input relations and uri pattern
		IComplexData tripleSet = (IComplexData)getInput(IN_DATA);
		Resource uriBase = (Resource) getInput(URI_BASE);
		StringLiteral uriPrefixes = (StringLiteral) getInput(URI_PREFIXES);
		
		//set prefixes
		Map<URI,String> prefixes = new LinkedHashMap<URI,String>();
		if(inputContainsKey(URI_PREFIXES)){
			String[] prefixesArray = uriPrefixes.getIdentifier().split(";");
			for(int i=0; i<prefixesArray.length; i+=2){
				prefixes.put(URI.create(prefixesArray[i]), prefixesArray[i+1]);
			}
		}
		
		URI base = inputContainsKey(URI_BASE) ? uriBase.getIdentifier().asURI() : null;
		
		//write file
		IDataResource rdf = writeRDF((IRDFTripleSet) tripleSet, base, prefixes);
		//return file
		setOutput(OUT_FILE, rdf);
		
	}
	
	private IDataResource writeRDF(IRDFTripleSet tripleSet, URI base, Map<URI,String> prefixes) {
		//get output file
		File file = getFile();
		//write relations to file
		writeRelations(file, tripleSet, base, prefixes);
		//return file resource
		return new Resource(new IRI(file.toURI()));
	}

	private File getFile() {
		try {
			return File.createTempFile("relations_" + UUID.randomUUID(), ".rdf");
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		}
	}
	
	private void writeRelations(File file, IRDFTripleSet tripleSet, URI base, Map<URI,String> prefixes) {
		BufferedWriter writer = null;
		try {
			//create writer
			writer = new BufferedWriter(new FileWriter(file));
			//write relations
			writeTriples(writer, tripleSet, base, prefixes);
		} catch (IOException e){
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
			}
		}
	}
	
	private void writeTriples(BufferedWriter writer, IRDFTripleSet tripleSet, URI base, Map<URI,String> prefixes) throws IOException {
		if(base != null)
			writer.write("@base <" + base + "> .\n");
		if(prefixes != null && prefixes.size() > 0){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				writer.write("@prefix " + prefix.getValue() + ": <" + prefix.getKey() + "> .\n");
			}
		}
		if(tripleSet instanceof IRDFCollection){
			List<String> rdfInserts = RDFTurtleEncoder.encodeTripleResource((IRDFCollection) tripleSet, base, prefixes, 1000);
			for(String insert : rdfInserts){
				writer.append(insert);
			}
		}
		else
			writer.append(RDFTurtleEncoder.encodeTripleResource(tripleSet, base, prefixes));
		
	}


	@Override
	protected IIRI getProcessIRI() {
		return new IRI(PROCESS_ID);
	}

	@Override
	protected String getProcessTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProcessDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getInputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<IIODescription> getOutputDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
