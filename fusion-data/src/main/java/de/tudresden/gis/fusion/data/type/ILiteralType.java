package de.tudresden.gis.fusion.data.type;

import de.tudresden.gis.fusion.data.IDataType;
import de.tudresden.gis.fusion.data.IRI;

public interface ILiteralType extends IDataType {
	
	public IRI getIdentifier();
	
	//RDF literal types
//	LITERAL_TYPE_BOOLEAN("http://www.w3.org/2001/XMLSchema/#boolean"),
//	LITERAL_TYPE_INTEGER("http://www.w3.org/2001/XMLSchema/#integer"),
//	LITERAL_TYPE_LONG("http://www.w3.org/2001/XMLSchema/#long"),
//	LITERAL_TYPE_DECIMAL("http://www.w3.org/2001/XMLSchema/#decimal"),
//	LITERAL_TYPE_STRING("http://www.w3.org/2001/XMLSchema/#string"),
//	LITERAL_TYPE_ANYURI("http://www.w3.org/2001/XMLSchema/#anyURI"),

}
