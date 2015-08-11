package de.tudresden.gis.fusion.data.feature.relation;

import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public interface IRelationType extends IRDFIdentifiableResource {

	/**
	 * get role of the source feature view as specified by this relation type
	 * @return source role
	 */
	public IRole getSourceRole();
	
	/**
	 * get role of the target feature view as specified by this relation type
	 * @return source role
	 */
	public IRole getTargetRole();
	
}
