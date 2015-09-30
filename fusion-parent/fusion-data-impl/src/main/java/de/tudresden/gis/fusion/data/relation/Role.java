package de.tudresden.gis.fusion.data.relation;

import de.tudresden.gis.fusion.data.feature.relation.IRole;

public class Role implements IRole {

	private String name, description;
	
	public Role(String name, String description){
		this.name = name;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
