package de.tudresden.gis.fusion.operation.retrieval;

import java.io.IOException;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;

import de.tudresden.gis.fusion.data.geotools.GTFeatureCollection;
import de.tudresden.gis.fusion.data.geotools.GTIndexedFeatureCollection;
import de.tudresden.gis.fusion.data.rdf.IIRI;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.IRI;
import de.tudresden.gis.fusion.data.rdf.IdentifiableResource;
import de.tudresden.gis.fusion.data.restrictions.ERestrictions;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.manage.EProcessType;
import de.tudresden.gis.fusion.manage.Namespace;
import de.tudresden.gis.fusion.metadata.data.IIODescription;
import de.tudresden.gis.fusion.metadata.data.IODescription;
import de.tudresden.gis.fusion.operation.AOperation;
import de.tudresden.gis.fusion.operation.IDataRetrieval;
import de.tudresden.gis.fusion.operation.ProcessException;
import de.tudresden.gis.fusion.operation.ProcessException.ExceptionKey;
import de.tudresden.gis.fusion.operation.io.IIORestriction;
import de.tudresden.gis.fusion.operation.io.IFilter;

public class ShapefileParser extends AOperation implements IDataRetrieval {
	
	private final String IN_RESOURCE = "IN_RESOURCE";
	private final String IN_WITH_INDEX = "IN_WITH_INDEX";
	
	private final String OUT_FEATURES = "OUT_FEATURES";
	
	private final IIdentifiableResource PROCESS_RESOURCE = new IdentifiableResource(Namespace.uri_process() + "/" + this.getProcessTitle());
	private final IIdentifiableResource[] PROCESS_CLASSIFICATION = new IIdentifiableResource[]{
			EProcessType.RETRIEVAL.resource()
	};

	@Override
	public void execute() {

		URILiteral shapeResource = (URILiteral) getInput(IN_RESOURCE);
		BooleanLiteral inWithIndex = (BooleanLiteral) getInput(IN_WITH_INDEX);
		
		IIRI identifier = new IRI(shapeResource.getIdentifier());
		boolean bWithIndex = inWithIndex == null ? false : inWithIndex.getValue();
		
		GTFeatureCollection shapeFC;
		try {
			ShapefileDataStore store = new ShapefileDataStore(identifier.asURL());
	        String name = store.getTypeNames()[0];
	        SimpleFeatureSource source = store.getFeatureSource(name);
	        if(bWithIndex)
	        	shapeFC = new GTIndexedFeatureCollection(identifier, DataUtilities.collection(source.getFeatures().features()));
	        else
	        	shapeFC = new GTFeatureCollection(identifier, DataUtilities.collection(source.getFeatures().features()));
	        store.dispose();
		} catch (IOException e) {
			throw new ProcessException(ExceptionKey.GENERAL_EXCEPTION, e);
		}
        
		setOutput(OUT_FEATURES, shapeFC);
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
	protected String getProcessDescription() {
		return "Parser for ESRI Shapefiles";
	}

	@Override
	protected IIODescription[] getInputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				IN_RESOURCE, "Shapefile resource",
				new IIORestriction[]{
					ERestrictions.BINDING_URIRESOURCE.getRestriction(),
					ERestrictions.MANDATORY.getRestriction()
				}
			),
			new IODescription(
				IN_WITH_INDEX, "if set true, a spatial index is build",
				new BooleanLiteral(true),
				new IIORestriction[]{
					ERestrictions.BINDING_BOOLEAN.getRestriction()
				}
			)
		};				
	}

	@Override
	protected IIODescription[] getOutputDescriptions() {
		return new IIODescription[]{
			new IODescription(
				OUT_FEATURES, "Output features",
				new IIORestriction[]{
					ERestrictions.MANDATORY.getRestriction(),
					ERestrictions.BINDING_IFEATUReCOLLECTION.getRestriction()
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
