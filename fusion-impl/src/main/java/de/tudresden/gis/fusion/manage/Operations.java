package de.tudresden.gis.fusion.manage;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.operation.IOperationProfile;
import de.tudresden.gis.fusion.operation.IMeasurementOperation;
import de.tudresden.gis.fusion.operation.IOperation;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class Operations {

	/**
	 * get available fusion operations
	 * @return available fusion operations
	 */
	public static Set<Class<? extends IOperation>> getOperations() {
		Reflections reflections = new Reflections("de.tudresden.gis.fusion.operation");
		Set<Class<? extends IOperation>> ops = reflections.getSubTypesOf(IOperation.class);
		Iterator<Class<? extends IOperation>> iter = ops.iterator();
		while(iter.hasNext()){
			Class<? extends IOperation> op = iter.next();
			if(op.isInterface() || Modifier.isAbstract(op.getModifiers()))
				iter.remove();
		}
		return ops;
	}
	
	/**
	 * get available fusion measurement operations
	 * @return available fusion measurement operations
	 */
	public static Set<Class<? extends IMeasurementOperation>> getRelationMeasurementOperations() {
		Reflections reflections = new Reflections("de.tudresden.gis.fusion.operation");
		Set<Class<? extends IMeasurementOperation>> ops = reflections.getSubTypesOf(IMeasurementOperation.class);
		Iterator<Class<? extends IMeasurementOperation>> iter = ops.iterator();
		while(iter.hasNext()){
			Class<? extends IMeasurementOperation> op = iter.next();
			if(op.isInterface() || Modifier.isAbstract(op.getModifiers()))
				iter.remove();
		}
		return ops;
	}
	
	/**
	 * check if input data complies with operation profile
	 * @param profile operation profile
	 * @param input input data
	 * @return true if input data complies with profile, false otherwise
	 */
	public static boolean profileAllowsInput(IOperationProfile profile, Map<String,IData> input){
		Collection<IIODescription> ioDesc = profile.getInputDescriptions();
		for(IIODescription desc : ioDesc){
			String id = desc.getIdentifier();
			Collection<IIORestriction> ioRestrictions = desc.getDataRestrictions();
			for(IIORestriction restriction : ioRestrictions){
				//return false, if input fails one of the restrictions
				if(!restriction.compliantWith(input.get(id)))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * get operations that take the provided data as input
	 * @param input data
	 * @return set of operations that take the provided input data as input
	 */
	public static Set<Class<? extends IOperation>> getSuitableOperations(Map<String,IData> input){
		Set<Class<? extends IOperation>> ops = getOperations();
		Iterator<Class<? extends IOperation>> iter = ops.iterator();
		while(iter.hasNext()){
			Class<? extends IOperation> op = iter.next();
			try {
				if(!profileAllowsInput(op.newInstance().getProfile(), input))
					iter.remove();
			} catch (Exception e) {
				iter.remove();
			}
		}
		return ops;
	}
	
}
