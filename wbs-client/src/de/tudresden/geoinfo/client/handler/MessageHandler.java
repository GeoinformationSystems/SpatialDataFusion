package de.tudresden.geoinfo.client.handler;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * JSF Message Handler
 */
public class MessageHandler implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * append message to faces context
     *
     * @param severity message severity level
     * @param summary  message string
     * @param detail   detailed message string
     */
    public static void sendMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, summary, detail));
    }

}
