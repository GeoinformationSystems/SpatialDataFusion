package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OSMFeatureCollection<T extends OSMVectorFeature> extends AbstractFeatureCollection<T> {
	
	private transient Set<OSMNode> nodes;
	private transient Set<OSMWay> ways;

	/**
	 * constructor
	 * @param sIdentifier resource identifier
	 * @param featureCollection GeoTools GTVectorFeature collection
	 * @param description collection description
	 */
	public OSMFeatureCollection(IIdentifier sIdentifier, Collection<T> featureCollection, IMetadataForData description){
		super(sIdentifier, featureCollection, description);
	}
	
	/**
	 * get OSM nodes
	 * @return OSM nodes in the collection
	 */
	public OSMFeatureCollection<OSMNode> getNodes() {
		if(nodes == null){
			nodes = new HashSet<>();
			for(OSMVectorFeature feature : this.resolve()){
				if(feature instanceof OSMNode){
					nodes.add((OSMNode) feature); 
				}
			}
		}
		return new OSMFeatureCollection<>(getIdentifier(), nodes, getMetadata());
	}
	
	/**
	 * get OSM ways
	 * @return OSM ways in the collection
	 */
	public OSMFeatureCollection<OSMWay> getWays() {
		if(ways == null){
			ways = new HashSet<>();
			for(OSMVectorFeature feature : this.resolve()){
				if(feature instanceof OSMWay){
					ways.add((OSMWay) feature); 
				}
			}
		}
		return new OSMFeatureCollection<>(getIdentifier(), ways, getMetadata());
	}

    @Override
	public Envelope getBounds() {
		double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
		for(OSMNode feature : nodes){
			if(minLat > feature.getLat())
				minLat = feature.getLat();
			if(maxLat < feature.getLat())
				maxLat = feature.getLat();
			if(minLon > feature.getLon())
				minLon = feature.getLon();
			if(maxLon < feature.getLon())
				maxLon = feature.getLon();
		}
		return new ReferencedEnvelope(minLat, maxLat, minLon, maxLon, getReferenceSystem());
	}

    @Override
    public CoordinateReferenceSystem getReferenceSystem() {
        return DefaultGeographicCRS.WGS84;
    }

}
