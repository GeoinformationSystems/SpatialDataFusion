package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.ResourceIdentifier;
import de.tudresden.geoinfo.fusion.operation.IWorkflowElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * workflow element implementation
 */
public abstract class AbstractWorkflowElement extends ResourceIdentifier implements IWorkflowElement {

    private String description;

    /**
     * constructor
     * @param identifier element identifier
     */
    public AbstractWorkflowElement(@NotNull IIdentifier identifier, @Nullable String description){
        super(identifier);
        this.description = description;
    }

    /**
     * constructor
     * @param identifier element identifier
     */
    public AbstractWorkflowElement(@NotNull IIdentifier identifier){
        this(identifier, null);
    }

    /**
     * get element description
     * @return element description
     */
    public String getDescription() {
        return this.description;
    }

}
