package de.tudresden.gis.fusion.client;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.tudresden.gis.fusion.client.ows.WFSHandler;

@ManagedBean(name = "referenceWFS")
@SessionScoped
public class ReferenceWFS extends WFSHandler {

	private static final long serialVersionUID = 1L;

}
