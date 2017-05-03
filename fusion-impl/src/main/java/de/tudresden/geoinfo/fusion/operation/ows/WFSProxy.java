package de.tudresden.geoinfo.fusion.operation.ows;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WFSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.PatternConstraint;
import de.tudresden.geoinfo.fusion.operation.retrieval.GMLParser;
import de.tudresden.geoinfo.fusion.operation.retrieval.GTFeatureParser;
import de.tudresden.geoinfo.fusion.operation.retrieval.JSONParser;
import de.tudresden.geoinfo.fusion.operation.workflow.OutputConnector;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class WFSProxy extends OWSServiceOperation {

    private static final String PROCESS_TITLE = WFSProxy.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for OGC WFS";

    private final static String IN_FORMAT_TITLE = "IN_FORMAT";
    private final static String IN_FORMAT_DESCRIPTION = "WFS output format";
    private final static String IN_LAYER_TITLE = "IN_LAYER";
    private final static String IN_LAYER_DESCRIPTION = "WFS layer";
    private final static String IN_FID_TITLE = "IN_FID";
    private final static String IN_FID_DESCRIPTION = "requested GML feature identifier (comma separated)";

    private final static String PARSER_IN_RESOURCE = "IN_RESOURCE";
    private final static String PARSER_OUT_FEATURES = "OUT_FEATURES";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "All WFS features";

    private final static String PARAM_SERVICE = "service";
    private final static String PARAM_REQUEST = "request";
    private final static String VALUE_SERVICE = "WFS";
    private final static String VALUE_GETFEATURE = "getFeature";
    private final static String PARAM_LAYER = "typename";
    private final static String PARAM_VERSION = "version";
    private final static String VALUE_DEFAULT_VERSION = "1.1.0";
    private final static String PARAM_OUTPUTFORMAT = "outputformat";
    private final static String PARAM_IDENTIFIER = "featureID";

    private final static Set<String> SUPPORTED_VERSIONS = new HashSet<>(Arrays.asList("1.0.0", "1.1.0", "2.0.0"));

    private Set<IIdentifier> featureIdConnectors;

    /**
     * constructor
     */
    public WFSProxy(@Nullable IIdentifier identifier, @NotNull URLLiteral base) {
        super(identifier, PROCESS_TITLE, PROCESS_DESCRIPTION, base, true);
    }

    @Override
    public void execute() {
        //parse features
        GTFeatureCollection features;
        try {
            features = getFeatures();
        } catch (IOException e) {
            throw new RuntimeException("Could not parse WFS features: ", e);
        }
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
    }

    @Override
    public WFSCapabilities getCapabilities() {
        return (WFSCapabilities) super.getCapabilities();
    }

    public GTFeatureCollection getFeatures() throws MalformedURLException {
        GTFeatureParser parser = getParser(((StringLiteral) getInputData(IN_FORMAT_TITLE)));
        if(parser == null)
            throw new RuntimeException("Could not find a parser for input format " + (getInputData(IN_FORMAT_TITLE)));
        //parse and return
        return getFeatures(getFeatureRequest(), parser);
    }

    /**
     * get feature request
     * @return get feature request
     * @throws MalformedURLException
     */
    public @NotNull URLLiteral getFeatureRequest() throws MalformedURLException {
        this.setRequest(VALUE_GETFEATURE);
        this.setVersion();
        this.setParameter(PARAM_LAYER, ((StringLiteral) getInputData(IN_LAYER_TITLE)));
        this.setParameter(PARAM_OUTPUTFORMAT, ((StringLiteral) getInputData(IN_FORMAT_TITLE)));
        this.setParameter(PARAM_IDENTIFIER, ((StringLiteral) getInputData(IN_FID_DESCRIPTION)));
        return this.getRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST, PARAM_VERSION, PARAM_LAYER}, new String[]{PARAM_OUTPUTFORMAT, PARAM_IDENTIFIER});
    }

    private @Nullable GTFeatureParser getParser(@Nullable StringLiteral format) {
        if(format == null || format.resolve().matches("(?i).*gml.*"))
            return new GMLParser();
        else if(format.resolve().matches("(?i).*json.*"))
            return new JSONParser();
        return null;
    }

    /**
     * parse features from WFS
     * @param request input request
     * @param parser feature parser
     * @return feature collection from from WFS
     */
    private GTFeatureCollection getFeatures(URLLiteral request, GTFeatureParser parser) {
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(PARSER_IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(PARSER_OUT_FEATURES).getIdentifier();
        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_RESOURCE, request);
        Map<IIdentifier, IData> output = parser.execute(input);
        return (GTFeatureCollection) output.get(ID_OUT_FEATURES);
    }


    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_FORMAT_TITLE, IN_FORMAT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class)},
                null,
                null);
        addInputConnector(IN_LAYER_TITLE, IN_LAYER_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_FID_TITLE, IN_FID_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new PatternConstraint("(\\w+)(,\\w+)*")},
                null,
                null);
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
        return this.getCapabilities().getWFSLayers();
    }

    @Override
    public @Nullable String getSelectedOffering(){
        return this.getTypename();
    }

    /**
     * get selected typename
     * @return selected typename
     */
    public @Nullable String getTypename() {
        return this.getParameter(IN_LAYER_TITLE);
    }

    /**
     * sselect typename
     * @param typename input typename
     */
    public void setTypename(@NotNull String typename) {
        if (!this.getCapabilities().getWFSLayers().contains(typename))
            throw new IllegalArgumentException("WFS does not provide a layer " + typename);
        //set KVP parameter
        this.setParameter(IN_LAYER_TITLE, typename);
        //set input connection
        this.connectInput(IN_LAYER_TITLE, new StringLiteral(typename));
    }

    /**
     * get selected feature identifier
     * @return feature identifier
     */
    public @Nullable String[] getFeatureIdentifier() {
        String fids = this.getParameter(PARAM_IDENTIFIER);
        return fids != null ? fids.split(",") : null;
    }

    /**
     * set feature identifier to be selected
     * @param featureIds input identifier
     */
    public void setFeatureIdentifier(@NotNull Set<String> featureIds) {
        this.setParameter(PARAM_IDENTIFIER, featureIds.isEmpty() ? null : StringUtils.join(featureIds, ","));
        updateOutputconnectors();
    }

    /**
     * set feature identifier to be selected
     * @param featureId input identifier
     */
    public void setFeatureIdentifier(@Nullable String featureId) {
        this.setFeatureIdentifier(featureId != null ? Collections.singleton(featureId) : Collections.emptySet());
    }

    @Override
    public void initializeOutputConnectors() {
        //add output for all features
        addOutputConnector(OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                new IConnectionConstraint[]{getWFSFormatConstraint()});
    }

    /**
     * update output connectors
     */
    private void updateOutputconnectors() {
        if(this.getFeatureIdentifier() == null || this.getFeatureIdentifier().length == 0)
            return;
        //set featureIdConnectors
        if(this.featureIdConnectors == null)
            this.featureIdConnectors = new HashSet<>();
        //remove previous feature id connectors
        for(IIdentifier connectorId : this.featureIdConnectors){
            this.removeOutputConnector(connectorId);
        }
        this.featureIdConnectors.clear();
        //add new output connectors
        for(String featureId : this.getFeatureIdentifier()){
            if(featureId == null)
                continue;
            OutputConnector connector = new OutputConnector(new Identifier(featureId), featureId, featureId, this,
                    new IRuntimeConstraint[]{new BindingConstraint(GTVectorFeature.class)},
                    new IConnectionConstraint[]{getWFSFormatConstraint()});
            this.featureIdConnectors.add(connector.getIdentifier());
            this.addOutputConnector(connector);
        }
    }

    /**
     * get formats supported by this WFS proxy
     * @return supported WFS feature encoding formats
     */
    private IOFormatConstraint getWFSFormatConstraint() {
        return new IOFormatConstraint(this.getCapabilities().getOutputDescription().getSupportedFormats());
    }

}
