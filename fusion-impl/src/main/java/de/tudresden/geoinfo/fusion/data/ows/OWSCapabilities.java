package de.tudresden.geoinfo.fusion.data.ows;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

/**
 * standard OWS capabilities
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class OWSCapabilities extends OWSResponse {

    private final static String NODE_XSI_SCHEMA_LOCATION = "xsi:schemaLocation";

    private OWSServiceType serviceType;

    /**
     * constructor
     *
     * @param uri      OWS base uri literal
     * @param object   OWS capabilities document
     * @param metadata OWS capabilities metadata
     */
    public OWSCapabilities(@NotNull URILiteral uri, @NotNull Document object, @Nullable IMetadata metadata) {
        super(uri, object, metadata);
        initCapabilities();
    }

    /**
     * parse capabilities
     */
    private void initCapabilities() {
        setServiceIdentification();
    }

    /**
     * parse service identification
     */
    private void setServiceIdentification() {
        if (this.resolve().getFirstChild().getAttributes().getNamedItem(NODE_XSI_SCHEMA_LOCATION) == null)
            throw new IllegalArgumentException("OWS schemaLocation is undefined");
        String schemaLocation = this.resolve().getFirstChild().getAttributes().getNamedItem("xsi:schemaLocation").getNodeValue();
        if (schemaLocation.contains("www.opengis.net/wps"))
            this.serviceType = OWSServiceType.WPS;
        else if (schemaLocation.contains("www.opengis.net/wfs"))
            this.serviceType = OWSServiceType.WFS;
        else if (schemaLocation.contains("www.opengis.net/wcs"))
            this.serviceType = OWSServiceType.WCS;
        else if (schemaLocation.contains("www.opengis.net/wms"))
            this.serviceType = OWSServiceType.WMS;
        else if (schemaLocation.contains("www.opengis.net/sos"))
            this.serviceType = OWSServiceType.SOS;
        else
            throw new IllegalArgumentException("OWS schemaLocation value is not supported");
    }

    /**
     * get service type
     *
     * @return one of OWSServiceType
     */
    @NotNull
    public OWSServiceType getServiceType() {
        return serviceType;
    }

    /**
     * service types enumeration
     */
    public static enum OWSServiceType {
        WFS, WPS, WMS, WCS, SOS
    }

}
