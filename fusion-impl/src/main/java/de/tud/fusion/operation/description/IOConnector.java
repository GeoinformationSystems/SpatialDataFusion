package de.tud.fusion.operation.description;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tud.fusion.data.IData;
import de.tud.fusion.data.description.Description;

/**
 * IO connector implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class IOConnector extends Description implements IIOConnector {
	
	private Set<IDataConstraint> dataConstraints; 
	private Set<IDescriptionConstraint> descriptionConstraints;
	private IData data;
	
	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param title IO title
	 * @param description IO description text
	 * @param dataConstraints IO connector data constraints
	 * @param descriptionConstraints IO connector data description constraints
	 */
	public IOConnector(String identifier, String title, String description, Set<IDataConstraint> dataConstraints, Set<IDescriptionConstraint> descriptionConstraints){
		super(identifier, title, description);
		setDataConstraints(dataConstraints);
		setDescriptionConstraints(descriptionConstraints);
	}

	/**
	 * constructor
	 * @param identifier IO identifier
	 * @param title IO title
	 * @param description IO description text
	 * @param dataConstraints IO connector data constraints
	 * @param descriptionConstraints IO connector data description constraints
	 */
	public IOConnector(String identifier, String title, String description, IDataConstraint[] dataConstraints, IDescriptionConstraint[] descriptionConstraints) {
		super(identifier, title, description);
		setDataConstraints(dataConstraints);
		setDescriptionConstraints(descriptionConstraints);
	}

	/**
	 * set data constraints
	 * @param dataConstraints input constraints
	 */
	private void setDataConstraints(Set<IDataConstraint> dataConstraints) {
		this.dataConstraints = dataConstraints != null ? dataConstraints : new HashSet<IDataConstraint>();
	}
	
	/**
	 * set data constraints
	 * @param dataConstraints input constraints
	 */
	private void setDataConstraints(IDataConstraint[] dataConstraints) {
		this.dataConstraints = dataConstraints != null ? Sets.newHashSet(dataConstraints) : new HashSet<IDataConstraint>();
	}

	/**
	 * set description constraints
	 * @param descriptionConstraints input constraints
	 */
	private void setDescriptionConstraints(Set<IDescriptionConstraint> descriptionConstraints) {
		this.descriptionConstraints = descriptionConstraints != null ? descriptionConstraints : new HashSet<IDescriptionConstraint>();
	}
	
	/**
	 * set description constraints
	 * @param descriptionConstraints input constraints
	 */
	private void setDescriptionConstraints(IDescriptionConstraint[] descriptionConstraints) {
		this.descriptionConstraints = descriptionConstraints != null ? Sets.newHashSet(descriptionConstraints) : new HashSet<IDescriptionConstraint>();
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
	public Set<IDescriptionConstraint> getDescriptionConstraints() {
		return descriptionConstraints;
	}
	
	/**
	 * add a description constraint
	 * @param constraint input constraint
	 */
	protected void addDescriptionConstraint(IDescriptionConstraint constraint) {
		this.descriptionConstraints.add(constraint);
	}

	@Override
	public void connect(IData data) {
		//connect data object
		this.data = data;
		validate();
	}

	@Override
	public IData getData() {
		return data;
	}
	
	@Override
	public void validate() {
		//check data constraints
		for(IDataConstraint dataConstraint : dataConstraints){
			if(!dataConstraint.compliantWith(getData()))
				throw new IllegalArgumentException("Data object does not comply with data constraints");
		}
		//check description constraint
		for(IDescriptionConstraint descriptionConstraint : descriptionConstraints){
			if(!descriptionConstraint.compliantWith(getData().getDescription()))
				throw new IllegalArgumentException("Data description does not comply with data description constraints");
		}
	}

}
