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
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
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

    private static final String PROCESS_TITLE = WFSProxy.class.getName();
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
    private boolean referenceOutput = false;

    /**
     * constructor
     */
    public WFSProxy(@Nullable IIdentifier identifier, @NotNull URLLiteral base) {
        super(identifier, base);
    }

    /**
     * flag: if true, proxy outputs are WFS references; if false, proxy outputs are GTVectorFeature/GTFeatureCollection objects
     * @return reference output flag
     */
    public boolean isReferenceOutput() {
        return this.referenceOutput;
    }

    public void setReferenceOutput(boolean referenceOutput){
        this.referenceOutput = referenceOutput;
    }

    @Override
    public void executeOperation() {
        if(isReferenceOutput())
            setReferenceOutput();
        else
            setFeatureOutput();
    }

    private void setReferenceOutput() {
        connectOutput(OUT_FEATURES_TITLE, this.getFeatureRequest());
        if (this.getFeatureIdentifier().length > 0) {
            for (String fid : this.getFeatureIdentifier()) {
                URLLiteral featureRequest = this.getFeatureRequest(new StringLiteral(fid));
                IOutputConnector connector = getOutputConnector(fid);
                    if (connector != null)
                        connector.setData(featureRequest);
            }
        }
    }

    private void setFeatureOutput() {
        //parse features
        GTFeatureCollection features;
        try {
            features = getFeatures();
        } catch (IOException e) {
            throw new RuntimeException("Could not parse WFS features: ", e);
        }
        //set output connectors
        connectOutput(OUT_FEATURES_TITLE, features);
        if (this.getFeatureIdentifier().length > 0) {
            for (String fid : this.getFeatureIdentifier()) {
                GTVectorFeature feature = features.getFeatureById(fid);
                if (feature != null) {
                    IOutputConnector connector = getOutputConnector(fid);
                    if (connector != null)
                        connector.setData(feature);
                }
            }
        }
    }

    @Override
    public @NotNull WFSCapabilities getCapabilities() {
        return (WFSCapabilities) super.getCapabilities();
    }

    public GTFeatureCollection getFeatures() throws MalformedURLException {
        GTFeatureParser parser = getParser(((StringLiteral) getInputData(IN_FORMAT_TITLE)));
        if (parser == null)
            throw new RuntimeException("Could not find a parser for input format " + (getInputData(IN_FORMAT_TITLE)));
        //parse and return
        return getFeatures(getFeatureRequest(), parser);
    }

    public @NotNull URLLiteral getFeatureRequest(){
        return this.getFeatureRequest((StringLiteral) getInputData(IN_FID_TITLE));
    }

    /**
     * get feature request
     *
     * @return get feature request
     */
    public @NotNull URLLiteral getFeatureRequest(@Nullable StringLiteral fids) {
        this.setRequest(VALUE_GETFEATURE);
        this.setVersion();
        this.setParameter(PARAM_LAYER, ((StringLiteral) getInputData(IN_LAYER_TITLE)));
        this.setParameter(PARAM_OUTPUTFORMAT, ((StringLiteral) getInputData(IN_FORMAT_TITLE)));
        if(fids != null && !fids.isBlank())
            this.setParameter(PARAM_IDENTIFIER, fids);
        return this.getRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST, PARAM_VERSION, PARAM_LAYER}, new String[]{PARAM_OUTPUTFORMAT, PARAM_IDENTIFIER});
    }

    private @Nullable GTFeatureParser getParser(@Nullable StringLiteral format) {
        if (format == null || format.resolve().matches("(?i).*gml.*"))
            return new GMLParser(null);
        else if (format.resolve().matches("(?i).*json.*"))
            return new JSONParser(null);
        return null;
    }

    @Override
    protected void connectData(@NotNull IWorkflowConnector connector, @Nullable IData data) {
        super.connectData(connector, data);
        //update output connectors, if input FIDs are provided
        if (connector.getTitle() != null && connector.getTitle().equals(IN_FID_TITLE))
            this.updateOutputconnectors();
    }

    /**
     * parse features from WFS
     *
     * @param request input request
     * @param parser  feature parser
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
        addInputConnector(null, IN_FID_TITLE, IN_FID_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        //regex: [^,]+ matches characters except comma one or more times; ,[^,]+)* matches a comma with subsequent non-comma characters
                        new PatternConstraint("([^,]+)(,[^,]+)*")},
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
    public @Nullable String getSelectedOffering() {
        return this.getTypename();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        this.setTypename(offering);
    }

    /**
     * get selected typename
     *
     * @return selected typename
     */
    public @Nullable String getTypename() {
        return this.getParameter(IN_LAYER_TITLE);
    }

    /**
     * sselect typename
     *
     * @param typename input typename
     */
    public void setTypename(@NotNull String typename) {
        if (!this.isSelectedOffering(typename))
            throw new IllegalArgumentException("WFS does not provide a layer " + typename);
        //set KVP parameter
        this.setParameter(IN_LAYER_TITLE, typename);
        //set input connection
        this.connectInput(IN_LAYER_TITLE, new StringLiteral(typename));
    }

    /**
     * get selected feature identifier
     *
     * @return feature identifier
     */
    public @NotNull String[] getFeatureIdentifier() {
        StringLiteral fids = ((StringLiteral) getInputData(IN_FID_TITLE));
        return fids != null ? fids.resolve().split(",") : new String[]{};
    }

    /**
     * set selected feature identifier
     */
    public void setFeatureIdentifier(@Nullable Set<String> fids) {
        this.connectInput(IN_FID_TITLE, fids != null && !fids.isEmpty() ? new StringLiteral(StringUtils.join(fids, ",")) : null);
    }

    @Override
    public void initializeOutputConnectors() {
        //add output for all features
        addOutputConnector(null, OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(isReferenceOutput() ? URLLiteral.class : GTFeatureCollection.class),
                        new MandatoryDataConstraint()},
                new IConnectionConstraint[]{getWFSFormatConstraint()});
    }

    /**
     * update output connectors
     */
    private void updateOutputconnectors() {
        String[] featureIds = this.getFeatureIdentifier();
        if (featureIds.length == 0)
            return;
        //set featureIdConnectors
        if (this.featureIdConnectors == null)
            this.featureIdConnectors = new HashSet<>();
        //remove previous feature id connectors
        for (IIdentifier connectorId : this.featureIdConnectors) {
            this.removeOutputConnector(connectorId);
        }
        this.featureIdConnectors.clear();
        //add new output connectors
        for (String featureId : featureIds) {
            if (featureId.isEmpty())
                continue;
            OutputConnector connector = new OutputConnector(new Identifier(featureId), featureId, featureId, this,
                    new IRuntimeConstraint[]{
                            new BindingConstraint(isReferenceOutput() ? URLLiteral.class : GTVectorFeature.class),
                            new MandatoryDataConstraint()},
                    new IConnectionConstraint[]{getWFSFormatConstraint()});
            this.featureIdConnectors.add(connector.getIdentifier());
            this.addOutputConnector(connector);
        }
    }

    /**
     * get formats supported by this WFS proxy
     *
     * @return supported WFS feature encoding formats
     */
    private IOFormatConstraint getWFSFormatConstraint() {
        return new IOFormatConstraint(this.getCapabilities().getOutputDescription().getSupportedFormats());
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
