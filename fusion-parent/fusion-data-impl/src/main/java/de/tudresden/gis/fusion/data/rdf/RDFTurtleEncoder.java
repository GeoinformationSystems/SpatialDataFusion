package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RDFTurtleEncoder {

	/**
	 * encode RDF representation collection
	 * @param collection RDF representation collection
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param chunkSize number of RDF representations in one String
	 * @return list of encoded RDF representation Strings
	 */
	public static List<String> encodeTripleResource(IRDFCollection collection, URI base, Map<URI,String> prefixes, int chunkSize) {
		List<String> sList = new ArrayList<String>();
		StringBuilder sTriple = new StringBuilder();
		//iterate collection
		int i = 0;
		for(IRDFRepresentation rdf : collection.rdfCollection()){
			i++;
			sTriple.append(encodeTripleResource(rdf, base, prefixes, "", true, true));
			//cut relation string by chunk size
			if(i % chunkSize == 0){
				sList.add(sTriple.toString());
				sTriple.setLength(0);
			}
		}
		//add remaining relations
		if(sTriple.length() > 0)
			sList.add(sTriple.toString());
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
	public static String encodeTripleResource(IRDFRepresentation rdf, URI base, Map<URI,String> prefixes) {
		return encodeTripleResource(rdf, base, prefixes, "", true, true);
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
	public static String encodeTripleResource(IRDFRepresentation rdf, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//if subject is not defined, do nothing
		if(rdf.subject() == null)
			return null;
		//write triple
		if(rdf instanceof IRDFTriple){
			sTriple.append(encodeTriple((IRDFTriple) rdf, base, prefixes, indent, close));
		}
		//write triple set
		else if(rdf instanceof IRDFTripleSet && ((IRDFTripleSet) rdf).objectSet().size() > 0){
			sTriple.append(encodeTripleSet((IRDFTripleSet) rdf, base, prefixes, indent, writeSubject, close));
		}
		//write resource, if objects are not defined
		else
			sTriple.append(encodeResource(rdf.subject(), base, prefixes));
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
	public static String encodeTripleSet(IRDFTripleSet tripleSet, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//write subject, if requested
		if(writeSubject)
			sTriple.append(indent + encodeResource(tripleSet.subject(), base, prefixes) + "\n");
		//increase indent
		indent += "\t";
		int i = tripleSet.objectSet().size();
		for(IRDFPredicateObject predobj : tripleSet.objectSet()){
			i--;
			boolean lastSet = (i == 0);
			boolean setClose = (close && lastSet);
			//continue, if either predicate or object are not set
			if(predobj.predicate() == null || predobj.object() == null)
				continue;
			//write predicate
			sTriple.append(indent + encodeResource(predobj.predicate(), base, prefixes));
			//write object
			if(predobj.object() instanceof IRDFTripleSet){
				if(((IRDFTripleSet) predobj.object()).objectSet() != null){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((IRDFTripleSet) predobj.object(), base, prefixes, indent, false, false));
					sTriple.append(indent + "]" + (setClose ? " ." : " ;") + "\n");
				}
				else
					sTriple.append(" " + encodeResource((IRDFIdentifiableResource) predobj.object(), base, prefixes) + " ;\n");		
			}
			else if(predobj.object() instanceof IRDFTriple){
				if(((IRDFTriple) predobj.object()).object() != null){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((IRDFTriple) predobj.object(), base, prefixes, indent, false, false));
					sTriple.append(indent + "]" + (setClose ? " ." : " ;") + "\n");
				}
				else
					sTriple.append(" " + encodeResource((IRDFIdentifiableResource) predobj.object(), base, prefixes) + " ;\n");		
			}
			else if(predobj.object() instanceof IRDFLiteral)
				sTriple.append(" " + encodeLiteral((IRDFLiteral) predobj.object(), base, prefixes) + (setClose ? " ." : " ;") + "\n");
			else if(predobj.object() instanceof IRDFIdentifiableResource){
				sTriple.append(" " + encodeResource((IRDFIdentifiableResource) predobj.object(), base, prefixes) + (setClose ? " ." : " ;") + "\n");
			}
			else
				//this should not be reached
				sTriple.append(" NotAResource ;\n");
		}
		//return
		return sTriple.toString();
	}
	
	/**
	 * encode RDF triple
	 * @param triple RDF triple
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @param indent styling indent
	 * @param close set true to close the set (. instead of ;)
	 * @return encoded RDF triple String
	 */
	public static String encodeTriple(IRDFTriple triple, URI base, Map<URI,String> prefixes, String indent, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//write subject
		sTriple.append(indent + encodeResource(triple.subject(), base, prefixes) + " ");
		//write predicate
		sTriple.append(encodeResource(triple.predicate(), base, prefixes));
		//write object
		if(triple.object() instanceof IRDFTripleSet){
			if(((IRDFTripleSet) triple.object()).objectSet() != null){
				sTriple.append(" [\n");
				sTriple.append(encodeTripleResource((IRDFTripleSet) triple.object(), base, prefixes, indent, false, false));
				sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
			}
			else
				sTriple.append(" " + encodeResource((IRDFIdentifiableResource) triple.object(), base, prefixes) + " ;\n");		
		}
		else if(triple.object() instanceof IRDFTriple){
			if(((IRDFTriple) triple.object()).object() != null){
				sTriple.append(" [\n");
				sTriple.append(encodeTripleResource((IRDFTriple) triple.object(), base, prefixes, indent, false, false));
				sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
			}
			else
				sTriple.append(" " + encodeResource((IRDFIdentifiableResource) triple.object(), base, prefixes) + " ;\n");		
		}
		else if(triple.object() instanceof IRDFLiteral)
			sTriple.append(" " + encodeLiteral((IRDFLiteral) triple.object(), base, prefixes) + (close ? " ." : " ;") + "\n");
		else if(triple.object()instanceof IRDFIdentifiableResource){
			sTriple.append(" " + encodeResource((IRDFIdentifiableResource) triple.object(), base, prefixes) + (close ? " ." : " ;") + "\n");
		}
		else
			//this should not be reached
			sTriple.append(" NotAResource ;\n");
		return sTriple.toString();
	}

	/**
	 * encode RDF resource
	 * @param resource RDF resource
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded resource String
	 */
	public static String encodeResource(IRDFIdentifiableResource resource, URI base, Map<URI,String> prefixes){
		if(resource instanceof IRDFResource && ((IRDFResource) resource).blank())
			//TODO: implement blank id
			return "_:" + resource.getClass().getSimpleName() + "_" + UUID.randomUUID();
		else {
			URI uriPrefix = relativizeIdentifier(resource.identifier().toURI(), base, prefixes);
			//write full resource, if it has not been relativized
			if(resource.identifier().toURI().equals(uriPrefix))
				return "<" + resource.identifier().toString() + ">";
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
	public static String encodeResource(IRDFIdentifiableResource resource, Map<URI,String> prefixes){
		return encodeResource(resource, null, prefixes);
	}
	
	/**
	 * encode RDF typed literal
	 * @param literal RDF typed literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeTypedLiteral(IRDFTypedLiteral literal, URI base, Map<URI,String> prefixes){
		IRDFIdentifiableResource resource = literal.type();
		URI relative = relativizeIdentifier(resource.identifier().toURI(), base, prefixes);
		return "\"" + literal.literalValue().value() + "\"^^" + (resource.identifier().equals(relative) ? "<" + resource.identifier() + ">" : relative);
	}
	
	/**
	 * encode RDF typed literal
	 * @param literal RDF typed literal
	 * @return encoded literal String
	 */
	public static String encodeTypedLiteral(IRDFTypedLiteral literal, Map<URI,String> prefixes){
		return encodeTypedLiteral(literal, null, prefixes);
	}
	
	/**
	 * encode RDF plain literal
	 * @param literal RDF plain literal
	 * @return encoded literal String
	 */
	public static String encodePlainLiteral(IRDFPlainLiteral literal){
		return "\"" + literal.literalValue().value() + "\"@" + literal.language();
	}
	
	/**
	 * encode RDF literal
	 * @param literal RDF literal
	 * @param base RDF base
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeLiteral(IRDFLiteral literal, URI base, Map<URI,String> prefixes){
		if(literal instanceof IRDFTypedLiteral)
			return encodeTypedLiteral((IRDFTypedLiteral) literal, base, prefixes);
		else if(literal instanceof IRDFPlainLiteral)
			return encodePlainLiteral((IRDFPlainLiteral) literal);
		else return null;
	}
	
	/**
	 * encode RDF literal
	 * @param literal RDF literal
	 * @param prefixes RDF prefixes
	 * @return encoded literal String
	 */
	public static String encodeLiteral(IRDFLiteral literal, Map<URI,String> prefixes){
		return encodeLiteral(literal, null, prefixes);
	}
	
	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @param prefixes URI prefixes
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI base, Map<URI,String> prefixes) {
		URI relative = relativizeIdentifier(identifier, base);
		if(identifier.equals(relative) && prefixes != null){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				relative = relativizeIdentifier(identifier, prefix.getKey(), prefix.getValue());
				if(!identifier.equals(relative))
					return relative;
			}
		}
		return relative;
	}
	
	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @param prefixes URI prefix
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI uri, String prefix) {
		if(identifier == null)
			return null;
		if(uri == null)
			return identifier;
		URI relative = uri.relativize(identifier);
		if(identifier.equals(relative) || relative.toString().contains("/"))
			return identifier;
		else
			return URI.create((prefix == null || prefix.length() == 0 ? "" : prefix + ":") + (relative.toString().startsWith("#") ? relative.toString().substring(1) : relative.toString()));
	}

	/**
	 * relativize an URI identifier
	 * @param identifier input identifier
	 * @param base URI base
	 * @return relativized identifier
	 */
	public static URI relativizeIdentifier(URI identifier, URI base) {
		return relativizeIdentifier(identifier, base, "");
	}
	
}
