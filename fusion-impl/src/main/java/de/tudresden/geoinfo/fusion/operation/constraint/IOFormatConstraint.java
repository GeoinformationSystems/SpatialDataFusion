package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.ows.WPSIOFormat;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * IO format constraint
 */
public class IOFormatConstraint implements IConnectionConstraint {

    private Set<WPSIOFormat> supportedFormats;

    /**
     * constructor
     *
     * @param supportedFormats supported formats
     */
    public IOFormatConstraint(@NotNull Set<WPSIOFormat> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }

    @Override
    public boolean compliantWith(@NotNull IWorkflowConnector connector) {
        for (IConnectionConstraint constraint : connector.getConnectionConstraints()) {
            if (constraint instanceof IOFormatConstraint) {
                for (WPSIOFormat format : ((IOFormatConstraint) constraint).getSupportedFormats()) {
                    if (this.compliantWith(format))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean compliantWith(@NotNull WPSIOFormat format) {
        return supportedFormats.contains(format);
    }

    /**
     * get supported formats
     *
     * @return supported formats
     */
    @NotNull
    public Set<WPSIOFormat> getSupportedFormats() {
        return this.supportedFormats;
    }

}
