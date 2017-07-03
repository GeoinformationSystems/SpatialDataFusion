package de.tudresden.geoinfo.fusion.operation.ows;

import de.tud.fusion.KVPRequestBuilder;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IConnectionConstraint;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.PatternConstraint;
import de.tudresden.geoinfo.fusion.operation.retrieval.ows.OWSCapabilitiesParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public abstract class OWSServiceOperation extends AbstractOperation {

    private final static String IN_VERSION_TITLE = "IN_VERSION";
    private final static String IN_VERSION_DESCRIPTION = "OWS version";

    private final static String PARAM_SERVICE = "service";
    private final static String PARAM_REQUEST = "request";
    private final static String PARAM_VERSION = "version";
    private final static String VALUE_GETCAPABILITIES = "getCapabilities";

    private final static String PARSER_IN_RESOURCE = "IN_RESOURCE";
    private final static String PARSER_OUT_CAPABILITIES = "OUT_CAPABILITIES";

    private KVPRequestBuilder requestBuilder;
    private OWSCapabilities capabilities;


    /**
     * constructor
     *
     * @param base        OSW base URL
     */
    public OWSServiceOperation(@Nullable IIdentifier identifier, @NotNull URLLiteral base) {
        super(identifier);
        this.requestBuilder = new KVPRequestBuilder(base);
        this.requestBuilder.setParameter(PARAM_VERSION, this.getDefaultVersion());
        this.requestBuilder.setParameter(PARAM_SERVICE, this.getService());
        this.initializeConnectors();
    }

    @Override
    public void initializeConnectors() {
        if(this.requestBuilder != null) {
            super.initializeConnectors();
            this.setCapabilities();
        }
    }

    /**
     * get OWS request parameter
     *
     * @return OWS request parameter
     */
    public @Nullable String getParameter(@NotNull String parameter) {
        return this.requestBuilder.getParameter(parameter);
    }

    /**
     * get OWS GET request
     *
     * @param mandatoryKeys mandatory GET keys
     * @param optionalKeys  optional GET keys
     * @return GET request for OWS service
     */
    public URLLiteral getRequest(@NotNull String[] mandatoryKeys, @NotNull String[] optionalKeys) {
        return this.getKVPRequestBuilder().getKVPRequest(mandatoryKeys, optionalKeys);
    }

    /**
     * set OWS request parameter
     *
     * @param parameter OWS request parameter
     * @param value     OWS request parameter value
     */
    public void setParameter(@NotNull String parameter, @Nullable String value) {
        if (value == null)
            this.requestBuilder.removeParameter(parameter);
        else
            this.requestBuilder.setParameter(parameter, value);
    }

    /**
     * set OWS request parameter
     *
     * @param parameter OWS request parameter
     * @param literal   OWS request parameter value
     */
    public void setParameter(@NotNull String parameter, @Nullable StringLiteral literal) {
        if (literal == null)
            this.requestBuilder.removeParameter(parameter);
        else
            this.setParameter(parameter, literal.resolve());
    }

    /**
     * get OWS service URL
     *
     * @return OWS service URL
     */
    public @NotNull URLLiteral getBase() {
        return this.requestBuilder.getBase();
    }

    /**
     * get service type for this OWS
     *
     * @return service type
     */
    public abstract String getService();

    /**
     * get OWS service version
     *
     * @return OWS service version
     */
    public @NotNull String getVersion() {
        //noinspection ConstantConditions
        return this.requestBuilder.getParameter(PARAM_VERSION);
    }

    /**
     * set OWS version
     */
    public void setVersion() {
        this.setParameter(PARAM_VERSION, (StringLiteral) this.getInputData(IN_VERSION_TITLE));
    }

    /**
     * get default versio for this OWS
     *
     * @return default version
     */
    public abstract @NotNull String getDefaultVersion();

    /**
     * get supported version for this OWS
     *
     * @return supported versions
     */
    public abstract @NotNull Set<String> getSupportedVersions();

    /**
     * get offerings that can be requested from the OWS
     *
     * @return service offerings (e.g. layers, maps, processes)
     */
    public abstract @NotNull Set<String> getOfferings();

    /**
     * get offerings that can be requested from the OWS
     *
     * @return service offerings (e.g. layers, maps, processes)
     */
    public abstract @Nullable String getSelectedOffering();

    /**
     * select OWS offering
     *
     * @param offering OWS offering
     * @throws IllegalArgumentException if offering is not supported
     */
    public abstract void setSelectedOffering(@NotNull String offering);

    /**
     * check, if provided offering is supported by this service
     *
     * @param offering input offering
     * @return true, if offering is provided by this service
     */
    public boolean isSelectedOffering(@NotNull String offering) {
        return this.getOfferings().contains(offering);
    }

    /**
     * get OWS service request
     *
     * @return OWS service request
     */
    public @NotNull String getRequest() {
        //noinspection ConstantConditions
        return this.requestBuilder.getParameter(PARAM_VERSION);
    }

    /**
     * set OWS request
     *
     * @param request OWS request
     */
    public void setRequest(@NotNull String request) {
        this.requestBuilder.setParameter(PARAM_REQUEST, request);
    }

    /**
     * get OWS capabilities
     *
     * @return OWS capabilities
     */
    public @NotNull OWSCapabilities getCapabilities() {
        if (this.capabilities == null)
            this.setCapabilities();
        return this.capabilities;
    }

    /**
     * set OWS capabilities
     */
    void setCapabilities() {
        //init parser
        OWSCapabilitiesParser parser = new OWSCapabilitiesParser(null);
        IIdentifier ID_IN_RESOURCE = parser.getInputConnector(PARSER_IN_RESOURCE).getIdentifier();
        IIdentifier ID_OUT_CAPABILITIES = parser.getOutputConnector(PARSER_OUT_CAPABILITIES).getIdentifier();
        Map<IIdentifier, IData> input = new HashMap<>();
        //set input, parse capabilities and connect output
        try {
            input.put(ID_IN_RESOURCE, getCapabilitiesRequest());
            Map<IIdentifier, IData> output = parser.execute(input);
            this.capabilities = (OWSCapabilities) output.get(ID_OUT_CAPABILITIES);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not parse capabilities", e);
        }
    }

    /**
     * get KVP request builder
     *
     * @return KVP request builder
     */
    private @NotNull KVPRequestBuilder getKVPRequestBuilder() {
        return this.requestBuilder;
    }

    /**
     * get capabilities request
     *
     * @return get capabilities
     * @throws MalformedURLException
     */
    private @NotNull URLLiteral getCapabilitiesRequest() throws MalformedURLException {
        this.setRequest(VALUE_GETCAPABILITIES);
        return this.requestBuilder.getKVPRequest(new String[]{PARAM_SERVICE, PARAM_REQUEST}, new String[]{PARAM_VERSION});
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_VERSION_TITLE, IN_VERSION_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new PatternConstraint("(" + String.join(")|(", this.getSupportedVersions()) + ")")},
                new IConnectionConstraint[]{
                        new IOFormatConstraint(new IOFormat(null, null, "xs:string"))
                },
                new StringLiteral(this.getDefaultVersion()));
    }

}
