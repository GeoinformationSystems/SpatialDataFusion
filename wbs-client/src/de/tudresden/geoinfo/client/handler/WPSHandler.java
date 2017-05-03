package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WPSDescribeProcess;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ows.WPSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONObject;

import javax.faces.application.FacesMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WPSHandler extends AbstractOWSHandler {

    public WPSHandler(String uid, String sBaseURL) throws IOException {
        super(new WPSProxy(new Identifier(uid), new URLLiteral(sBaseURL)));
    }

    @Override
    public WPSCapabilities getCapabilities() {
        return (WPSCapabilities) super.getCapabilities();
    }

    @Override
    public WPSProxy getProxy() {
        return (WPSProxy) super.getProxy();
    }

    @Override
    public @NotNull Set<String> getOfferings() {
        return this.getCapabilities().getWPSProcesses();
    }

    @Override
    public @Nullable String getSelectedOffering() {
        return this.getProxy().getProcessId();
    }

    @Override
    public void setSelectedOffering(@NotNull String offering) {
        try {
            this.getProxy().setProcessId(offering);
        } catch (IOException e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "WPS process description error", e.getLocalizedMessage());
        }
    }

    /**
     * get process description for selected process
     * @return process description
     */
    public WPSDescribeProcess.WPSProcessDescription getProcessDescription() {
        return this.getProxy().getProcessDescription();
    }

    /**
     * get process description as json (used by jsPlumb)
     *
     * @return JSON process description
     */
    public JSONObject getJSONProcessDescription() {
        if (this.getProxy() == null || this.getProxy().getProcessDescription() == null)
            return null;
        //set hidden entries
        Set<IIdentifier> hiddenInputs = new HashSet<>();
        hiddenInputs.add(this.getProxy().getInputConnector("IN_VERSION").getIdentifier());
        Set<IIdentifier> hiddenOutputs = new HashSet<>();
        hiddenOutputs.add(this.getProxy().getOutputConnector("OUT_START").getIdentifier());
        hiddenOutputs.add(this.getProxy().getOutputConnector("OUT_RUNTIME").getIdentifier());
        //get description
        return JSONUtils.getJSONDescription(this.getProxy(), hiddenInputs, hiddenOutputs);
    }

    /**
     * execute the process
     * @param input input data
     * @return output data
     */
    public Map<IIdentifier,IData> execute(Map<IIdentifier, IData> input) {
        return this.getProxy().execute(input);
    }
}
