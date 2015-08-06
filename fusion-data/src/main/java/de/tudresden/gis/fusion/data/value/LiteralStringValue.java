package de.tudresden.gis.fusion.data.value;

import de.tudresden.gis.fusion.data.type.LiteralStringType;

public class LiteralStringValue implements ILiteralValue {

	private String content;
	private LiteralStringType type;
	
	/**
	 * constructor
	 * @param content String content of this value
	 */
	public LiteralStringValue(String content) {
		this.content = content;
		this.type = new LiteralStringType();
	}
	
	@Override
	public String getContent() {
		return content;
	}
	
	/**
	 * set content of this String value
	 * @param content new value
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public LiteralStringType getType() {
		return type;
	}

}
