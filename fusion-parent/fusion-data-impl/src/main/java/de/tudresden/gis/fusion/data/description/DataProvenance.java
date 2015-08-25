package de.tudresden.gis.fusion.data.description;

import java.util.LinkedList;
import java.util.List;

import de.tudresden.gis.fusion.data.rdf.IRDFIdentifiableResource;

public class DataProvenance implements IDataProvenance {
	
	private List<IRDFIdentifiableResource> processes;
	
	public DataProvenance(List<IRDFIdentifiableResource> processes){
		this.processes = processes;
	}
	
	public DataProvenance(IRDFIdentifiableResource process){
		initList();
		this.processes.add(process);
	}
	
	/**
	 * initialize process list
	 */
	private void initList(){
		this.processes = new LinkedList<IRDFIdentifiableResource>();
	}

	/**
	 * add a process identifier to lineage
	 * @param process process identifier
	 */
	public void addProcessToLineage(IRDFIdentifiableResource process){
		if(processes == null)
			initList();
		processes.add(process);
	}
	
	@Override
	public List<IRDFIdentifiableResource> processLineage() {
		return processes;
	}

}
