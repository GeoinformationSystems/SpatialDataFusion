package de.tudresden.gis.fusion.data.rdf.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ILiteral;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IPlainLiteral;
import de.tudresden.gis.fusion.data.rdf.IRDFCollection;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRDFTriple;
import de.tudresden.gis.fusion.data.rdf.IRDFTripleSet;
import de.tudresden.gis.fusion.data.rdf.IResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;

public class RDFBasicTurtleEncoder {
	
	public static List<String> encodeTripleResource(IRDFCollection data, URI base, Map<URI,String> prefixes, int chunkSize) {
		List<String> sList = new ArrayList<String>();
		StringBuilder sTriple = new StringBuilder();
		//iterate collection
		int i = 0;
		for(IRDFRepresentation rdf : data.getRDFCollection()){
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
	
	public static String encodeTripleResource(IRDFRepresentation data, URI base, Map<URI,String> prefixes) {
		return encodeTripleResource(data, base, prefixes, "", true, true);
	}
	
	public static String encodeTripleResource(IRDFRepresentation data, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//if subject is not defined, do nothing
		if(data.getSubject() == null)
			return null;
		//write triple
		if(data instanceof IRDFTriple){
			sTriple.append(encodeTriple((IRDFTriple) data, base, prefixes, indent, close));
		}
		//write triple set
		else if(data instanceof IRDFTripleSet){
			sTriple.append(encodeTripleSet((IRDFTripleSet) data, base, prefixes, indent, writeSubject, close));
		}
		//write resource, if objects are not defined
		else
			sTriple.append(RDFBasicTurtleEncoder.encodeResource(data.getSubject(), base, prefixes));
		//return
		return sTriple.toString();
	}

	public static String encodeTripleSet(IRDFTripleSet tripleSet, URI base, Map<URI,String> prefixes, String indent, boolean writeSubject, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//write subject, if requested
		if(writeSubject)
			sTriple.append(indent + RDFBasicTurtleEncoder.encodeResource(tripleSet.getSubject(), base, prefixes) + "\n");
		//increase indent
		indent += "\t";
		int i = tripleSet.getObjectSet().size();
		for(Map.Entry<IIdentifiableResource,Set<INode>> objects : tripleSet.getObjectSet().entrySet()){
			i--;
			boolean lastSet = (i == 0);
			int j = objects.getValue().size();
			for(INode object : objects.getValue()){
				j--;
				boolean setClose = (close && lastSet && j == 0);
				//continue, if either predicate or object are not set
				if(objects.getKey() == null || object == null)
					continue;
				//write predicate
				sTriple.append(indent + RDFBasicTurtleEncoder.encodeResource(objects.getKey(), base, prefixes));
				//write object
				if(object instanceof IRDFTripleSet){
					if(((IRDFTripleSet) object).getObjectSet() != null){
						sTriple.append(" [\n");
						sTriple.append(encodeTripleResource((IRDFTripleSet) object, base, prefixes, indent, false, false));
						sTriple.append(indent + "]" + (setClose ? " ." : " ;") + "\n");
					}
					else
						sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) object, base, prefixes) + " ;\n");		
				}
				else if(object instanceof IRDFTriple){
					if(((IRDFTriple) object).getObject() != null){
						sTriple.append(" [\n");
						sTriple.append(encodeTripleResource((IRDFTriple) object, base, prefixes, indent, false, false));
						sTriple.append(indent + "]" + (setClose ? " ." : " ;") + "\n");
					}
					else
						sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) object, base, prefixes) + " ;\n");		
				}
				else if(object instanceof ILiteral)
					sTriple.append(" " + RDFBasicTurtleEncoder.encodeLiteral((ILiteral) object, base, prefixes) + (setClose ? " ." : " ;") + "\n");
				else if(object instanceof IResource){
					sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) object, base, prefixes) + (setClose ? " ." : " ;") + "\n");
				}
				else
					//this should not be reached
					sTriple.append(" NotAResource ;\n");
			}
		}
		//return
		return sTriple.toString();
	}
	
	public static String encodeTriple(IRDFTriple triple, URI base, Map<URI,String> prefixes, String indent, boolean close) {
		StringBuilder sTriple = new StringBuilder();
		//write subject
		sTriple.append(indent + RDFBasicTurtleEncoder.encodeResource(triple.getSubject(), base, prefixes) + " ");
		//write predicate
		sTriple.append(RDFBasicTurtleEncoder.encodeResource(triple.getPredicate(), base, prefixes));
		//write object
		if(triple.getObject() instanceof IRDFTripleSet){
			if(((IRDFTripleSet) triple.getObject()).getObjectSet() != null){
				sTriple.append(" [\n");
				sTriple.append(encodeTripleResource((IRDFTripleSet) triple.getObject(), base, prefixes, indent, false, false));
				sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
			}
			else
				sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) triple.getObject(), base, prefixes) + " ;\n");		
		}
		else if(triple.getObject() instanceof IRDFTriple){
			if(((IRDFTriple) triple.getObject()).getObject() != null){
				sTriple.append(" [\n");
				sTriple.append(encodeTripleResource((IRDFTriple) triple.getObject(), base, prefixes, indent, false, false));
				sTriple.append(indent + "]" + (close ? " ." : " ;") + "\n");
			}
			else
				sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) triple.getObject(), base, prefixes) + " ;\n");		
		}
		else if(triple.getObject() instanceof ILiteral)
			sTriple.append(" " + RDFBasicTurtleEncoder.encodeLiteral((ILiteral) triple.getObject(), base, prefixes) + (close ? " ." : " ;") + "\n");
		else if(triple.getObject()instanceof IResource){
			sTriple.append(" " + RDFBasicTurtleEncoder.encodeResource((IResource) triple.getObject(), base, prefixes) + (close ? " ." : " ;") + "\n");
		}
		else
			//this should not be reached
			sTriple.append(" NotAResource ;\n");
		return sTriple.toString();
	}

	public static String encodeResource(IResource resource, URI base, Map<URI,String> prefixes){
		if(resource.isBlank())
			//TODO: implement blank id
			return "_:" + resource.getClass().getSimpleName() + "_" + UUID.randomUUID();
		else {
			URI uriPrefix = DataUtilities.relativizeIdentifier(resource.getIdentifier().asURI(), base, prefixes);
			//write full resource, if it has not been relativized
			if(resource.getIdentifier().asURI().equals(uriPrefix))
				return "<" + resource.getIdentifier().toString() + ">";
			else
				return uriPrefix.toString();
		}
	}
	
	public static String encodeResource(IResource resource, Map<URI,String> prefixes){
		return encodeResource(resource, null, prefixes);
	}
	
	public static String encodeTypedLiteral(ITypedLiteral literal, URI base, Map<URI,String> prefixes){
		IIdentifiableResource resource = literal.getType();
		URI relative = DataUtilities.relativizeIdentifier(resource.getIdentifier().asURI(), base, prefixes);
		return "\"" + literal.getIdentifier() + "\"^^" + (resource.getIdentifier().equals(relative) ? "<" + resource.getIdentifier() + ">" : relative);
	}
	
	public static String encodeTypedLiteral(ITypedLiteral literal, Map<URI,String> prefixes){
		return encodeTypedLiteral(literal, null, prefixes);
	}
	
	public static String encodePlainLiteral(IPlainLiteral literal){
		return "\"" + literal.getIdentifier() + "\"@" + literal.getLanguage();
	}
	
	public static String encodeLiteral(ILiteral literal, URI base, Map<URI,String> prefixes){
		if(literal instanceof ITypedLiteral)
			return encodeTypedLiteral((ITypedLiteral) literal, base, prefixes);
		else if(literal instanceof IPlainLiteral)
			return encodePlainLiteral((IPlainLiteral) literal);
		else return null;
	}
	
	public static String encodeLiteral(ILiteral literal, Map<URI,String> prefixes){
		return encodeLiteral(literal, null, prefixes);
	}
	
}
