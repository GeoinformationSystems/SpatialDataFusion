package de.tudresden.gis.fusion.manage;

import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;

public enum EProcessType {
	
	//process classification
	SEARCH("#dataSearch"),
	RETRIEVAL("#dataRetrieval"),
	ENHANCEMENT("#dataEnhancement"),
	HARMONIZATION("#dataHarmonization"),
	RELATION("#relationMeasurement"),
	MAPPING("#featureMapping"),
	RESOLVING("#resolving"),
	PROVISION("#dataProvision"),
	
	//enhancement operations
	OP_ENH_GEOM_REP("/dataEnhancement/feature/geometry#repair"),
	OP_ENH_GEOM_SEG("/dataEnhancement/feature/geometry#segmentation"),
	
	//harmonization operations
	OP_HAR_CRS("/dataHarmonization/transformation/feature/property/spatial#crs"),

	//relation measurement operations - shape
	OP_REL_PROP_ORIENTATION("/relationMeasurement/feature/property/spatial/shape#orientation"),
	OP_REL_PROP_AREA("/relationMeasurement/feature/property/spatial/shape#area"),
	OP_REL_PROP_LENGTH("/relationMeasurement/feature/property/spatial/shape#length"),
	OP_REL_PROP_SINUOSITY("/relationMeasurement/feature/property/spatial/shape#sinuosity"),
	
	//relation measurement operations - location
	OP_REL_PROP_LOC("/relationMeasurement/feature/property/spatial#location"),
	
	//relation measurement operations - topology
	OP_REL_PROP_TOPO("/relationMeasurement/feature/property/spatial#topology"),
	
	//relation measurement operations - thematic
	OP_REL_PROP_STRING("/relationMeasurement/feature/property/thematic#string"),
	
	//resolving operations
	OP_RES_TRANSFER_ATT("/resolving/transfer#property"),
	
	;
	
	private IIdentifiableResource resource;
	
	private EProcessType(String identifier){
		this.resource = new IdentifiableResource(Namespace.uri_process() + identifier);
	}
	
	public IIdentifiableResource resource(){
		return resource;
	}
		
}
