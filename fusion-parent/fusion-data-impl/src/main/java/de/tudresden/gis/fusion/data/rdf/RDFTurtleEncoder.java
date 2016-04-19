package de.tudresden.gis.fusion.data.rdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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
	public static List<String> encodeTripleResource(ISubjectCollection collection, URI base, Map<URI,String> prefixes, int chunkSize) {
		List<String> sList = new ArrayList<String>();
		StringBuilder sTriple = new StringBuilder();
		StringBuilder sTripleRoot = new StringBuilder();
		//iterate collection
		int i = 0;
		for(ISubject rdf : collection.collection()){
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
		//write triple
		if(rdf instanceof ITriple){
			sTriple.append(encodeTriple((ITriple) rdf, base, prefixes, indent, close, sTripleRoot));
		}
		//write triple set
		else if(rdf instanceof ITripleSet){
			sTriple.append(encodeTripleSet((ITripleSet) rdf, base, prefixes, indent, writeSubject, close, sTripleRoot));
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
	public static String encodeTripleSet(ITripleSet tripleSet, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//write subject, if requested
		if(writeSubject)
			sTriple.append(indent + encodeResource(tripleSet, base, prefixes) + "\n");
		//increase indent
		indent += "\t";
		//iterate predicates in triple set
		int i = tripleSet.size();
		for(IIdentifiableResource predicate : (tripleSet).getPredicates()){
			Collection<INode> objects = tripleSet.getObject(predicate);
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
	public static String encodeTriple(ITriple triple, URI base, Map<URI,String> prefixes, String indent, boolean close, StringBuilder sTripleRoot) {
		StringBuilder sTriple = new StringBuilder();
		//write subject
		sTriple.append(indent + encodeResource(triple, base, prefixes) + " ");
		//write predicate
		sTriple.append(encodeResource(triple.getPredicate(), base, prefixes));
		//write object
		sTriple.append(encodeObject(triple.getObject(), base, prefixes, indent, close, sTripleRoot));
		
		return sTriple.toString();
	}
	
	public static String encodeObject(INode object, URI base, Map<URI,String> prefixes, String indent, boolean close, StringBuilder sTripleRoot){
		StringBuilder sTriple = new StringBuilder();
		//encode triple set
		if(object instanceof ITripleSet){
			if(((ITripleSet) object).size() > 0){
				if(((IResource) object).isBlank()){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((ITripleSet) object, base, prefixes, indent, false, false, sTripleRoot));
					sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
				}
				else {
					sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
					sTripleRoot.append(encodeTripleResource((ITripleSet) object, base, prefixes, "", true, true, sTripleRoot));
				}
			}
			else
				sTriple.append(" " + encodeResource((IIdentifiableResource) object, base, prefixes) + " ;\n");		
		}
		//encode triple
		else if(object instanceof ITriple){
			if(((ITriple) object).getObject() != null){
				if(((IResource) object).isBlank()){
					sTriple.append(" [\n");
					sTriple.append(encodeTripleResource((ITriple) object, base, prefixes, indent, false, false, sTripleRoot));
					sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
				} 
				else {
					sTriple.append(" " + encodeResource((IResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
					sTripleRoot.append(encodeTripleResource((ITriple) object, base, prefixes, "", true, true, sTripleRoot));
				}
			}
			else
				sTriple.append(" " + encodeResource((IIdentifiableResource) object, base, prefixes) + " ;\n");		
		}
		//encode literal
		else if(object instanceof ILiteral)
			sTriple.append(" " + encodeLiteral((ILiteral) object, base, prefixes) + (close ? " ." : " ;") + "\n");
		//encode identifiable resource
		else if(object instanceof IIdentifiableResource){
			sTriple.append(" " + encodeResource((IIdentifiableResource) object, base, prefixes) + (close ? " ." : " ;") + "\n");
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
			URI uriPrefix = relativizeIdentifier(new IdentifiableResource(resource), base, prefixes);
			//write full resource, if it has not been relativized
			if(resource.asURI().equals(uriPrefix))
				return "<" + resource.asString().toString() + ">";
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
		IIdentifiableResource resource = literal.getType();
		URI relative = relativizeIdentifier(resource, base, prefixes);
		return "\"" + literal.getValue() + "\"^^" + (resource.asString().equals(relative) ? "<" + resource.asString() + ">" : relative);
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
	public static URI relativizeIdentifier(IIdentifiableResource resource, URI base, Map<URI,String> prefixes) {
		URI relative = relativizeIdentifier(resource, base);
		if(resource.asURI().equals(relative) && prefixes != null){
			for(Map.Entry<URI,String> prefix : prefixes.entrySet()){
				relative = relativizeIdentifier(resource, prefix.getKey(), prefix.getValue());
				if(!resource.asURI().equals(relative))
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
	public static URI relativizeIdentifier(IIdentifiableResource resource, URI uri, String prefix) {
		//special case: http://www.w3.org/1999/02/22-rdf-syntax-ns#type --> a
		if(resource.asString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
			return URI.create("a");
		//default
		URI relative = resource.relativizeURI(uri);
		if(resource.asURI().equals(relative) || relative.toString().matches(".*((\\/)|(.+#)).*"))
			return resource.asURI();
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
	public static URI relativizeIdentifier(IIdentifiableResource resource, URI base) {
		return relativizeIdentifier(resource, base, "");
	}
	
}
