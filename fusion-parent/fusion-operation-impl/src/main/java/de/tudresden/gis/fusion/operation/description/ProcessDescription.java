package de.tudresden.gis.fusion.operation.description;

import java.util.Collection;

import de.tudresden.gis.fusion.data.IRI;
import de.tudresden.gis.fusion.data.rdf.RDFResource;
import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;

public class ProcessDescription extends RDFResource  implements IProcessDescription {
	
	private String title, abstrakt;
	private Collection<IProcessConstraint> constraints;
	
	public ProcessDescription(IRI identifier, String title, String abstrakt, Collection<IProcessConstraint> constraints){
		super(identifier);
		this.title = title;
		this.abstrakt = abstrakt;
		this.constraints = constraints;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String abstrakt() {
		return abstrakt;
	}

	@Override
	public Collection<IProcessConstraint> constraints() {
		return constraints;
	}

}
