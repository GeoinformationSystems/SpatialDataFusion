package de.tudresden.gis.fusion.data.description;

public class DataDescription implements IDataDescription {

	String title;
	String abstrakt;
	IDataProvenance provenance;
	
	public DataDescription(String title, String abstrakt, IDataProvenance provenance){
		this.title = title;
		this.abstrakt = abstrakt;
		this.provenance = provenance;
	}

	@Override
	public IDataProvenance getProvenance() {
		return provenance;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getAbstract() {
		return abstrakt;
	}

}
