package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.ows.OWSCapabilities;
import de.tudresden.geoinfo.fusion.data.ows.WMSCapabilities;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class WMSHandler extends OWSHandler {

	private final String SERVICE = "WMS";
    private final String DEFAULT_VERSION = "1.3.0";
    private final String DEFAULT_FORMAT = "image/png";

    private WMSCapabilities wmsCapabilities;

	private final String PARAM_FORMAT = "format";
    private final String PARAM_LAYERS = "layers";
	private final String PARAM_SRSNAME = "srs";
	private final String PARAM_BBOX = "bbox";

	public WMSHandler(String sBaseURL) throws IOException {
	    super(sBaseURL);
		this.setService(SERVICE);
		this.setVersion(DEFAULT_VERSION);
		this.setParameter(PARAM_FORMAT,DEFAULT_FORMAT);
		this.wmsCapabilities = getCapabilities();
	}

    /**
     * get WMS capabilities document
     * @return capabilites document
     */
    public WMSCapabilities getCapabilities() throws IOException {
        OWSCapabilities capabilities = super.getCapabilities();
        if(!(capabilities instanceof WMSCapabilities))
            throw new IOException("Could not parse WMS capabilities");
        return (WMSCapabilities) capabilities;
    }

    /**
     * get all layers provided by this WMS instance
     * @return WMS layers
     */
    public Set<String> getSupportedLayers(){
        return wmsCapabilities != null ? wmsCapabilities.getWMSLayers() : Collections.emptySet();
    }

    /**
     * check, if certain layer is provided
     * @param sLayer input layer name
     * @return true, if layer is provided by WMS
     */
    public boolean isSupportedLayer(String sLayer){
        return this.getSupportedLayers().contains(sLayer);
    }

    /**
     * get selected WMS layer
     * @return selected WMS layer
     */
    public String getLayer() {
        return this.getParameter(PARAM_LAYERS);
    }

    /**
     * select WMS layer
     * @param sLayer WMS layer name
     */
    public void setLayer(String sLayer) {
        if(!this.isSupportedLayer(sLayer))
            throw new IllegalArgumentException("Layer " + sLayer + " is not supported");
        this.setParameter(PARAM_LAYERS, sLayer);
    }

}
