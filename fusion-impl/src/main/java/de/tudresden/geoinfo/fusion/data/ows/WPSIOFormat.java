package de.tudresden.geoinfo.fusion.data.ows;

import org.jetbrains.annotations.NotNull;

/**
 * IO Format implementation
 */
public class WPSIOFormat implements Comparable<WPSIOFormat> {

    private String mimetype;
    private String schema;
    private String type;

    public WPSIOFormat(String mimetype, String schema, String type) {
        this.mimetype = mimetype;
        this.schema = schema;
        this.type = type;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * compares formats based on mimetype and schema
     */
    public boolean equals(@NotNull WPSIOFormat format) {
        return format.getMimetype().equalsIgnoreCase(getMimetype()) &&
                format.getSchema().equalsIgnoreCase(getSchema()) &&
                format.getType().equalsIgnoreCase(getType());
    }

    @Override
    public int compareTo(@NotNull WPSIOFormat format) {
        if (this.equals(format))
            return 0;
        else
            return 1;
    }

}