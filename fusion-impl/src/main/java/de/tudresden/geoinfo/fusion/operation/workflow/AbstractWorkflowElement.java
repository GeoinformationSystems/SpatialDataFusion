package de.tudresden.geoinfo.fusion.operation.workflow;

import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ElementState;
import de.tudresden.geoinfo.fusion.operation.IWorkflowElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * workflow element implementation
 */
public abstract class AbstractWorkflowElement extends Resource implements IWorkflowElement {

    private ElementState state;

    /**
     * constructor
     *
     * @param identifier element identifier
     */
    public AbstractWorkflowElement(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description) {
        super(identifier, title, description);
        this.setState(ElementState.INITIALIZED);
    }

    /**
     * constructor
     *
     * @param identifier resource identifier
     */
    public AbstractWorkflowElement(@NotNull IIdentifier identifier) {
        this(identifier, null, null);
    }

    /**
     * constructor
     *
     * @param title resource title
     */
    public AbstractWorkflowElement(@NotNull String title) {
        this(null, title, null);
    }

    @NotNull
    @Override
    public ElementState getState() {
        this.updateState();
        return this.state;
    }

    /**
     * set element state
     *
     * @param state new element state
     */
    protected void setState(@NotNull ElementState state) {
        this.state = state;
    }

    /**
     * update the state of this element
     */
    protected abstract void updateState();

    /**
     * compare element state with target state
     *
     * @param state target state
     * @return the value 0 if this.state == state; a value less than 0 if this.state.value < state.value; and a value greater than 0 if this.state.value > state.value
     */
    public int compareState(@NotNull ElementState state) {
        return Integer.compare(this.state.getValue(), state.getValue());
    }

}
