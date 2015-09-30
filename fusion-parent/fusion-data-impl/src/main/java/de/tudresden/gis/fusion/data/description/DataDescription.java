package de.tudresden.gis.fusion.data.description;

import de.tudresden.gis.fusion.data.rdf.Resource;

public class DataDescription extends Resource implements IDataDescription {

	String title, description;
	
	public DataDescription(String identifier, String title, String description){
		super(identifier);
		this.title = title;
		this.description = description;
	}
	
	public DataDescription(String title, String description){
		this(null, title, description);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
