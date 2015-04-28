package de.tudresden.gis.fusion.data.complex;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IRDFRepresentation;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.Resource;
import de.tudresden.gis.fusion.metadata.data.IDescription;

public class BPMNModel extends Resource implements IComplexData {
	
	private BpmnModelInstance bpmnModel;
	
	public BPMNModel(IIRI iri, BpmnModelInstance bpmnModel){
		super(iri);
		this.bpmnModel = bpmnModel;
	}
	
	public BPMNModel(BpmnModelInstance bpmnModel){
		this(new IRI(bpmnModel.getDefinitions().getId()), bpmnModel);
	}
	
	public BpmnModelInstance getBpmnModelInstance() {
		return bpmnModel;
	}

	@Override
	public IDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRDFRepresentation getRDFRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
