package de.tudresden.gis.fusion.data.rdf;

import de.tudresden.gis.fusion.data.IRI;

public class RDFResource extends RDFIdentifiableResource implements IRDFResource {

	public RDFResource(IRI identifier) {
		super(identifier);
	}

	@Override
	public boolean isBlank() {
		return this.getIdentifier() == null;
	}

}
