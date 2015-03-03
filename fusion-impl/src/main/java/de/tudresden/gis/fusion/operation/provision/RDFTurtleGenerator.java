package de.tudresden.gis.fusion.operation.provision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.RDFBasicTurtleEncoder;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataProvision;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class RDFTurtleGenerator extends AOperation implements IDataProvision {
	
	private final String IN_RDF = "IN_RDF";
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	
	private final String OUT_RESOURCE = "OUT_RESOURCE";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.PROVISION.resource()
	};
	
	@Override
	public void execute() throws ProcessException {
		
		//get input relations and uri pattern
		IComplexData rdfData = (IComplexData)getInput(IN_RDF);
		StringLiteral uriBase = (StringLiteral) getInput(IN_URI_BASE);
		StringLiteral uriPrefixes = (StringLiteral) getInput(IN_URI_PREFIXES);
		
		//set prefixes
		Map<URI,String> prefixes = new LinkedHashMap<URI,String>();
		if(inputContainsKey(IN_URI_PREFIXES)){
			String[] prefixesArray = uriPrefixes.getIdentifier().split(";");
			for(int i=0; i<prefixesArray.length; i+=2){
				prefixes.put(URI.create(prefixesArray[i]), prefixesArray[i+1]);
			}
		}
		
		URI base = inputContainsKey(IN_URI_BASE) ? URI.create(uriBase.getIdentifier()) : null;
		
		//write file
		URILiteral rdf = writeRDF(rdfData.getRDFRepresentation(), base, prefixes);
		//return file
		setOutput(OUT_RESOURCE, rdf);
		
	}
	
	private URILiteral writeRDF(IRDFRepresentation rdf, URI base, Map<URI,String> prefixes) {
		//get output file
		File file = getFile();
		//write relations to file
		writeRelations(file, rdf, base, prefixes);
		//return file resource
		return new URILiteral(file.toURI());
	}

	private File getFile() {
		try {
			return File.createTempFile("relations_" + UUID.randomUUID(), ".rdf");
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.ACCESS_RESTRICTION, e);
		}
	}
	
	private void writeRelations(File file, IRDFRepresentation rdf, URI base, Map<URI,String> prefixes) {
		BufferedWriter writer = null;
		try {
			//create writer
			writer = new BufferedWriter(new FileWriter(file));
			//write relations
			writeTriples(writer, rdf, base, prefixes);
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
	
	private void writeTriples(BufferedWriter writer, IRDFRepresentation rdf, URI base, Map<URI,String> prefixes) throws IOException {
		if(base != null)
			writer.write("@base <" + base + "> .\n");
		if(prefixes != null && prefixes.size() > 0){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				writer.write("@prefix " + prefix.getValue() + ": <" + prefix.getKey() + "> .\n");
			}
		}
		if(rdf instanceof IRDFCollection){
			List<String> rdfInserts = RDFBasicTurtleEncoder.encodeTripleResource((IRDFCollection) rdf, base, prefixes, 1000);
			for(String insert : rdfInserts){
				writer.append(insert);
			}
		}
		else
			writer.append(RDFBasicTurtleEncoder.encodeTripleResource(rdf, base, prefixes));
		
	}

	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

	@Override
	protected String getProcessDescription() {
		return "RDF Turtle Generator for triples";
	}
	
	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					IN_RDF, "Input RDF data",
					new IIORestriction[]{
							ERestrictions.BINDING_ICOMPLEX.getRestriction(),
							ERestrictions.MANDATORY.getRestriction()
					}
			),
			new IODescription(
					IN_URI_BASE, "RDF URI base",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction()
					}
			),
			new IODescription(
					IN_URI_PREFIXES, "RDF URI prefixes (CSV formatted)",
					new IIORestriction[]{
							ERestrictions.BINDING_STRING.getRestriction()
					}
			)
		};
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
					OUT_RESOURCE, "Output RDF file",
					new IIORestriction[]{
							ERestrictions.MANDATORY.getRestriction(),
							ERestrictions.BINDING_URIRESOURCE.getRestriction()
					}
			)
		};
	}
	
}
