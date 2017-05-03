package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.AbstractOWSHandler;
import de.tudresden.geoinfo.client.handler.WMSHandler;
import org.primefaces.context.RequestContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name="basemap")
@SessionScoped
public class Basemap extends AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    boolean multiSelect() {
        return true;
    }

    @Override
    WMSHandler initOWSHandler(String uid, String sBaseURL) throws IOException {
        return new WMSHandler(uid, sBaseURL);
    }

    @Override
    public void registerOWSOffering(AbstractOWSHandler handler, String selectedOffering) {
        RequestContext.getCurrentInstance().execute("f_registerWMSBasemapFromJSF(" + handler.getIdentifier() + ",olMap,'" + handler.getBase().toString() + "','" + selectedOffering + "')");
        update();
    }

    @Override
    void update() {
        //update layer view
        StringBuilder sLayers = new StringBuilder();
        if (this.getSelectedOfferings() != null && !this.getSelectedOfferings().isEmpty()) {
            for (String layer : this.getSelectedOfferings()) {
                sLayers.append(layer).append(";");
            }
        }
        //call javascript update
        if (sLayers.length() > 0)
            RequestContext.getCurrentInstance().execute("olMap.updateBasemapSelection('" + sLayers.substring(0, sLayers.length() - 1) + "')");
    }

    @Override
    public void registerOWSHandler(final String uid, final String url, final String offering, final boolean selected) {
        //add key, if url is empty (e.g. for OSM basemap)
        if (url == null || url.isEmpty()) {
            this.addOffering(uid, offering);
            if (selected)
                this.setSelectedOffering(uid);
        }
        else
            super.registerOWSHandler(uid, url, offering, selected);
    }

}
