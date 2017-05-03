package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * IO format constraint
 */
public class IOFormatConstraint implements IConnectionConstraint {

    private Set<IOFormat> supportedFormats;
    private IOFormat defaultFormat;

    /**
     * constructor
     *
     * @param supportedFormats supported formats
     * @param defaultFormat default format
     */
    public IOFormatConstraint(@NotNull Set<IOFormat> supportedFormats, @Nullable IOFormat defaultFormat) {
        if(supportedFormats.isEmpty())
            throw new IllegalArgumentException("At least one supported format must be specified");
        this.supportedFormats = supportedFormats;
        this.defaultFormat = defaultFormat != null ? defaultFormat : supportedFormats.iterator().next();
        if(defaultFormat != null && !supportedFormats.contains(defaultFormat))
            this.supportedFormats.add(defaultFormat);
    }


    /**
     * constructor
     *
     * @param supportedFormats supported formats
     */
    public IOFormatConstraint(@NotNull Set<IOFormat> supportedFormats) {
        this(supportedFormats, null);
    }

    /**
     * constructor
     *
     * @param supportedFormat supported format
     */
    public IOFormatConstraint(@NotNull IOFormat supportedFormat) {
        this(Collections.singleton(supportedFormat), null);
    }

    @Override
    public boolean compliantWith(@Nullable IWorkflowConnector connector) {
        if (connector == null)
            return true;
        for (IConnectionConstraint constraint : connector.getConnectionConstraints()) {
            if (constraint instanceof IOFormatConstraint) {
                for (IOFormat format : ((IOFormatConstraint) constraint).getSupportedFormats()) {
                    if (this.compliantWith(format))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean compliantWith(@NotNull IOFormat format) {
        return supportedFormats.contains(format);
    }

    /**
     * get supported formats
     *
     * @return supported formats
     */
    @NotNull
    public Set<IOFormat> getSupportedFormats() {
        return this.supportedFormats;
    }

    /**
     * get default format
     *
     * @return default format
     */
    @NotNull
    public IOFormat getDefaultFormat() {
        return this.defaultFormat;
    }

}
