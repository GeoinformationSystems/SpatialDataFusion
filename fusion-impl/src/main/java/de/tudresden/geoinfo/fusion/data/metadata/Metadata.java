package de.tudresden.geoinfo.fusion.data.metadata;

import de.tudresden.geoinfo.fusion.data.IMeasurementRange;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.IMetadataElement;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic metadata implementation
 */
public class Metadata implements IMetadata {

    private Map<IResource, IMetadataElement> elements;

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
    public Metadata(Collection<IMetadataElement> elements) {
        this();
        this.addElements(elements);
    }

    /**
     * Constructor
     *
     * @param dc_title    dublin core title
     * @param dc_abstract dublin core description
     */
    public Metadata(String dc_title, String dc_abstract) {
        this();
        this.addElement(new MetadataElement(DC_Metadata.DC_TITLE.getResource(), dc_title));
        this.addElement(new MetadataElement(DC_Metadata.DC_ABSTRACT.getResource(), dc_abstract));
    }

    /**
     * Constructor for measurements
     *
     * @param dc_title         dublin core title
     * @param dc_abstract      dublin core description
     * @param uom              unit of measurement
     * @param measurementRange measurement range
     */
    public Metadata(String dc_title, String dc_abstract, IResource uom, IMeasurementRange measurementRange) {
        this(dc_title, dc_abstract);
        this.addElement(new MetadataElement(Predicates.MEASUREMENT_VALUE_RANGE.getResource(), uom));
        this.addElement(new MetadataElement(Predicates.MEASUREMENT_UOM.getResource(), measurementRange));
    }

    @Override
    public @NotNull Collection<IMetadataElement> getElements() {
        return this.elements.values();
    }

    @Override
    public @Nullable IMetadataElement getElement(@NotNull IResource resource) {
        return this.elements.get(resource);
    }

    @Override
    public boolean hasElement(IResource resource) {
        return this.elements.containsKey(resource);
    }

    /**
     * add single metadata element
     *
     * @param element metadata element
     */
    public void addElement(@NotNull IMetadataElement element) {
        this.elements.put(element.getResource(), element);
    }

    /**
     * add collection of metadata elements
     *
     * @param elements metadata elements
     */
    public void addElements(Collection<IMetadataElement> elements) {
        for (IMetadataElement element : elements) {
            this.addElement(element);
        }
    }

}
