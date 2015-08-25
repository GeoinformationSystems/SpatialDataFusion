package de.tudresden.gis.fusion.data;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RangePattern implements IRange<String> {

	private Pattern pattern;
	
	public RangePattern(String pattern) throws PatternSyntaxException {
		this.pattern = Pattern.compile(pattern);
	}
	
	@Override
	public String[] valueRange() {
		return new String[]{pattern.pattern()};
	}

	@Override
	public boolean continuous() {
		return false;
	}

	@Override
	public String min() {
		return null;
	}

	@Override
	public String max() {
		return null;
	}

	@Override
	public boolean contains(String target) {
		return pattern.matcher(target).matches();
	}

}
