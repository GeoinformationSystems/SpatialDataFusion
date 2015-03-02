package de.tudresden.gis.fusion.manage;

public class Namespace {
	
	public static String prefix_global() { return "fusion"; }
	public static String uri_global() {	return "http://tu-dresden.de/uw/geo/gis/" + prefix_global(); }
	
	public static String prefix_restriction() { return "restriction"; }
	public static String uri_restriction() { return uri_global() + "/" + prefix_restriction(); }
	
	public static String prefix_process() { return "process"; }	
	public static String uri_process() { return uri_global() + "/" + prefix_process(); }
	
	public static String prefix_relation() { return "relation"; }
	public static String uri_relation() { return uri_global() + "/" + prefix_relation(); }

	public static String prefix_measurement() { return "measurement"; }
	public static String uri_measurement() { return uri_global() + "/" + prefix_measurement(); }
	
}
