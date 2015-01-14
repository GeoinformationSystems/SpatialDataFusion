package de.tudresden.gis.fusion.metadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.IData;
import de.tudresden.gis.fusion.data.ISimpleData;
import de.tudresden.gis.fusion.data.rdf.EFusionNamespace;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.INode;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
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

	public Map<IIdentifiableResource,INode> getObjectSet() {
		Map<IIdentifiableResource,INode> objectSet = super.getObjectSet();
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_IDENTIFIER.asString()), new IdentifiableResource(this.getIdentifier()));
		objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DESCRIPTION.asString()), new StringLiteral(this.getAbstract()));
		for(IDataRestriction restriction : restrictions){
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_RESTRICTION.asString()), restriction);
		}
		if(getDefault() instanceof ISimpleData)
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DEFAULT.asString()), (ISimpleData) getDefault());
		else if(getDefault() instanceof IComplexData)
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DEFAULT.asString()), (IComplexData) getDefault());
		else
			objectSet.put(new IdentifiableResource(EFusionNamespace.HAS_DEFAULT.asString()), new StringLiteral(getDefault().getDescription().getAbstract()));
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
