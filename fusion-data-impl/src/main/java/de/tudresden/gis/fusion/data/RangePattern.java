package de.tudresden.gis.fusion.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.tudresden.gis.fusion.data.description.IMeasurementRange;
import de.tudresden.gis.fusion.data.literal.StringLiteral;
import de.tudresden.gis.fusion.data.rdf.Resource;

/**
 * string range using regular expression
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class RangePattern extends Resource implements IMeasurementRange {

	/**
	 * regular expression pattern
	 */
	private Pattern pattern;
	
	/**
	 * string range
	 */
	private transient Collection<IMeasurement> range;
	
	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param pattern string regex pattern
	 * @throws PatternSyntaxException
	 */
	public RangePattern(String identifier, String pattern) throws PatternSyntaxException {
		super(identifier);
		this.pattern = Pattern.compile(pattern);
	}
	
	/**
	 * constructor
	 * @param pattern string regex pattern
	 * @throws PatternSyntaxException
	 */
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
