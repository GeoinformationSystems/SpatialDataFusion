package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Units;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForOperation;
import de.tudresden.geoinfo.fusion.metadata.MetadataForConnector;
import de.tudresden.geoinfo.fusion.metadata.MetadataForMeasurement;
import de.tudresden.geoinfo.fusion.metadata.MetadataForOperation;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract operation implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public abstract class AbstractOperation extends Resource implements IOperation {

    private static final IIdentifier OUT_START = new Identifier("OUT_START");
    private static final IIdentifier OUT_RUNTIME = new Identifier("OUT_RUNTIME");

    private static IResource TIME_TYPE = Objects.LONG.getResource();
    private static IResource TIME_OBJECT = Objects.TIME_INSTANT.getResource();
    private static IResource TIME_UNIT = Units.MILLISECOND.getResource();

	private Map<IIdentifier,IInputConnector> inputConnectors;
	private Map<IIdentifier,IOutputConnector> outputConnectors;
	
	/**
	 * constructor
	 * @param identifier operation identifier
	 */
	public AbstractOperation(IIdentifier identifier){
		super(identifier);
        //init connectors
        inputConnectors = initInputConnectors();
        outputConnectors = initOutputConnectors();
        amendOutputConnectors();
	}

	@Override
	public Map<IIdentifier,IData> execute(Map<IIdentifier,IData> inputs) {
		//set inputs
		connectInputs(inputs);
		//validateInputs
		if(!validateInputs()){
			throw new IllegalArgumentException("Could not execute process: no valid inputs");
		}
		//set start time
		setStartTime();
		//execute (set output connectors)
		execute();
        //set runtime
        setRuntime();
		//validateOutputs
        if(!validateOutputs()){
            throw new IllegalArgumentException("Could not execute process: no valid outputs");
        }

		//return result
		return getOutputs();
	}

	/**
	 * get input connectors
	 */
	protected abstract Map<IIdentifier,IInputConnector> initInputConnectors();
	
	/**
	 * get output connectors
	 */
    protected abstract Map<IIdentifier,IOutputConnector> initOutputConnectors();
	
	/**
	 * amend output connectors with start time and runtime
	 */
	private void amendOutputConnectors(){
		//add process start time
		outputConnectors.put(OUT_START, new OutputConnector(
				OUT_START,
                new MetadataForConnector(OUT_START.toString(), "Start time of the operation"),
                new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(LongLiteral.class)},
				null));
		//add process runtime
		outputConnectors.put(OUT_RUNTIME, new OutputConnector(
				OUT_RUNTIME,
                new MetadataForConnector(OUT_RUNTIME.toString(), "Runtime of the operation"),
                new IDataConstraint[]{
						new MandatoryConstraint(),
						new BindingConstraint(LongLiteral.class)},
				null));
	}

	/**
	 * set input connectors
	 * @param inputs process inputs
	 */
	private void connectInputs(Map<IIdentifier,IData> inputs) {
		for(Map.Entry<IIdentifier,IData> input : inputs.entrySet()){
			connectInput(input.getKey(), input.getValue());
		}
	}
	
	/**
	 * set input connector
	 * @param input process input
	 */
	protected void connectInput(IIdentifier identifier, IData input) {
		IInputConnector connector = getInputConnector(identifier);
		if(connector != null)
			connector.connect(input);	
	}
	
	/**
	 * validate input connectors
	 */
	private boolean validateInputs() {
		for(IInputConnector inputConnector : inputConnectors.values()){
			if(!inputConnector.isValid()){
			    return false;
            }
		}
		return true;
	}
	
	/**
	 * set output connector
	 * @param output process output
	 */
	protected void connectOutput(IIdentifier identifier, IData output) {
		IOutputConnector connector = getOutputConnector(identifier);
		if(connector != null)
			connector.connect(output);
	}

	@Override
	public IInputConnector getInputConnector(IIdentifier identifier) {
		return inputConnectors.get(identifier);
	}

    @Override
	public IOutputConnector getOutputConnector(IIdentifier identifier) {
		return outputConnectors.get(identifier);
	}

	/**
	 * set start time for this process
	 */
	private void setStartTime() {
		connectOutput(OUT_START, new LongLiteral(System.currentTimeMillis(),
				new MetadataForMeasurement(
                        OUT_START.toString(),
						"Start time of the operation in Unix time",
                        TIME_TYPE,
                        TIME_OBJECT,
						LongLiteral.getMaxRange(), 
						TIME_UNIT)));
	}
	
	/**
	 * get start time of the last process
	 * @return latest process start time
	 */
	public LongLiteral getStartTime(){
		return (LongLiteral) getOutputConnector(OUT_START).getData();
	}
	
	/**
	 * set runtime of the process
	 */
	private void setRuntime(){
		connectOutput(OUT_RUNTIME, new LongLiteral(System.currentTimeMillis() - getStartTime().resolve(),
				new MetadataForMeasurement(
						OUT_RUNTIME.toString(),
						"Runtime of the operation in milliseconds",
                        TIME_TYPE,
                        TIME_OBJECT,
						LongLiteral.getPositiveRange(),
                        TIME_UNIT)));
	}

    /**
     * get start time of the last process
     * @return latest process start time
     */
    public LongLiteral getRuntime(){
        return (LongLiteral) getOutputConnector(OUT_RUNTIME).getData();
    }

	/**
	 * validate output connectors
	 */
	private boolean validateOutputs() {
		for(IOutputConnector outputConnector : outputConnectors.values()){
			if(!outputConnector.isValid())
			    return false;
		}
		return true;
	}
	
	/**
	 * get operation outputs
	 * @return operation outputs
	 */
	private Map<IIdentifier,IData> getOutputs() {
		Map<IIdentifier,IData> outputs = new HashMap<>();
		for(IOutputConnector outputConnector : outputConnectors.values()){
			outputs.put(outputConnector.getIdentifier(), outputConnector.getData());
		}
		return outputs;
	}

	/**
	 * execution of the process
	 */
	public abstract void execute();
	
	@Override
	public IMetadataForOperation getMetadata() {
		return new MetadataForOperation(
				getProcessTitle(),
				getProcessAbstract(),
                inputConnectors.keySet(),
				outputConnectors.keySet());
	}
	
	/**
	 * get process title
	 * @return process title
	 */
	public abstract String getProcessTitle();
	
	/**
	 * get process description
	 * @return process description
	 */
	public abstract String getProcessAbstract();

}
