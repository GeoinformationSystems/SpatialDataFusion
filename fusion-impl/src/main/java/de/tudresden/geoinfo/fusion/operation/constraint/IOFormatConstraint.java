package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.metadata.IMetadata;
import de.tudresden.geoinfo.fusion.metadata.IOFormat;
import de.tudresden.geoinfo.fusion.metadata.MetadataForIOFormat;
import de.tudresden.geoinfo.fusion.operation.IMetadataConstraint;

import java.util.Set;

/**
 * IO format constraint
 */
public class IOFormatConstraint implements IMetadataConstraint {

    private Set<IOFormat> supportedFormats;

    public IOFormatConstraint(Set<IOFormat> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }

    private boolean compliantWith(IOFormat format) {
        return supportedFormats.contains(format);
    }

    @Override
    public boolean compliantWith(IMetadata metadata) {
        if(metadata instanceof MetadataForIOFormat)
            for(IOFormat format : ((MetadataForIOFormat) metadata).getSupportedFormats()){
                if(this.compliantWith(format))
                    return true;
            }
        return false;
    }

}
