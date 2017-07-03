package de.tudresden.geoinfo.fusion.operation.ows;

import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WMSProxy extends OWSServiceOperation {

    private static final String PROCESS_TITLE = WMSProxy.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for OGC WMS";

    private final static String IN_FORMAT_TITLE = "IN_FORMAT";
    private final static String IN_FORMAT_DESCRIPTION = "WMS image format";
    private final static String IN_LAYER_TITLE = "IN_LAYER";
    private final static String IN_LAYER_DESCRIPTION = "WFS layer";

    private final static String PARAM_LAYERS = "layers";
    private final static String VALUE_SERVICE = "WMS";
    private final static String VALUE_DEFAULT_VERSION = "1.3.0";

    private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Arrays.asList("1.0.0", "1.3.0"));

    /**
     * constructor
     */
    public WMSProxy(@Nullable IIdentifier identifier, @NotNull URLLiteral base) {
        super(identifier, base);
    }

    @Override
    public void executeOperation() {
        //do nothing //TODO implement getMap
    }

    @Override
    public @NotNull WMSCapabilities getCapabilities() {
        return (WMSCapabilities) super.getCapabilities();
    }

    @Override
    public void initializeInputConnectors() {
        super.initializeInputConnectors();
        addInputConnector(null, IN_FORMAT_TITLE, IN_FORMAT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class)},
                null,
                null);
        addInputConnector(null, IN_LAYER_TITLE, IN_LAYER_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
    }

    @Override
    protected void initializeOutputConnectors() {
        //TODO add output
    }

    @Override
    public @Nullable String getSelectedOffering() {
        return this.getLayer();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        this.setLayer(offering);
    }

    @Override
    public String getService() {
        return VALUE_SERVICE;
    }

    @Override
    public @NotNull String getDefaultVersion() {
        return VALUE_DEFAULT_VERSION;
    }

    @Override
    public @NotNull Set<String> getSupportedVersions() {
        return SUPPORTED_VERSIONS;
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWMSLayers();
    }

    /**
     * get selected layer
     *
     * @return selected layer
     */
    public @Nullable String getLayer() {
        return this.getParameter(PARAM_LAYERS);
    }

    /**
     * select layer
     *
     * @param layer input typename
     */
    public void setLayer(@NotNull String layer) {
        if (!this.isSelectedOffering(layer))
            throw new IllegalArgumentException("Layer " + layer + " is not supported");
        this.setParameter(PARAM_LAYERS, layer);
    }

    @NotNull
    @Override
    public String getTitle() {
        return PROCESS_TITLE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PROCESS_DESCRIPTION;
    }

}
