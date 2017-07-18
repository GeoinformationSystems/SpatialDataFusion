package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.IDataCollection;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractTest {

    public void execute(AbstractOperation operation, Map<String,IData> inputs, Map<String,Class<? extends IData>> outputs) {

        this.logOperation(operation);

        Map<IIdentifier, IData> operationInputs = new HashMap<>();
        for(Map.Entry<String,IData> input : inputs.entrySet()){
            IIdentifier id = operation.getInputConnector(input.getKey()).getIdentifier();
            operationInputs.put(id, input.getValue());
        }

        Map<IIdentifier, IData> operationOutput = operation.execute(operationInputs);

        Assert.assertTrue(operation.success());
        Assert.assertNotNull(operationOutput);

        for(Map.Entry<String,Class<? extends IData>> output : outputs.entrySet()){
            IIdentifier id = operation.getOutputConnector(output.getKey()).getIdentifier();
            Assert.assertTrue(operationOutput.containsKey(id));
            IData outputData = operationOutput.get(id);
            Assert.assertTrue(output.getValue().isAssignableFrom(outputData.getClass()));
            if(outputData instanceof IDataCollection)
                Assert.assertTrue(((IDataCollection) outputData).size() > 0);
            this.logOutput(output.getKey(), outputData);
        }

        this.logRuntime(operation);

    }

    private void logOperation(AbstractOperation operation) {
        System.out.println("TEST: " + operation.getLocalIdentifier());
    }

    private void logOutput(String key, IData result) {
        System.out.print("\t" + "result: " + key + " (type=" + result.getClass().getName() +
                (result instanceof URLLiteral ? ", url=" + ((URLLiteral) result).resolve() : "") +
                (result instanceof IDataCollection ? ", size=" + ((IDataCollection) result).size() : "") + ")\n");
    }

    private void logRuntime(AbstractOperation operation) {
        //noinspection ConstantConditions
        System.out.println("\t" + "process runtime (ms): " + operation.getRuntime().resolve());
    }

}
