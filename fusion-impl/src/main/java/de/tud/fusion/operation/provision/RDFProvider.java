package de.tud.fusion.operation.provision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.literal.BooleanLiteral;
import de.tud.fusion.data.literal.StringLiteral;
import de.tud.fusion.data.literal.URILiteral;
import de.tud.fusion.data.rdf.IGraph;
import de.tud.fusion.data.rdf.ILiteral;
import de.tud.fusion.data.rdf.INode;
import de.tud.fusion.data.rdf.IPlainLiteral;
import de.tud.fusion.data.rdf.IResource;
import de.tud.fusion.data.rdf.ISubject;
import de.tud.fusion.data.rdf.ITypedLiteral;
import de.tud.fusion.operation.AbstractOperation;
import de.tud.fusion.operation.constraint.BindingConstraint;
import de.tud.fusion.operation.constraint.MandatoryConstraint;
import de.tud.fusion.operation.constraint.PatternConstraint;
import de.tud.fusion.operation.description.IDataConstraint;
import de.tud.fusion.operation.description.IInputConnector;
import de.tud.fusion.operation.description.IOutputConnector;
import de.tud.fusion.operation.description.InputConnector;
import de.tud.fusion.operation.description.OutputConnector;
import de.tud.fusion.operation.retrieval.GMLParser;

public class RDFProvider extends AbstractOperation {

	public final static String PROCESS_ID = GMLParser.class.getSimpleName();
	
	private final String IN_RDF = "IN_RDF";
	private final String IN_URI_BASE = "IN_URI_BASE";
	private final String IN_URI_PREFIXES = "IN_URI_PREFIXES";
	private final String IN_TRIPLE_STORE = "IN_TRIPLE_STORE";
	private final String IN_CLEAR_STORE = "IN_CLEAR_STORE";
	
	private final String OUT_RESOURCE = "OUT_RESOURCE";
		
	private Set<IInputConnector> inputConnectors;
	private Set<IOutputConnector> outputConnectors;
	
	public RDFProvider() {
		super(PROCESS_ID);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector rdfConnector = getInputConnector(IN_RDF);
		IInputConnector baseConnector = getInputConnector(IN_URI_BASE);
		IInputConnector prefixConnector = getInputConnector(IN_URI_PREFIXES);
		IInputConnector storeConnector = getInputConnector(IN_TRIPLE_STORE);
		IInputConnector clearConnector = getInputConnector(IN_CLEAR_STORE);
		//get data
		ISubject rdfData = (ISubject) ((IData) rdfConnector.getData()).resolve();
		URI base = ((URILiteral) baseConnector.getData()).resolve();
		Map<URI,String> prefixes = parsePrefixes((StringLiteral) prefixConnector.getData());
		//init result
		URILiteral rdfResource;
		//check for triple store URI
		if(storeConnector.isConnected()){
			try {
				rdfResource = writeRDFToStore(rdfData, base, prefixes);
			} catch (IOException e) {
				throw new RuntimeException("Could not access or write to triple store", e);
			}
		}
		//else: provide RDF file
		else {
			try {
				rdfResource = generateRDFFile(rdfData, base, prefixes);
			} catch (IOException e) {
				throw new RuntimeException("Could not write RDF file", e);
			}
		}
		//set output connector
		setOutputConnector(OUT_RESOURCE, rdfResource);
	}
	
	/**
	 * get RDF prefixes
	 * @param data input prefix string
	 * @return prefix map
	 */
	private Map<URI, String> parsePrefixes(StringLiteral data) {
		Map<URI,String> prefixes = new HashMap<URI,String>();
		if(data != null && !data.resolve().isEmpty()){
			String[] prefixesArray = data.resolve().split(";");
			for(String prefix : prefixesArray){
				prefixes.put(URI.create(prefix.split(",")[0]), prefix.split(",")[1]);
			}
		}
		return prefixes;
	}
	
	private URILiteral writeRDFToStore(ISubject rdf, URI base, Map<URI,String> prefixes) throws IOException {
		
	}

