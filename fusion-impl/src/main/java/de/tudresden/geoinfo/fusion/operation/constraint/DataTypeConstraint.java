package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Data binding constraint
 */
public class DataTypeConstraint implements IConnectionConstraint {

    private Set<IRDFProperty> supportedDataTypes;

    /**
     * constructor
     *
     * @param supportedDataTypes supported data types
     */
    public DataTypeConstraint(@NotNull Set<IRDFProperty> supportedDataTypes) {
        this.supportedDataTypes = supportedDataTypes;
    }

    /**
     * constructor
     *
     * @param supportedDataTypes supported data types
     */
    public DataTypeConstraint(@NotNull IRDFProperty[] supportedDataTypes) {
        this(Sets.newHashSet(supportedDataTypes));
    }

    /**
     * constructor
     *
     * @param supportedDataType supported data type
     */
    public DataTypeConstraint(@NotNull IRDFProperty supportedDataType) {
        this(Sets.newHashSet(supportedDataType));
    }

    @Override
    public boolean compliantWith(@Nullable IWorkflowConnector connector) {
        if (connector == null)
            return true;
        for (IConnectionConstraint constraint : connector.getConnectionConstraints()) {
            if (constraint instanceof DataTypeConstraint) {
                for (IRDFProperty resource : ((DataTypeConstraint) constraint).getSupportedDataTypes()) {
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
    private boolean compliantWith(@NotNull IRDFProperty target) {
        for (IRDFProperty dataType : this.getSupportedDataTypes()) {
            if (dataType.getIRI().equals(target.getIRI()))
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
    public Set<IRDFProperty> getSupportedDataTypes() {
        return this.supportedDataTypes;
    }

}
