package de.tudresden.gis.fusion.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.tudresden.gis.fusion.data.description.IMeasurementRange;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.rdf.Resource;

public class RangePattern extends Resource implements IMeasurementRange {

	private Pattern pattern;
	private transient Collection<IMeasurement> range;
	
	public RangePattern(String identifier, String pattern) throws PatternSyntaxException {
		super(identifier);
		this.pattern = Pattern.compile(pattern);
	}
	
	public RangePattern(String pattern) throws PatternSyntaxException {
		this(null, pattern);
	}
	
	@Override
	public Collection<IMeasurement> getRange() {
		if(range == null){
			range = new HashSet<IMeasurement>();
			range.add(new StringLiteral(pattern.pattern()));
		}
		return range;
	}

	@Override
	public boolean isContinuous() {
		return false;
	}

	@Override
	public StringLiteral getMin() {
		return null;
	}

	@Override
	public StringLiteral getMax() {
		return null;
	}

	@Override
	public boolean contains(IMeasurement target) {
		return pattern.matcher(target.resolve().toString()).matches();
	}

}
