package de.tudresden.geoinfo.fusion.operation;

/**
 * Basic data constraint
 * @author Stefan Wiemann, TU Dresden
 *
 */
public interface IConstraint<T> {
	
	/**
	 * check whether data complies with constraint
	 * @param object object to be tested
	 * @return true, if data satisfies constraint
	 */
    boolean compliantWith(T object);

}
