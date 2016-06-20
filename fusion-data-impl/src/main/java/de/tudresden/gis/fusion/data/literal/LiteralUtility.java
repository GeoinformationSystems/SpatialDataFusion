package de.tudresden.gis.fusion.data.literal;

import java.net.URI;

import de.tudresden.gis.fusion.data.ILiteralData;

public class LiteralUtility {

	/**
	 * get literal from object
	 * @param value input value
	 * @return RDF literal object
	 */
	public static ILiteralData literal(Object value) {
		if(value instanceof Boolean)
			return new BooleanLiteral((Boolean) value);
		else if(value instanceof Integer)
			return new IntegerLiteral((Integer) value);
		else if(value instanceof Long)
			return new LongLiteral((Long) value);
		else if(value instanceof Double)
			return new DecimalLiteral((Double) value);
		else if(value instanceof URI)
			return new URILiteral((URI) value);
		else
			return new StringLiteral(value.toString());
	}
	
}
