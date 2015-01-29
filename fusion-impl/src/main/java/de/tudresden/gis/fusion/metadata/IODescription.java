package de.tudresden.gis.fusion.metadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.manage.DataUtilities;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;
import de.tudresden.gis.fusion.operation.metadata.IIODescription;

public class IODescription extends DataDescription implements IIODescription {
	
	private IIRI iri;
	private IData defaultIO;
	private Collection<IDataRestriction> restrictions;
	
	public IODescription(IIRI iri, String description, IData defaultIO, Collection<IDataRestriction> restrictions){
		super(description);
		this.iri = iri;
		this.defaultIO = defaultIO;
		this.restrictions = restrictions;
	}
	
	public IODescription(IIRI iri, String description, Collection<IDataRestriction> restrictions){
		this(iri, description, null, restrictions);
	}
	
	public IODescription(IIRI iri, String description, IData defaultIO, IDataRestriction[] restrictions){
		super(description);
		this.iri = iri;
		this.defaultIO = defaultIO;
		this.restrictions = Arrays.asList(restrictions);
	}
	
	public IODescription(IIRI iri, String description, IDataRestriction[] restrictions){
		this(iri, description, null, restrictions);
	}

	public Map<IIdentifiableResource,Set<INode>> getObjectSet() {
		Map<IIdentifiableResource,Set<INode>> objectSet = super.getObjectSet();
		objectSet.put(EFusionNamespace.HAS_IDENTIFIER.resource(), DataUtilities.toSet(new IdentifiableResource(this.getIdentifier())));
		objectSet.put(EFusionNamespace.HAS_DESCRIPTION.resource(), DataUtilities.toSet(new StringLiteral(this.getAbstract())));
		objectSet.put(EFusionNamespace.HAS_RESTRICTION.resource(), DataUtilities.collectionToSet(restrictions));
		if(getDefault() instanceof ISimpleData)
			objectSet.put(EFusionNamespace.HAS_DEFAULT.resource(), DataUtilities.toSet((ISimpleData) getDefault()));
		else if(getDefault() instanceof IComplexData)
			objectSet.put(EFusionNamespace.HAS_DEFAULT.resource(), DataUtilities.toSet((IComplexData) getDefault()));
		else
			objectSet.put(EFusionNamespace.HAS_DEFAULT.resource(), DataUtilities.toSet(new StringLiteral(getDefault().getDescription().getAbstract())));
		return objectSet;
	}

	@Override
	public IData getDefault() {
		return defaultIO;
	}

	@Override
	public Collection<IDataRestriction> getDataRestrictions() {
		return restrictions;
	}

	@Override
	public IIRI getIdentifier() {
		return iri;
	}

}
