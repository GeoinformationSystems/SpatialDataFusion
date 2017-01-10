package de.tudresden.geoinfo.fusion.operation.provision;

import com.hp.hpl.jena.sparql.modify.request.Target;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IGraph;
import de.tudresden.geoinfo.fusion.data.ISubject;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.*;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.PatternConstraint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class RDFProvider extends AbstractOperation {

    private static final IIdentifier PROCESS = new Identifier(RDFProvider.class.getSimpleName());
	
	private final static IIdentifier IN_RDF = new Identifier("IN_RDF");
	private final static IIdentifier IN_URI_BASE = new Identifier("IN_URI_BASE");
	private final static IIdentifier IN_URI_PREFIXES = new Identifier("IN_URI_PREFIXES");
	private final static IIdentifier IN_TRIPLE_STORE = new Identifier("IN_TRIPLE_STORE");
	private final static IIdentifier IN_CLEAR_STORE = new Identifier("IN_CLEAR_STORE");
    private final static IIdentifier IN_TRIPLE_BAG_SIZE = new Identifier("IN_TRIPLE_BAG_SIZE");

	private final static IIdentifier OUT_RESOURCE = new Identifier("OUT_RESOURCE");
	
	public RDFProvider() {
		super(PROCESS);
	}
	
	@Override
	public void execute() {
		//get input connectors
		IInputConnector rdfConnector = getInputConnector(IN_RDF);
		IInputConnector baseConnector = getInputConnector(IN_URI_BASE);
		IInputConnector prefixConnector = getInputConnector(IN_URI_PREFIXES);
		IInputConnector storeConnector = getInputConnector(IN_TRIPLE_STORE);
		IInputConnector clearConnector = getInputConnector(IN_CLEAR_STORE);
        IInputConnector bagSizeConnector = getInputConnector(IN_TRIPLE_BAG_SIZE);
		//get data
		ISubject rdfData = (ISubject) (rdfConnector.getData()).resolve();
		URI base = ((URILiteral) baseConnector.getData()).resolve();
		Map<URI,String> prefixes = parsePrefixes((StringLiteral) prefixConnector.getData());
		//init result
		URILiteral rdfResource;
		//check for triple store URI
		if(storeConnector.isConnected()){
			try {
				URILiteral tripleStoreURI = (URILiteral) storeConnector.getData();
				BooleanLiteral clearStore = (BooleanLiteral) clearConnector.getData();
                IntegerLiteral bagSize = (IntegerLiteral) bagSizeConnector.getData();
				//add base prefix to prefixes
				if(baseConnector.isConnected())
					prefixes.put(base, "base");
				writeRDFToStore(tripleStoreURI.resolve(), rdfData, prefixes, clearStore.resolve(), bagSize.resolve());
				rdfResource = tripleStoreURI;
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
		connectOutput(OUT_RESOURCE, rdfResource);
	}
	
	/**
	 * get RDF prefixes
	 * @param data input prefix string
	 * @return prefix map
	 */
	private Map<URI, String> parsePrefixes(StringLiteral data) {
		Map<URI,String> prefixes = new HashMap<>();
		if(data != null && !data.resolve().isEmpty()){
			String[] prefixesArray = data.resolve().split(";");
			for(String prefix : prefixesArray){
				prefixes.put(URI.create(prefix.split(",")[0]), prefix.split(",")[1]);
			}
		}
		return prefixes;
	}

	/**
	 * write triples to triple store
     * @param tripleStore triple store URI
	 * @param rdfData input subjects
	 * @param prefixes prefix URLs
     * @param clearStore flag: clear triple store before insert
	 * @throws IOException if triple store update fails
	 */
	private void writeRDFToStore(URI tripleStore, ISubject rdfData, Map<URI,String> prefixes, boolean clearStore, int bagSize) throws IOException {
        if(clearStore)
            clearStore(tripleStore);
        updateStore(tripleStore, rdfData, prefixes, bagSize);
	}

	private void clearStore(URI tripleStore){
        UpdateRequest request = new UpdateRequest().add(new UpdateClear(Target.ALL));
        updateStore(tripleStore, request);
    }

    private void updateStore(URI tripleStore, UpdateRequest request){
        UpdateExecutionFactory.createRemote(request, tripleStore.toString()).execute();
    }

    private void updateStore(URI tripleStore, ISubject rdfData, Map<URI,String> prefixes, int bagSize) {
        StringBuilder sRequest = new StringBuilder();
        //insert data
        if(rdfData instanceof IGraph){
            List<String> rdfInserts = encodeTripleResource((IGraph) rdfData, null, prefixes, bagSize);
            for(String insert : rdfInserts){
                sRequest.append(getPrefixHeader(prefixes));
                sRequest.append("INSERT DATA {\n");
                sRequest.append(insert);
                sRequest.append("}");
                updateStore(tripleStore, new UpdateRequest().add(sRequest.toString()));
                sRequest.setLength(0);
            }
        }
        else if(rdfData != null){
            sRequest.append(getPrefixHeader(prefixes));
            sRequest.append("INSERT DATA {\n");
            sRequest.append(encodeTripleResource(rdfData, null, prefixes));
            sRequest.append("}");
            updateStore(tripleStore, new UpdateRequest().add(sRequest.toString()));
        }
    }

    private String getPrefixHeader(Map<URI,String> prefixes){
        StringBuilder sHeader = new StringBuilder();
        //append prefixes
        for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
            sHeader.append("PREFIX ").append(prefix.getValue()).append(": <").append(prefix.getKey()).append(">\n");
        }
        return sHeader.toString();
    }

	/**
	 * generate RDF file
	 * @param rdfData input subjects
	 * @param base base URL
	 * @param prefixes prefix URLs
	 * @return URL literal instance pointing to file
	 * @throws IOException if RDF file cannot be created
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
	 * @throws IOException if triples cannot be written to file
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
				List<String> rdfInserts = encodeTripleResource((IGraph<? extends ISubject>) rdfData, base, prefixes, 1000);
				for(String insert : rdfInserts){
					writer.append(insert);
				}
			}
			else
				writer.append(encodeTripleResource(rdfData, base, prefixes));
		} finally {
            if (writer != null) {
                writer.close();
            }
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
    private List<String> encodeTripleResource(IGraph<? extends ISubject> collection, URI base, Map<URI,String> prefixes, int chunkSize) {
		List<String> sList = new ArrayList<>();
		StringBuilder sTriple = new StringBuilder();
		StringBuilder sTripleRoot = new StringBuilder();
		//iterate collection
		int i = 0;
		for(ISubject rdf : collection.resolve()){
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
    private String encodeTripleResource(ISubject rdf, URI base, Map<URI,String> prefixes) {
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
    private String encodeTripleResource(ISubject rdf, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//if subject is not defined, do nothing
		if(rdf == null)
			return null;
		//write resource identifier, if no objects are defined
		else if(rdf.getPredicates().isEmpty()){
            sTriple.append(encodeResource(rdf, base, prefixes));
		}
		//write triple set
		else
            sTriple.append(encodeSubject(rdf, base, prefixes, indent, writeSubject, close, sTripleRoot));
		//return
		return sTriple.toString();
	}

	/**
	 * encode RDF triple set
	 * @param subject RDF subject
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param indent styling indent
	 * @param writeSubject set true to include encoded subject node
	 * @param close set true to close the set (. instead of ;)
	 * @return encoded RDF triple set String
	 */
    private String encodeSubject(ISubject subject, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//check number of predicates
		if(subject.getPredicates().size() == 1){
			IResource predicate = subject.getPredicates().iterator().next();
			sTriple.append(indent).append(encodeResource(subject, base, prefixes)).append(" ");
			//write predicate
			sTriple.append(encodeResource(predicate, base, prefixes));
			//write object
			sTriple.append(encodeObject(subject.getObjects(predicate).iterator().next(), base, prefixes, indent, close, sTripleRoot));
		}
		else {
			//write subject, if requested
			if(writeSubject)
				sTriple.append(indent).append(encodeResource(subject, base, prefixes)).append("\n");
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
					sTriple.append(indent).append(encodeResource(predicate, base, prefixes));
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

    private String encodeObject(INode object, URI base, Map<URI,String> prefixes, String indent, boolean close, StringBuilder sTripleRoot){
		StringBuilder sTriple = new StringBuilder();
		//encode subject
		if(object instanceof ISubject){
			if(((ISubject) object).getPredicates().size() > 0){
				if(((IResource) object).isBlank()){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((ISubject) object, base, prefixes, indent, false, false, sTripleRoot));
					sTriple.append(indent).append("]").append(close ? " ." : " ;").append("\n");
				}
				else {
					sTriple.append(" ").append(encodeResource((IResource) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
					sTripleRoot.append(encodeTripleResource((ISubject) object, base, prefixes, "", true, true, sTripleRoot));
				}
			}
			else
				sTriple.append(" ").append(encodeResource((IResource) object, base, prefixes)).append(" ;\n");
		}
		//encode literal
		else if(object instanceof ILiteral)
			sTriple.append(" ").append(encodeLiteral((ILiteral) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
		//encode identifiable resource
		else if(object instanceof IResource && !((IResource) object).isBlank()){
			sTriple.append(" ").append(encodeResource((IResource) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
		}
		else
			//this should not happen
			sTriple.append(" NoIdentifiableRDFNode").append(close ? " ." : " ;").append("\n");
		
		return sTriple.toString();
	}

	/**
	 * encode RDF resource
	 * @param resource RDF resource
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded resource String
	 */
    private String encodeResource(IResource resource, URI base, Map<URI,String> prefixes){
		if(resource.isBlank())
			return "_:" + resource.getClass().getSimpleName() + "_" + UUID.randomUUID();
		else {
			URI uriPrefix = relativizeIdentifier(resource, base, prefixes);
			//write full resource, if it has not been relativized
			if(resource.getIdentifier().equals(uriPrefix))
				return "<" + resource.getIdentifier() + ">";
			else
				return uriPrefix.toString();
		}
	}
	
	/**
	 * encode RDF typed literal
	 * @param literal RDF typed literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
    private String encodeTypedLiteral(ITypedLiteral literal, URI base, Map<URI,String> prefixes){
		IResource resource = literal.getType();
		URI relative = relativizeIdentifier(resource, base, prefixes);
		return "\"" + literal.getValue() + "\"^^" + (resource.getIdentifier().equals(relative) ? "<" + resource.getIdentifier() + ">" : relative);
	}
	
	/**
	 * encode RDF plain literal
	 * @param literal RDF plain literal
	 * @return encoded literal String
	 */
    private String encodePlainLiteral(IPlainLiteral literal){
		return "\"" + literal.getValue() + "\"@" + literal.getLanguage();
	}
	
	/**
	 * encode RDF literal
	 * @param literal RDF literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
    private String encodeLiteral(ILiteral literal, URI base, Map<URI,String> prefixes){
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
    private String encodeLiteral(ILiteral literal, Map<URI,String> prefixes){
		return encodeLiteral(literal, null, prefixes);
	}

	/**
	 * relativize a resource identifier
	 * @param resource input resource
	 * @param base URI base
	 * @param prefixes URI prefixes
	 * @return relativized resource identifier
	 */
    private URI relativizeIdentifier(IResource resource, URI base, Map<URI,String> prefixes) {
		URI relative = relativizeIdentifier(resource, base);
		if(resource.getIdentifier().equals(relative) && prefixes != null){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				relative = relativizeIdentifier(resource, prefix.getKey(), prefix.getValue());
				if(!resource.getIdentifier().equals(relative))
					return relative;
			}
		}
		return relative;
	}
	
	/**
	 * relativize an resource identifier
	 * @param resource input resource
	 * @param uri URI base
	 * @param prefix URI prefix
	 * @return relativized resource identifier
	 */
    private URI relativizeIdentifier(IResource resource, URI uri, String prefix) {
		//special case: http://www.w3.org/1999/02/22-rdf-syntax-ns#type --> a
		if(resource.getIdentifier().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
			return URI.create("a");
		//default
		URI relative = relativizeIdentifier(resource, uri);
		if(resource.getIdentifier().toURI().equals(relative) || relative.toString().matches(".*((/)|(.+#)).*"))
			return resource.getIdentifier().toURI();
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
	private URI relativizeIdentifier(IResource resource, URI base) {
		return relativizeIdentifier(resource, base, "");
	}

	@Override
	public Map<IIdentifier,IInputConnector> initInputConnectors() {
		Map<IIdentifier,IInputConnector> inputConnectors = new HashMap<>();
		inputConnectors.put(IN_RDF, new InputConnector(
                IN_RDF,
				new MetadataForConnector(IN_RDF.toString(), "Input RDF triples"),
				new IDataConstraint[]{
						new BindingConstraint(new Class<?>[]{ISubject.class,IData.class}),
						new MandatoryConstraint()},
				null,
				null));
        inputConnectors.put(IN_URI_BASE, new InputConnector(
                IN_URI_BASE,
                new MetadataForConnector(IN_URI_BASE.toString(), "RDF Base URI"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class)},
                null,
                null));
        inputConnectors.put(IN_URI_PREFIXES, new InputConnector(
                IN_URI_PREFIXES,
                new MetadataForConnector(IN_URI_PREFIXES.toString(), "RDF Prefixes (schema,prefix;schema,prefix...)"),
                new IDataConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new PatternConstraint("^(" + URILiteral.getURLRegex() + ",([a-z]+);)+$")},
                null,
                null));
        inputConnectors.put(IN_TRIPLE_STORE, new InputConnector(
                IN_TRIPLE_STORE,
                new MetadataForConnector(IN_TRIPLE_STORE.toString(), "Triple Store URI"),
                new IDataConstraint[]{
                        new BindingConstraint(URILiteral.class)},
                null,
                null));
        inputConnectors.put(IN_CLEAR_STORE, new InputConnector(
                IN_CLEAR_STORE,
                new MetadataForConnector(IN_CLEAR_STORE.toString(), "Flag: Clear triple store before insert"),
                new IDataConstraint[]{
                        new BindingConstraint(BooleanLiteral.class),
                        new MandatoryConstraint()},
                null,
                new BooleanLiteral(false)));
        inputConnectors.put(IN_TRIPLE_BAG_SIZE, new InputConnector(
                IN_TRIPLE_BAG_SIZE,
                new MetadataForConnector(IN_TRIPLE_BAG_SIZE.toString(), "Number of subjects written to SPARQL endpoint in one INSERT request"),
                new IDataConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new IntegerLiteral(1000)));
		return inputConnectors;
	}

	@Override
	public Map<IIdentifier,IOutputConnector> initOutputConnectors() {
		Map<IIdentifier,IOutputConnector> outputConnectors = new HashMap<>();
		outputConnectors.put(OUT_RESOURCE, new OutputConnector(
                OUT_RESOURCE,
				new MetadataForConnector(OUT_RESOURCE.toString(), "Link to RDF encoded file or SPARQL Endpoint"),
				new IDataConstraint[]{
						new BindingConstraint(URILiteral.class)},
				null));
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
