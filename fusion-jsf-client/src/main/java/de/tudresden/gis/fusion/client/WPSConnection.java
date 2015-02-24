package de.tudresden.gis.fusion.client;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.tudresden.gis.fusion.client.ows.WPSHandler;

@ManagedBean(name = "wpsConnection")
@SessionScoped
public class WPSConnection extends WPSHandler implements Serializable {

	private static final long serialVersionUID = 1L;

}
