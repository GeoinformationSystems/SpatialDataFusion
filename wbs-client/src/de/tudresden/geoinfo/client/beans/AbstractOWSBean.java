package de.tudresden.geoinfo.client.beans;

import de.tudresden.geoinfo.client.handler.MessageHandler;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.ows.OWSServiceOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.faces.application.FacesMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * OWS Bean
 */
public abstract class AbstractOWSBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, OWSServiceOperation> handler;
    private Map<String, String> offerings;
    private Set<String> selectedOfferings;
    /**
     * variables and methods for client-side handler initialization
     */

    private String tmp_owsBase;
    private String tmp_selectedOffering;
    private OWSServiceOperation tmp_handler;

    /**
     * flag: multiple OWS handlers can be selected by client
     *
     * @return true, if multiple handlers can be selected
     */
    abstract boolean multiSelect();

    abstract OWSServiceOperation initOWSHandler(String uid, String sBaseURL) throws IOException;

    public Map<String, String> getOfferings() {
        return this.offerings;
    }

    public void addOffering(String uid, String offering) {
        if (this.offerings == null)
            this.offerings = new HashMap<>();
        this.offerings.put(uid, offering);
    }

    public Set<String> getSelectedOfferings() {
        return this.selectedOfferings;
    }

    public void setSelectedOfferings(Set<String> selectedOfferings) {
        this.selectedOfferings = selectedOfferings;
        update();
    }

    public String getSingleSelectedOffering() {
        return this.selectedOfferings == null || this.selectedOfferings.isEmpty() ? null : this.selectedOfferings.iterator().next();
    }

    public void setSingleSelectedOffering(String selection) {
        this.setSelectedOffering(selection);
    }

    public @Nullable OWSServiceOperation getHandler(@NotNull String uid) {
        return this.handler.get(uid);
    }

    public void setSelectedOffering(String selection) {
        if (this.selectedOfferings == null)
            this.selectedOfferings = new HashSet<>();
        if (!multiSelect())
            this.selectedOfferings.clear();
        this.selectedOfferings.add(selection);
        update();
    }

    private void addOWSHandler(OWSServiceOperation handler) {
        if (this.handler == null)
            this.handler = new HashMap<>();
        this.handler.put(handler.getIdentifier().toString(), handler);
        this.addOffering(handler.getIdentifier().toString(), handler.getSelectedOffering());
    }

    Set<OWSServiceOperation> getSelectedOWSHandler() {
        Set<OWSServiceOperation> handlers = new HashSet<>();
        for (OWSServiceOperation handler : this.handler.values()) {
            if (this.getSelectedOfferings().contains(handler.getIdentifier().toString()))
                handlers.add(handler);
        }
        return handlers;
    }

    /**
     * @param uid      OWS proxy id
     * @param url      OWS base url
     * @param offering selected offering
     * @param selected flag: offering is selected
     */
    public void registerOWSHandler(final String uid, final String url, final String offering, final boolean selected) {
        OWSServiceOperation handler = null;
        try {
            handler = this.initOWSHandler(uid, url);
        } catch (Exception e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "OWS Handler Error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        registerOWSHandler(handler, offering, selected);
    }

    /**
     * @param handler  OWS handler
     * @param offering selected offering
     * @param selected flag: offering is selected
     */
    public void registerOWSHandler(OWSServiceOperation handler, String offering, final boolean selected) {
        handler.setSelectedOffering(offering);
        this.addOWSHandler(handler);
        this.addOffering(handler.getIdentifier().toString(), offering);
        if (selected)
            this.setSelectedOffering(handler.getIdentifier().toString());
    }

    public @Nullable String getTmp_owsBase() {
        return this.tmp_owsBase;
    }

    public void setTmp_owsBase(@Nullable String tmp_owsBase) {
        this.tmp_owsBase = tmp_owsBase;
    }

    public @Nullable String getTmp_selectedOffering() {
        return this.tmp_selectedOffering;
    }

    public void setTmp_selectedOffering(@Nullable String tmp_selectedOffering) {
        this.tmp_selectedOffering = tmp_selectedOffering;
    }

    private void setTmp_handler(@Nullable OWSServiceOperation tmp_handler) {
        this.tmp_handler = tmp_handler;
    }

    public Set<String> getTmp_offerings() {
        return tmp_handler != null ? tmp_handler.getOfferings() : Collections.emptySet();
    }

    public void initOWSHandler() {
        //check entries
        if (this.tmp_owsBase == null || this.tmp_owsBase.isEmpty()) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_INFO, "No URL", "A OWS endpoint must be provided");
            return;
        }
        //test for valid url
        if (!this.tmp_owsBase.matches(URLLiteral.getURLRegex())) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "No valid URL", "The OWS endpoint is not a valid URL");
            return;
        }
        //try to create OWS handler
        try {
            setTmp_handler(this.initOWSHandler("_" + UUID.randomUUID(), tmp_owsBase));
        } catch (IOException e) {
            MessageHandler.sendMessage(FacesMessage.SEVERITY_ERROR, "OWS initialization error", e.getLocalizedMessage());
        }
    }

    public void addHandler() {
        registerOWSHandler(this.tmp_handler, this.tmp_selectedOffering, true);
        registerOWSOffering(this.tmp_handler, this.tmp_selectedOffering);
        this.tmp_reset();
    }

    private void tmp_reset() {
        this.setTmp_owsBase(null);
        this.setTmp_selectedOffering(null);
        this.setTmp_handler(null);
    }

    /**
     * register OWS offering on client, update display
     *
     * @param handler          OWS handler
     * @param selectedOffering OWS selected offering
     */
    public abstract void registerOWSOffering(OWSServiceOperation handler, String selectedOffering);

    /**
     * update client-side selectedOfferings display
     */
    abstract void update();

}
