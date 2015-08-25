package de.tudresden.gis.fusion.data.literal;

import de.tudresden.gis.fusion.data.rdf.IRDFLiteral;

public class LiteralUtility {

	/**
	 * get RDF literal from object
	 * @param value input value
	 * @return RDF literal object
	 */
	public static IRDFLiteral literal(Object value) {
		if(value instanceof Boolean)
			return new BooleanLiteral((Boolean) value);
		else if(value instanceof Integer)
			return new IntegerLiteral((Integer) value);
		else if(value instanceof Long)
			return new LongLiteral((Long) value);
		else if(value instanceof Double)
			return new DecimalLiteral((Double) value);
		else
			return new StringLiteral(value.toString());
	}
	
}
