package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;
import de.tudresden.geoinfo.fusion.operation.ows.WMSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

/**
 * WMS handler
 */
public class WMSHandler extends AbstractOWSHandler {

    /**
     * constructor
     * @param sBaseURL OWS base URL
     * @throws IOException
     */
    public WMSHandler(String uid, String sBaseURL) throws IOException {
        super(new WMSProxy(new Identifier(uid), new URLLiteral(sBaseURL)));
    }

    @Override
    public WMSCapabilities getCapabilities() {
        return (WMSCapabilities) super.getCapabilities();
    }

    @Override
    public WMSProxy getProxy() {
        return (WMSProxy) super.getProxy();
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWMSLayers();
    }

    @Override
    public @Nullable String getSelectedOffering() {
        return this.getProxy().getLayer();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        this.getProxy().setLayer(offering);
    }

}
