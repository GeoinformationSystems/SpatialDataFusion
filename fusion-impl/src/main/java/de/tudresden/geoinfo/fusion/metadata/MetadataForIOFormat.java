package de.tudresden.geoinfo.fusion.metadata;

import java.util.Set;

/**
 * IO Format description
 */
public class MetadataForIOFormat extends Metadata {

    private Set<IOFormat> supportedFormats;

    /**
     * constructor
     * @param title IO format title
     * @param description IO format description
     * @param supportedFormats supportedIO formats
     */
	public MetadataForIOFormat(String title, String description, Set<IOFormat> supportedFormats){
        super(title, description);
		this.supportedFormats = supportedFormats;
	}

    /**
     * get supported formats
     * @return supported formats
     */
    public Set<IOFormat> getSupportedFormats() {
        return supportedFormats;
    }

}
