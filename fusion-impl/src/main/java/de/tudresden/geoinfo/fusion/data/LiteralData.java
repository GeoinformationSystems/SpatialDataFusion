package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.ITypedLiteral;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LiteralData data implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class LiteralData<T> implements IData, ITypedLiteral {

    private T value;
    private IMetadataForData metadata;
    private IResource type;

	/**
	 * constructor
	 * @param value literal value
	 * @param metadata literal data metadata
	 */
	public LiteralData(T value, IResource type, IMetadataForData metadata) {
        if(value == null)
            throw new IllegalArgumentException("LiteralData must not be null");
        this.value = value;
        this.type = type;
		this.metadata = metadata;
	}

	@Override
	public String getValue() {
		return String.valueOf(resolve());
	}

	@Override
	public T resolve() {
		return value;
	}

    @Override
    public IResource getType() {
        return type;
    }

    @Override
    public IMetadataForData getMetadata() {
        return metadata;
    }

    /**
     * get Java binding for object string representation
     * @param sLiteral string representation of object
     * @return Java binding
     */
    public static Class<?> getObjectBindingFromString(String sLiteral){
        //check boolean
        if(sLiteral.matches("^(?i)(true|false)$"))
            return Boolean.class;
            // check integer
        else if(sLiteral.matches("^\\d{1,9}$"))
            return Integer.class;
            // check big integer
        else if(sLiteral.matches("^\\d+$"))
            return BigInteger.class;
            // check float
        else if(sLiteral.matches("^\\d+\\.?\\d*$"))
            return Double.class;
        // check date
        try {
            new SimpleDateFormat().parse(sLiteral);
            return Date.class;
        } catch (ParseException e) {
            // do nothing
        }
        // define as string
        return String.class;
    }

    /**
     * parse Java primitive from String
     * @param sLiteral input String
     * @return Java primitive
     */
    public static Object parseObjectFromString(String sLiteral){
        Class<?> targetClass = getObjectBindingFromString(sLiteral);
        try {
            // initialize primitive (constructor with String argument)
            return targetClass.getConstructor(new Class[]{String.class}).newInstance(sLiteral);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not parse " + sLiteral, e);
        }
    }

}
