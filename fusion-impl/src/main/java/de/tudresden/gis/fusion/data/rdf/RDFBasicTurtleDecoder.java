package de.tudresden.gis.fusion.data.rdf;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class RDFBasicTurtleDecoder {

	/**
	 * decode triples resource
	 * @param sResource triple resource string
	 * @param prefixes RDF prefixes
	 * @return RDF representation of input resource
	 * @throws IOException  
	 */
	public static IRDFRepresentation decodeRDFResource(String sResource, Map<String,URI> prefixes) throws IOException {
		if(sResource.contains(";"))
			return decodeTripleSet(sResource, prefixes);
		else
			return decodeTriple(sResource, prefixes);
	}

	public static IRDFTripleSet decodeTripleSet(String sTripleSet, Map<String,URI> prefixes) throws IOException {
		
		String[] aTripleSet = decomposeTripleSetString(sTripleSet.trim());
		IResource subject = new Resource();
		Map<IIdentifiableResource,Set<INode>> objectSet = new HashMap<IIdentifiableResource,Set<INode>>();
		
		for(int i=0; i<aTripleSet.length; i++) {
			
			if(aTripleSet[i].startsWith("["))
				continue;
				
			String[] elements = splitRDFLine(aTripleSet[i]);			
			
			//first line (subject, predicate, object)
			if(elements.length == 3){
				subject = decodeResource(elements[0], prefixes);
				addToObjectSet(objectSet, decodeIdentifiableResource(elements[1], prefixes), decodeNode(elements[2], prefixes));
			}
			
			//refers to previously defined subject (predicate, object)
			else if(elements.length == 2){
				addToObjectSet(objectSet, decodeIdentifiableResource(elements[0], prefixes), decodeNode(elements[1], prefixes));
			}
			
			//refers to single predicate with following triple set
			else if(elements.length == 1 && aTripleSet.length > i+1 && aTripleSet[i+1].startsWith("[")){
				addToObjectSet(objectSet, decodeIdentifiableResource(elements[0], prefixes), decodeTripleSet(aTripleSet[i+1], prefixes));
			}

			else
				continue;
		}
		
		return new RDFTripleSet(subject, objectSet);
	}
	
	/**
	 * split line within RDF file
	 * @param line RDF line
	 * @return splitted RDF elements
	 */
	private static String[] splitRDFLine(String line) {
		//handles RDF literals that contain spaces between quotes
		return line.split("[\\s]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	}

	private static void addToObjectSet(Map<IIdentifiableResource,Set<INode>> objectSet, IIdentifiableResource predicate, INode node){
		if(objectSet.containsKey(predicate))
			objectSet.get(predicate).add(node);
		else
			objectSet.put(predicate, DataUtilities.toSet(node));
	}
	
	private static String[] decomposeTripleSetString(String sTripleSet){
		//remove surrounding brackets (if exist)
		if(sTripleSet.startsWith("[") && sTripleSet.endsWith("]"))
			sTripleSet = sTripleSet.substring(1, sTripleSet.length()-2);
		//remove multiple whitespaces and end point
		sTripleSet = sTripleSet.replaceAll("\\s+", " ").replaceAll("\\]\\s*\\.|\\]\\s*\\;", "]");
		//split by semicolon
		String[] aTripleSet = sTripleSet.split(";|.(?=\\[)|(?<=\\[).|.(?=\\])|(?<=\\]).");
		//create list, remove empty lines and merge sub-triples
		List<String> elementList = new ArrayList<String>();
		String lineTmp = "";
		int level = 1;
		for(String line : aTripleSet){
			line = line.trim();
			if(line.matches("\\s*"))
				continue;			
			else if(line.matches("\\[")){
				level++;
				lineTmp += "[ ";
			}
			else if(line.matches("\\]")){
				level--;
				lineTmp += " ]";
				if(level == 1){
					elementList.add(lineTmp);
					lineTmp = "";
				}					
			}
			else if(level != 1){
				lineTmp += line + " ; ";
			}
			else
				elementList.add(line);
		}
		return elementList.toArray(new String[elementList.size()]);
	}
	
	public static IRDFTriple decodeTriple(String sTriple, Map<String,URI> prefixes) throws IOException {
		String[] aTriple = sTriple.trim().split(" ");
		if(aTriple.length < 3)
			return null;
		IResource subject = decodeResource(aTriple[0], prefixes);
		IIdentifiableResource predicate = decodeIdentifiableResource(aTriple[1], prefixes);
		INode object = decodeNode(aTriple[2], prefixes);
		return new RDFTriple(subject, predicate, object);
	}
	
	/**
	 * decode RDF Node
	 * @param sNode input node as string
	 * @param prefixes RDF prefixes
	 * @return RDF node implementation
	 * @throws IOException
	 */
	public static INode decodeNode(String sNode, Map<String,URI> prefixes) throws IOException {
		if(sNode == null || sNode.length() == 0)
			return null;
		if(sNode.contains("\""))
			return decodeLiteral(sNode);
		else
			return decodeResource(sNode, prefixes);
	}
	
	/**
	 * decode identifiable RDF resource
	 * @param resource input resource as string
	 * @param prefixes RDF prefixes
	 * @return decoded identifiable RDF resource implementation
	 * @throws IOException
	 */
	public static IIdentifiableResource decodeIdentifiableResource(String resource, Map<String,URI> prefixes) throws IOException {
		if(resource.startsWith("_:")){
			throw new IOException("Identifiable resource must not be blank.");
		}
		else {
			URI identifier = DataUtilities.resolveIdentifier(resource, prefixes);
			return DataUtilities.resolveResource(new IRI(identifier));
		}
	}
	
	/**
	 * decode RDF resource
	 * @param resource input resource as string
	 * @param prefixes RDF prefixes
	 * @return decoded RDF resource implementation
	 * @throws IOException
	 */
	public static IResource decodeResource(String resource, Map<String,URI> prefixes) throws IOException {
		if(resource.startsWith("_:")){
			return new Resource(null);
		}
		else {
			URI identifier = DataUtilities.resolveIdentifier(resource, prefixes);
			return new IdentifiableResource(new IRI(identifier));
		}
	}
	
	/**
	 * decode RDF literal
	 * @param literal input literal string
	 * @return RDF literal
	 */
	public static ILiteral decodeLiteral(String literal){
		if(literal.contains("^^"))
			return decodeTypedLiteral(literal);
		else if(literal.contains("@"))
			return decodePlainLiteral(literal);
		else
			return new StringLiteral(literal);
	}
	
	/**
	 * decode RDF typed literal
	 * @param literal input literal string
	 * @return RDF typed literal
	 */
	public static ITypedLiteral decodeTypedLiteral(String literal){
		String[] parts = literal.split("\\^\\^");
		if(parts[1].contains("boolean"))
			return new BooleanLiteral(Boolean.parseBoolean(parts[0].replace("\"","")));
		if(parts[1].contains("integer"))
			return new IntegerLiteral(Integer.parseInt(parts[0].replace("\"","")));
		if(parts[1].contains("long"))
			return new LongLiteral(Long.parseLong(parts[0].replace("\"","")));
		if(parts[1].contains("decimal"))
			return new DecimalLiteral(Double.parseDouble(parts[0].replace("\"","")));
		else
			return new StringLiteral(parts[0].replace("\"",""));
	}
	
	/**
	 * decode RDF plain literal
	 * @param literal input literal string
	 * @return RDF plain literal
	 */
	public static IPlainLiteral decodePlainLiteral(String literal){
		String[] parts = literal.split("@");
		return new StringLiteral(parts[0].replace("\"", ""), parts[1]);
	}
	
	/**
	 * get prefix definitions
	 * @param sPrefixes prefix string (prefixes separated by ".")
	 * @return prefix map
	 */
	public static Map<String,URI> getPrefixes(String sPrefixes){
		Map<String,URI> prefixes = new HashMap<String,URI>();
		String[] aPrefixes = sPrefixes.split(".");
		for(String sPrefix : aPrefixes){
			String[] aPrefix = getPrefix(sPrefix);
			if(aPrefix == null || aPrefix.length != 2)
				continue;
			try {
				prefixes.put(aPrefix[0], URI.create(aPrefix[1]));
			} catch(Exception e){
				continue;
			}
		}
		return prefixes;
	}
	
	/**
	 * get prefix definition from string
	 * @param sPrefix prefix string ("@prefix prefix: uri .")
	 * @return prefix {prefix, uri}
	 */
	public static String[] getPrefix(String sPrefix){
		String[] parts = sPrefix.split(" ");
		if(!parts[0].equalsIgnoreCase("@prefix") || parts.length < 3)
			return null;
		return new String[]{parts[1].trim().replace(":",""), parts[2].replace("<","").replace(">","")};
	}
	
}
