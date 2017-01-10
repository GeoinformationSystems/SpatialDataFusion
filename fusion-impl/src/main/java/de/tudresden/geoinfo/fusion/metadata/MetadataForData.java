package de.tudresden.geoinfo.fusion.metadata;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * Data description implementation
 */
public class MetadataForData extends Metadata implements IMetadataForData {
	
	private IResource dataType;
	
	/**
	 * constructor
	 * @param title data title
	 * @param description data description
	 * @param dataType supported data type
	 */
	public MetadataForData(String title, String description, IResource dataType) {
		super(title, description);
		this.dataType = dataType;
	}

	@Override
	public IResource getDataType() {
		return dataType;
	}

}
