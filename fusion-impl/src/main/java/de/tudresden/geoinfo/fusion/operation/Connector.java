package de.tudresden.geoinfo.fusion.operation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Resource;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForConnector;

import java.util.Collections;
import java.util.Set;

/**
 * IO connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Connector extends Resource implements IConnector {
	
	private Set<IDataConstraint> dataConstraints;
	private Set<IMetadataConstraint> metadataConstraints;
	private IData data;
	private IMetadataForConnector metadata;
	
	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param dataConstraints IO connector data constraints
	 * @param descriptionConstraints IO connector data description constraints
	 */
	public Connector(IIdentifier identifier, IMetadataForConnector metadata, Set<IDataConstraint> dataConstraints, Set<IMetadataConstraint> descriptionConstraints){
		super(identifier);
		this.metadata = metadata;
		setDataConstraints(dataConstraints);
		setDescriptionConstraints(descriptionConstraints);
	}

	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param dataConstraints IO connector data constraints
	 * @param descriptionConstraints IO connector data description constraints
	 */
	public Connector(IIdentifier identifier, IMetadataForConnector metadata, IDataConstraint[] dataConstraints, IMetadataConstraint[] descriptionConstraints) {
		this(identifier, metadata, dataConstraints != null ? Sets.newHashSet(dataConstraints) : null, descriptionConstraints != null ? Sets.newHashSet(descriptionConstraints) : null);
	}

	/**
	 * set data constraints
	 * @param dataConstraints input constraints
	 */
	private void setDataConstraints(Set<IDataConstraint> dataConstraints) {
		this.dataConstraints = dataConstraints != null ? dataConstraints : Collections.emptySet();
	}

	/**
	 * set description constraints
	 * @param descriptionConstraints input constraints
	 */
	private void setDescriptionConstraints(Set<IMetadataConstraint> descriptionConstraints) {
		this.metadataConstraints = descriptionConstraints != null ? descriptionConstraints : Collections.emptySet();
	}
	
	@Override
	public Set<IDataConstraint> getDataConstraints() {
		return dataConstraints;
	}
	
	/**
	 * add a data constraint
	 * @param constraint input constraint
	 */
	protected void addDataConstraint(IDataConstraint constraint) {
		this.dataConstraints.add(constraint);
	}

	@Override
	public Set<IMetadataConstraint> getMetadataConstraints() {
		return metadataConstraints;
	}
	
	/**
	 * add a description constraint
	 * @param constraint input constraint
	 */
	protected void addDescriptionConstraint(IMetadataConstraint constraint) {
		this.metadataConstraints.add(constraint);
	}

	@Override
	public void connect(IData data) {
        //validate
	    validate(data);
	    //connect data object
		this.data = data;
	}

	@Override
	public IData getData() {
		return this.data;
	}
	
	@Override
	public boolean isConnected(){
		return this.data != null;
	}

	@Override
    public boolean isValid() {
	    try {
            validate(getData());
        } catch (IllegalArgumentException e) {
	    	e.printStackTrace();
	        return false;
        }
        return true;
    }

	/**
	 * validate connected data object with constraints
	 */
	private void validate(IData data) throws IllegalArgumentException {
		//check data constraints
		for(IDataConstraint dataConstraint : dataConstraints){
			if(!dataConstraint.compliantWith(data))
				throw new IllegalArgumentException("Data object " + (data != null ? data.getClass() : "null") + " does not comply with data constraint: " + dataConstraint.getClass());
		}
		//check description constraint
		for(IMetadataConstraint metadataConstraint : metadataConstraints){
			if(!metadataConstraint.compliantWith(data.getMetadata()))
				throw new IllegalArgumentException("Metadata for " + (data != null ? data.getClass() : "null") + " does not comply with data metadata constraint: " + metadataConstraint.getClass());
		}
	}

    @Override
    public IMetadataForConnector getMetadata() {
        return this.metadata;
    }

}
