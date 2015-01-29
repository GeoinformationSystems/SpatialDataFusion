package de.tudresden.gis.fusion.data.rdf;

public enum ERDFNamespaces {

	//RDF literal types
	LITERAL_TYPE_BOOLEAN("http://www.w3.org/2001/XMLSchema/#boolean"),
	LITERAL_TYPE_INTEGER("http://www.w3.org/2001/XMLSchema/#integer"),
	LITERAL_TYPE_LONG("http://www.w3.org/2001/XMLSchema/#long"),
	LITERAL_TYPE_DECIMAL("http://www.w3.org/2001/XMLSchema/#decimal"),
	LITERAL_TYPE_STRING("http://www.w3.org/2001/XMLSchema/#string"),
	
	//RDF basics
	INSTANCE_OF("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	HAS_VALUE("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
	
	private IIdentifiableResource resource;
	
	private ERDFNamespaces(String identifier){
		this.resource = new IdentifiableResource(identifier);
	}
	
	public IIdentifiableResource resource(){
		return resource;
	}
	
	public static IIdentifiableResource resource4Identifier(IIRI identifier){
		for(ERDFNamespaces namespace : ERDFNamespaces.values()){
			if(namespace.resource().getIdentifier().equals(identifier))
				return namespace.resource();
		}
		return null;
	}
	
}