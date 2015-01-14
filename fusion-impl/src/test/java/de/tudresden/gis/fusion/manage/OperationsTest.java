package de.tudresden.gis.fusion.manage;

import java.util.Set;

import org.junit.Test;

import de.tudresden.gis.fusion.operation.IMeasurementOperation;
import de.tudresden.gis.fusion.operation.IOperation;

public class OperationsTest {
	
	@Test
	public void testOperations() {
		
		Set<Class<? extends IOperation>> operations = Operations.getAvalaibleOperations();
		System.out.println("Available operations:");
		for(Class<? extends IOperation> op : operations) {
			System.out.println("\t" + op.getCanonicalName());
		}
		
		Set<Class<? extends IMeasurementOperation>> measurementOperations = Operations.getAvalaibleMeasurementOperations();
		System.out.println("Available measurement operations:");
		for(Class<? extends IMeasurementOperation> op : measurementOperations) {
			System.out.println("\t" + op.getCanonicalName());
		}
		
		
		
	}

}