	/**
	 * generate RDF file
	 * @param rdfData input subjects
	 * @param base base URL
	 * @param prefixes prefix URLs
	 * @return URL literal instance pointing to file
	 * @throws IOException
	 */
	private URILiteral generateRDFFile(ISubject rdfData, URI base, Map<URI, String> prefixes) throws IOException {
		//init file
		File file = File.createTempFile("relations_" + UUID.randomUUID(), ".rdf");
		//write RDF turtles
		writeTriplesToFile(rdfData, base, prefixes, file);
		//return
		return new URILiteral(file.toURI());
	}

	/**
	 * write RDF data to file
	 * @param rdfData input subjects
	 * @param base base URL
	 * @param prefixes prefix URLs
	 * @param file output file
	 * @throws IOException 
	 */
	private void writeTriplesToFile(ISubject rdfData, URI base, Map<URI, String> prefixes, File file) throws IOException {
		//create file writer
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			//write base and prefixes
			if(base != null)
				writer.write("@base <" + base + "> .\n");
			if(prefixes != null && prefixes.size() > 0){
				for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
					writer.write("@prefix " + prefix.getValue() + ": <" + prefix.getKey() + "> .\n");
				}
			}
			//write graph data
			if(rdfData instanceof IGraph){
				List<String> rdfInserts = encodeTripleResource((IGraph) rdfData, base, prefixes, 1000);
				for(String insert : rdfInserts){
					writer.append(insert);
				}
			}
			else
				writer.append(encodeTripleResource((ISubject) rdfData, base, prefixes));
		} finally {
			writer.close();
		}
	}
	
	/**
	 * encode RDF representation collection
	 * @param collection RDF representation collection
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param chunkSize number of RDF representations in one String
	 * @return list of encoded RDF representation Strings
	 */
	public static List<String> encodeTripleResource(IGraph collection, URI base, Map<URI,String> prefixes, int chunkSize) {
		List<String> sList = new ArrayList<String>();
		StringBuilder sTriple = new StringBuilder();
		StringBuilder sTripleRoot = new StringBuilder();
		//iterate collection
		int i = 0;
		for(ISubject rdf : collection.getSubjects()){
			i++;
			sTriple.append(encodeTripleResource(rdf, base, prefixes, "", true, true, sTripleRoot));
			//cut relation string by chunk size
			if(i % chunkSize == 0){
				sList.add(sTriple.append(sTripleRoot).toString());
				sTriple.setLength(0);
				sTripleRoot.setLength(0);
			}
		}
		//add remaining relations
		if(sTriple.length() > 0)
			sList.add(sTriple.append(sTripleRoot).toString());
		//return list
		return sList;
	}
	
	/**
	 * encode RDF triple representation
	 * @param rdf RDF triple representation
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded RDF triple representation String
	 */
	public static String encodeTripleResource(ISubject rdf, URI base, Map<URI,String> prefixes) {
		StringBuilder sTripleRoot = new StringBuilder();
		String sTriple = encodeTripleResource(rdf, base, prefixes, "", true, true, sTripleRoot);
		return sTripleRoot.append(sTriple).toString();
	}
	
	/**
	 * encode RDF triple representation
	 * @param rdf RDF triple representation
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param indent styling indent
	 * @param writeSubject set true to include encoded subject node
	 * @param close set true to close the set (. instead of ;)
	 * @return encoded RDF triple representation String
	 */
	public static String encodeTripleResource(ISubject rdf, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//if subject is not defined, do nothing
		if(rdf == null)
			return null;
		//write triple set
		else if(rdf instanceof ISubject){
			sTriple.append(encodeSubject((ISubject) rdf, base, prefixes, indent, writeSubject, close, sTripleRoot));
		}
		//write resource, if objects are not defined
		else
			sTriple.append(encodeResource(rdf, base, prefixes));
		//return
		return sTriple.toString();
	}

	/**
	 * encode RDF triple set
	 * @param tripleSet RDF triple set
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param indent styling indent
	 * @param writeSubject set true to include encoded subject node
	 * @param close set true to close the set (. instead of ;)
	 * @return encoded RDF triple set String
	 */
	public static String encodeSubject(ISubject subject, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//check number of predicates
		if(subject.getPredicates().size() == 1){
			IResource predicate = subject.getPredicates().iterator().next();
			sTriple.append(indent + encodeResource(subject, base, prefixes) + " ");
			//write predicate
			sTriple.append(encodeResource(predicate, base, prefixes));
			//write object
			sTriple.append(encodeObject(subject.getObjects(predicate).iterator().next(), base, prefixes, indent, close, sTripleRoot));
		}
		else {
			//write subject, if requested
			if(writeSubject)
				sTriple.append(indent + encodeResource(subject, base, prefixes) + "\n");
			//increase indent
			indent += "\t";
			//iterate predicates in triple set
			int i = subject.getPredicates().size();
			for(IResource predicate : (subject).getPredicates()){
				Collection<INode> objects = subject.getObjects(predicate);
				//continue if no objects are related to predicate
				if(objects == null || objects.isEmpty())
					continue;
				//iterate objects for predicate
				for(INode object : objects){
					//write predicate
					sTriple.append(indent + encodeResource(predicate, base, prefixes));
					//add object (close if last object in object set)
					i--;
					boolean lastObject = (i == 0);
					boolean setClose = (close && lastObject);
					sTriple.append(encodeObject(object, base, prefixes, indent, setClose, sTripleRoot));
				}
			}
		}
		//return
		return sTriple.toString();
	}
	
	public static String encodeObject(INode object, URI base, Map<URI,String> prefixes, String indent, boolean close, StringBuilder sTripleRoot){
		StringBuilder sTriple = new StringBuilder();
		//encode subject
		if(object instanceof ISubject){
			if(((ISubject) object).getPredicates().size() > 0){
				if(((IResource) object).isBlank()){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((ISubject) object, base, prefixes, indent, false, false, sTripleRoot));
					sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
				}
				else {
					sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
					sTripleRoot.append(encodeTripleResource((ISubject) object, base, prefixes, "", true, true, sTripleRoot));
				}
			}
			else
				sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + " ;\n");		
		}
		//encode literal
		else if(object instanceof ILiteral)
			sTriple.append(" " + encodeLiteral((ILiteral) object, base, prefixes) + (close ? " ." : " ;") + "\n");
		//encode identifiable resource
		else if(object instanceof IResource){
			sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
		}
		//encode resource if not blank
		else if(object instanceof IResource && !((IResource) object).isBlank()){
			sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
		}
		else
			//this should not happen
			sTriple.append(" NoIdentifiableRDFNode" + (close ? " ." : " ;") + "\n");
		
		return sTriple.toString();
	}

	/**
	 * encode RDF resource
	 * @param resource RDF resource
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded resource String
	 */
	public static String encodeResource(IResource resource, URI base, Map<URI,String> prefixes){
		if(resource.isBlank())
			//TODO: implement blank id
			return "_:" + resource.getClass().getSimpleName() + "_" + UUID.randomUUID();
		else {
			URI uriPrefix = relativizeIdentifier(resource, base, prefixes);
			//write full resource, if it has not been relativized
			if(resource.getURI().equals(uriPrefix))
				return "<" + resource.getIdentifier().toString() + ">";
			else
				return uriPrefix.toString();
		}
	}
	
	/**
	 * encode RDF resource
	 * @param resource RDF resource
	 * @param prefixes RDF prefixes
	 * @return encoded resource String
	 */
	public static String encodeResource(IResource resource, Map<URI,String> prefixes){
		return encodeResource(resource, null, prefixes);
	}
	
	/**
	 * encode RDF typed literal
	 * @param literal RDF typed literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeTypedLiteral(ITypedLiteral literal, URI base, Map<URI,String> prefixes){
		IResource resource = literal.getType();
		URI relative = relativizeIdentifier(resource, base, prefixes);
		return "\"" + literal.getValue() + "\"^^" + (resource.getIdentifier().equals(relative) ? "<" + resource.getIdentifier() + ">" : relative);
	}
	
	/**
	 * encode RDF typed literal
	 * @param literal RDF typed literal
	 * @return encoded literal String
	 */
	public static String encodeTypedLiteral(ITypedLiteral literal, Map<URI,String> prefixes){
		return encodeTypedLiteral(literal, null, prefixes);
	}
	
	/**
	 * encode RDF plain literal
	 * @param literal RDF plain literal
	 * @return encoded literal String
	 */
	public static String encodePlainLiteral(IPlainLiteral literal){
		return "\"" + literal.getValue() + "\"@" + literal.getLanguage();
	}
	
	/**
	 * encode RDF literal
	 * @param literal RDF literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeLiteral(ILiteral literal, URI base, Map<URI,String> prefixes){
		if(literal instanceof ITypedLiteral)
			return encodeTypedLiteral((ITypedLiteral) literal, base, prefixes);
		else if(literal instanceof IPlainLiteral)
			return encodePlainLiteral((IPlainLiteral) literal);
		else return null;
	}
	
	/**
	 * encode RDF literal
	 * @param literal RDF literal
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeLiteral(ILiteral literal, Map<URI,String> prefixes){
		return encodeLiteral(literal, null, prefixes);
	}
	
	/**
	 * relativize a resource identifier
	 * @param resource input resource
	 * @param base URI base
	 * @param prefixes URI prefixes
	 * @return relativized resource identifier
	 */
	public static URI relativizeIdentifier(IResource resource, URI base, Map<URI,String> prefixes) {
		URI relative = relativizeIdentifier(resource, base);
		if(resource.getURI().equals(relative) && prefixes != null){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				relative = relativizeIdentifier(resource, prefix.getKey(), prefix.getValue());
				if(!resource.getURI().equals(relative))
					return relative;
			}
		}
		return relative;
	}
	
	/**
	 * relativize an resource identifier
	 * @param resource input resource
	 * @param base URI base
	 * @param prefixes URI prefix
	 * @return relativized resource identifier
	 */
	public static URI relativizeIdentifier(IResource resource, URI uri, String prefix) {
		//special case: http://www.w3.org/1999/02/22-rdf-syntax-ns#type --> a
		if(resource.getIdentifier().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
			return URI.create("a");
		//default
		URI relative = relativizeIdentifier(resource, uri);
		if(resource.getURI().equals(relative) || relative.toString().matches(".*((\\/)|(.+#)).*"))
			return resource.getURI();
		else
			return URI.create((prefix == null || prefix.length() == 0 ? "" : prefix + ":") + 
					(relative.toString().startsWith("#") ? relative.toString().substring(1) : relative.toString()));
	}

	/**
	 * relativize a resource identifier
	 * @param resource input resource
	 * @param base URI base
	 * @return relativized resource identifier
	 */
	public static URI relativizeIdentifier(IResource resource, URI base) {
		return relativizeIdentifier(resource, base, "");
	}

	@Override
	public Set<IInputConnector> getInputConnectors() {
		if(inputConnectors != null)
			return inputConnectors;
		//generate descriptions
		inputConnectors = new HashSet<IInputConnector>();
		inputConnectors.add(new InputConnector(
				IN_RDF, IN_RDF, "Input RDF triples",
				new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(new Class<?>[]{ISubject.class,IData.class})},
				null,
				null));
		inputConnectors.add(new InputConnector(
				IN_URI_BASE, IN_URI_BASE, "RDF Base URI",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class)},
				null,
				null));
		inputConnectors.add(new InputConnector(
				IN_URI_PREFIXES, IN_URI_PREFIXES, "RDF Prefixes (schema,prefix;schema,prefix...)",
				new IDataConstraint[]{
						new BindingConstraint(StringLiteral.class),
						new PatternConstraint("^(" + URILiteral.getURIRegex() + ",([a-z]+);)+$")},
				null,
				null));
		inputConnectors.add(new InputConnector(
				IN_TRIPLE_STORE, IN_TRIPLE_STORE, "Triple Store URI",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class)},
				null,
				null));
		inputConnectors.add(new InputConnector(
				IN_CLEAR_STORE, IN_CLEAR_STORE, "Flag: Clear triple store",
				new IDataConstraint[]{
						new BindingConstraint(BooleanLiteral.class)},
				null,
				new BooleanLiteral(false)));
		//return
		return inputConnectors;
	}

	@Override
	public Set<IOutputConnector> getOutputConnectors() {
		if(outputConnectors != null)
			return outputConnectors;
		//generate descriptions
		outputConnectors = new HashSet<IOutputConnector>();
		outputConnectors.add(new OutputConnector(
				OUT_RESOURCE, OUT_RESOURCE, "Link to RDF encoded file or SPARQL Endpoint",
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class),
						new MandatoryConstraint()},
				null));		
		//return
		return outputConnectors;
	}

	@Override
	public String getProcessTitle() {
		return "Triple generator";
	}

	@Override
	public String getProcessAbstract() {
		return "Generator for W3C RDF format";
	}
	
}
