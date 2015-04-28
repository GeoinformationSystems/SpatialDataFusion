package de.tudresden.gis.fusion.operation.retrieval;

import java.io.InputStream;

import org.camunda.bpm.model.bpmn.Bpmn;

import de.tudresden.gis.fusion.data.complex.BPMNModel;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IFilter;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public class BPMNParser extends AOperation implements IDataRetrieval {
	
	public final String IN_RESOURCE = "IN_RESOURCE";
	public final String OUT_BPMN = "OUT_BPMN";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};

	@Override
	protected void execute() {
		
		URILiteral rdfResource = (URILiteral) getInput(IN_RESOURCE);		
		IIRI identifier = new IRI(rdfResource.getIdentifier());
		
		BPMNModel bpmn;
		try {
			bpmn = parseBPMN(identifier, identifier.asURL().openStream());
		} catch (Exception e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
        
		setOutput(OUT_BPMN, bpmn);
		
	}

	private BPMNModel parseBPMN(IIRI iri, InputStream stream) {
		return new BPMNModel(iri, Bpmn.readModelFromStream(stream));
	}

	@Override
	public void setFilter(IFilter filter) {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getProcessTitle() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getProcessAbstract() {
		return "Parser for BPMN model";
	}

	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
				new IODescription(
					IN_RESOURCE, "RDF relations resource",
					new IIORestriction[]{
						ERestrictions.BINDING_URIRESOURCE.getRestriction(),
						ERestrictions.MANDATORY.getRestriction()
					}
				),
		};			
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription (
				OUT_BPMN, "Output BPMN model",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_BPMN.getRestriction()
				}
			)
		};
	}
	
	@Override
	protected IIdentifiableResource getResource() {
		return PROCESS_RESOURCE;
	}

	@Override
	protected IIdentifiableResource[] getClassification() {
		return PROCESS_CLASSIFICATION;
	}

}
