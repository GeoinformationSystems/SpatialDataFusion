package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.IMetadataElement;
import de.tudresden.geoinfo.fusion.operation.IOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic metadata implementation
 */
public class Metadata implements IMetadata {

    private Map<IIdentifier, IMetadataElement> elements;

    /**
     * Constructor
     */
    public Metadata() {
        this.elements = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param elements initial collection of metadata elements
     */
    public Metadata(@NotNull Collection<IMetadataElement> elements) {
        this();
        this.addElements(elements);
    }

    /**
     * Constructor with DC title and abstract
     *
     * @param dc_title    dublin core title
     * @param dc_abstract dublin core description
     */
    public Metadata(@Nullable String dc_title, @Nullable String dc_abstract) {
        this();
        if(dc_title != null)
            this.addElement(new MetadataElement(MetadataVocabulary.DC_TITLE.getIdentifier(), dc_title));
        if(dc_abstract != null)
            this.addElement(new MetadataElement(MetadataVocabulary.DC_ABSTRACT.getIdentifier(), dc_abstract));
    }

    /**
     * Constructor for measurement metadata
     * @param dc_title dublin core title
     * @param dc_abstract dublin core description
     * @param uom unit of measurement
     * @param range measurement range
     * @param operation measurement operation
     */
    public Metadata(@Nullable String dc_title, @Nullable String dc_abstract, @Nullable IIdentifier uom, @Nullable IMeasurementRange range, @Nullable IOperation operation) {
        this(dc_title, dc_abstract);
        if(uom != null)
            this.addElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_UOM.getIdentifier(), uom));
        if(range != null)
            this.addElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_VALUE_RANGE.getIdentifier(), range));
        if(operation != null)
            this.addElement(new MetadataElement(MetadataVocabulary.MEASUREMENT_OPERATION.getIdentifier(), operation));
    }

    @Override
    public @NotNull Collection<IMetadataElement> getElements() {
        return this.elements.values();
    }

    @Override
    public @Nullable IMetadataElement getElement(@NotNull IIdentifier resource) {
        return this.elements.get(resource);
    }

    @Override
    public boolean hasElement(@NotNull IIdentifier resource) {
        return this.elements.containsKey(resource);
    }

    /**
     * add single metadata element
     *
     * @param element metadata element
     */
    public void addElement(@NotNull IMetadataElement element) {
        this.elements.put(element.getIdentifier(), element);
    }

    /**
     * add collection of metadata elements
     *
     * @param elements metadata elements
     */
    public void addElements(@NotNull Collection<IMetadataElement> elements) {
        for (IMetadataElement element : elements) {
            this.addElement(element);
        }
    }

}
