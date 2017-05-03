package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.operation.ows.OWSServiceOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * basic OWS Handler, implemented by OWS beans
 */
public abstract class AbstractOWSHandler {

    private OWSServiceOperation owsProxy;

    /**
     * constructor
     * @param owsProxy OWS proxy instance
     */
    public AbstractOWSHandler(OWSServiceOperation owsProxy) {
        this.owsProxy = owsProxy;
    }

    /**
     * get OWS proxy
     * @return OWS proxy
     */
    public OWSServiceOperation getProxy() {
        return this.owsProxy;
    }

    /**
     * get base URL for OWS proxy
     * @return base URL
     */
    public URLLiteral getBase() {
        return owsProxy.getBase();
    }

    /**
     * get proxy identifier
     * @return identifier
     */
    public String getIdentifier() {
        return this.owsProxy.getIdentifier().toString();
    }

    /**
     * get OWS capabilities document
     *
     * @return capabilites document
     */
    public OWSCapabilities getCapabilities() {
        return this.owsProxy.getCapabilities();
    }

    /**
     * get OWS offerings provided by this OWS instance
     *
     * @return OWS offerings
     */
    public abstract @NotNull Set<String> getOfferings();

    /**
     * check, if specified offering is supported
     * @param offering input offering
     * @return true, if offering is supported by OWS instance
     */
    public boolean isSupportedOffering(String offering){
        return getOfferings().contains(offering);
    }

    /**
     * get selected OWS offering
     *
     * @return selected OWS offering
     */
    public abstract @Nullable String getSelectedOffering();

    /**
     * select OWS offering
     *
     * @param offering OWS offering
     */
    public abstract void setSelectedOffering(@NotNull String offering);

}
