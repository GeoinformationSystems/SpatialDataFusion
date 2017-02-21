package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Data binding constraint
 */
public class DataTypeConstraint implements IConnectionConstraint {

    private Set<IResource> supportedDataTypes;

    /**
     * constructor
     *
     * @param supportedDataTypes supported data types
     */
    public DataTypeConstraint(@NotNull Set<IResource> supportedDataTypes) {
        this.supportedDataTypes = supportedDataTypes;
    }

    /**
     * constructor
     *
     * @param supportedDataTypes supported data types
     */
    public DataTypeConstraint(@NotNull IResource[] supportedDataTypes) {
        this(Sets.newHashSet(supportedDataTypes));
    }

    /**
     * constructor
     *
     * @param supportedDataType supported data type
     */
    public DataTypeConstraint(@NotNull IResource supportedDataType) {
        this(Sets.newHashSet(supportedDataType));
    }

    @Override
    public boolean compliantWith(@NotNull IWorkflowConnector connector) {
        for (IConnectionConstraint constraint : connector.getConnectionConstraints()) {
            if (constraint instanceof DataTypeConstraint) {
                for (IResource resource : ((DataTypeConstraint) constraint).getSupportedDataTypes()) {
                    if (this.compliantWith(resource))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * check if object classes are compliant
     *
     * @param target target object
     * @return true, if there is a match between the bindings
     */
    private boolean compliantWith(@NotNull IResource target) {
        for (IResource dataType : this.getSupportedDataTypes()) {
            if (dataType.getIdentifier().equals(target.getIdentifier()))
                return true;
        }
        return false;
    }

    /**
     * get supported formats
     *
     * @return supported formats
     */
    @NotNull
    public Set<IResource> getSupportedDataTypes() {
        return this.supportedDataTypes;
    }

}
