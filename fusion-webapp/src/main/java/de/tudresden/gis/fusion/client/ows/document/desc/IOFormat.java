package de.tudresden.gis.fusion.client.ows.document.desc;

import java.io.Serializable;

/**
 * WPS process IO format
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IOFormat implements Serializable, Comparable<IOFormat> {
	
	private static final long serialVersionUID = 1L;
	
	private String mimetype;
	private String schema;
	private String type;
	
	public IOFormat(String mimetype, String schema, String type){
		this.mimetype = mimetype;
		this.schema = schema;
		this.type = type;
	}
	
	public IOFormat(){
		this("", "", "");
	}
	
	public String getMimetype() { return mimetype; }
	public void setMimetype(String mimetype) { this.mimetype = mimetype; }
	
	public String getSchema() { return schema; }
	public void setSchema(String schema) { this.schema = schema; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	/**
	 * compares formats based on mimetype and schema
	 */
	public boolean equals(IOFormat format){
		if(format.getMimetype().equalsIgnoreCase(getMimetype()) && 
				format.getSchema().equalsIgnoreCase(getSchema()) &&
				format.getType().equalsIgnoreCase(getType()))
			return true;
		return false;
	}
	
	//get JSON process description to be interpreted by jsPlumb
	public String getJSONDescription() {
		StringBuilder builder = new StringBuilder("{");
		builder.append(getMimetype() != "" ? "\"mimetype\" : \"" + this.getMimetype() + "\"," : "");
		builder.append(getSchema() != "" ? "\"schema\" : \"" + this.getSchema() + "\"," : "");	
		builder.append(getType() != "" ? "\"type\" : \"" + this.getType() + "\"," : "");
		if(builder.charAt(builder.length() - 1) == ',')
			builder.deleteCharAt(builder.length() - 1); //removes last comma
		builder.append("}");
		return builder.toString();
	}
	@Override
	public int compareTo(IOFormat format) {
		if(this.equals(format))
			return 0;
		else
			return 1;
	}
	
}
