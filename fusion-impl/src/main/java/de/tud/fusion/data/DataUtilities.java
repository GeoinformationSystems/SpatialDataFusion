package de.tud.fusion.data;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtilities {

	/**
	 * get matching Java binding for object string representation
	 * @param sPrimitive string representation of object
	 * @return Java binding
	 */
	public static Class<?> getObjectBindingFromString(String literal){
		//check boolean
		if(literal.matches("^(?i)(true|false)$"))
			return Boolean.class;
		// check integer
		else if(literal.matches("^\\d{1,9}$"))
			return Integer.class;
		// check big integer
		else if(literal.matches("^\\d+$"))
			return BigInteger.class;
		// check float
		else if(literal.matches("^\\d+\\.?\\d*$"))
			return Double.class;	
		// check date
		try {
			new SimpleDateFormat().parse(literal);
			return Date.class;
		} catch (ParseException e) {
			// do nothing
		}
		// define as string
		return String.class;
	}
	
	/**
	 * parse Java primitive from String
	 * @param sPrimitive input String
	 * @return Java primitive
	 */
	public static Object parseObjectFromString(String literal){
		Class<?> targetClass = getObjectBindingFromString(literal);
		try {
			// initialize primitive (constructor with String argument)
			return targetClass.getConstructor(new Class[]{String.class}).newInstance(literal);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Could not parse " + literal, e);
		}
	}
	
}
