package de.tudresden.gis.fusion.data.simple;

import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.rdf.ILiteral;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.metadata.data.IDescription;

public abstract class Literal implements ISimpleData,ITypedLiteral {
	
	String identifier;
	IDescription description;
	
	public Literal(String identifier, IDescription description){
		this.identifier = identifier;
		this.description = description;
	}
	
	public Literal(String identifier){
		this(identifier, null);
	}

	@Override
	public IDescription getDescription() {
		return description;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public ILiteral getRDFRepresentation() {
		return this;
	}

}
