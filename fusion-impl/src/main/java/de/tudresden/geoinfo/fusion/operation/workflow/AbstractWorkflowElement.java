package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IWorkflowElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * workflow element implementation
 */
public abstract class AbstractWorkflowElement extends Resource implements IWorkflowElement {

    private String title, description;

    public AbstractWorkflowElement(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description){
        super(identifier != null ? identifier : new Identifier());
        this.title = title;
        this.description = description;
    }

    @Nullable
    @Override
    public String getTitle(){
        return title;
    }

    protected void setTitle(@NotNull String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getDescription(){
        return description;
    }

    protected void setDescription(@NotNull String description) {
        this.description = description;
    }

}
