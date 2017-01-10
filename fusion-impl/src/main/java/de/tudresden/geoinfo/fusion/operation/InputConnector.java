package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForConnector;

import java.util.Set;

/**
 * Input connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class InputConnector extends Connector implements IInputConnector {
	
	private IData defaultData;

	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param dataConstraints connector data constraints
	 * @param descriptionConstraints connector data description constraints
	 * @param defaultData default data object
	 */
	public InputConnector(IIdentifier identifier, IMetadataForConnector metadata, Set<IDataConstraint> dataConstraints, Set<IMetadataConstraint> descriptionConstraints, IData defaultData) {
		super(identifier, metadata, dataConstraints, descriptionConstraints);
		this.defaultData = defaultData;
	}

    /**
     * constructor
     * @param identifier IO identifier
     * @param dataConstraints connector data constraints
     * @param descriptionConstraints connector data description constraints
     * @param defaultData default data object
     */
    public InputConnector(IIdentifier identifier, IMetadataForConnector metadata, IDataConstraint[] dataConstraints, IMetadataConstraint[] descriptionConstraints, IData defaultData) {
        super(identifier, metadata, dataConstraints, descriptionConstraints);
		this.defaultData = defaultData;
    }

	@Override
	public IData getDefault() {
		return defaultData;
	}
	
	@Override
	public IData getData() {
		return super.getData() != null ? super.getData() : getDefault();
	}

	@Override
	public boolean isConnected(){
		return super.isConnected() || this.getDefault() != null;
	}

}
