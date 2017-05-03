package de.tudresden.geoinfo.fusion.data.ows;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * IO Format implementation
 */
public class IOFormat implements Comparable<IOFormat> {

    private String mimetype;
    private String schema;
    private String type;

    /**
     * constructor
     * @param mimetype format mimetype
     * @param schema format schema
     * @param type format type
     */
    public IOFormat(@Nullable String mimetype, @Nullable String schema, @Nullable String type) {
        this.mimetype = mimetype;
        this.schema = schema;
        this.type = type;
    }

    public @Nullable String getMimetype() {
        return mimetype;
    }

    void setMimetype(@NotNull String mimetype) {
        this.mimetype = mimetype;
    }

    public @Nullable String getSchema() {
        return schema;
    }

    void setSchema(@NotNull String schema) {
        this.schema = schema;
    }

    public @Nullable String getType() {
        return type;
    }

    void setType(@NotNull String type) {
        this.type = type;
    }

    /**
     * compares formats based on mimetype, schema and type
     */
    public boolean equals(@NotNull IOFormat format) {
        return equals(format.getMimetype(), this.getMimetype()) &&
                equals(format.getSchema(), this.getSchema()) &&
                equals(format.getType(), this.getType());
    }

    /**
     * check if two strings are equal, allows null values
     * @param io1 first string
     * @param io2 second string
     * @return true, if io1 equals io2
     */
    private boolean equals(@Nullable String io1, @Nullable String io2){
        return io1 != null ? io1.equalsIgnoreCase(io2) : io2 == null;
    }

    @Override
    public int compareTo(@NotNull IOFormat format) {
        if (this.equals(format))
            return 0;
        else
            return 1;
    }

}