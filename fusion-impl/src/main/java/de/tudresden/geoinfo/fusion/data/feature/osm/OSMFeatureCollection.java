package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OSMFeatureCollection<T extends OSMVectorFeature> extends AbstractFeatureCollection<T> {

    private Set<OSMNode> nodes;
    private Set<OSMWay> ways;

    /**
     * constructor
     *
     * @param identifier       resource identifier
     * @param features OSM features
     */
    public OSMFeatureCollection(@NotNull IIdentifier identifier, @NotNull Collection<T> features, @Nullable IMetadata metadata) {
        super(identifier, features, metadata);
    }

    /**
     * get OSM nodes
     *
     * @return OSM nodes in the collection
     */
    @NotNull
    public Set<OSMNode> getNodes() {
        if (this.nodes.isEmpty()) {
            for (OSMVectorFeature feature : this) {
                if (feature instanceof OSMNode) {
                    this.nodes.add((OSMNode) feature);
                }
            }
        }
        return this.nodes;
    }

    /**
     * get OSM ways
     *
     * @return OSM ways in the collection
     */
    @NotNull
    public Set<OSMWay> getWays() {
        if(this.ways.isEmpty()) {
            for (OSMVectorFeature feature : this) {
                if (feature instanceof OSMWay) {
                    this.ways.add((OSMWay) feature);
                }
            }
        }
        return this.ways;
    }

    @Override
    public boolean add(T data){
        if(this.nodes == null)
            this.nodes = new HashSet<>();
        if(this.ways == null)
            this.ways = new HashSet<>();

        if(data instanceof OSMNode)
            this.nodes.add((OSMNode) data);
        else if(data instanceof OSMWay)
            this.ways.add((OSMWay) data);

        return super.add(data);
    }

    @Override
    @NotNull
    public Envelope getBounds() {
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
        for (OSMNode feature : nodes) {
            if (minLat > feature.getLat())
                minLat = feature.getLat();
            if (maxLat < feature.getLat())
                maxLat = feature.getLat();
            if (minLon > feature.getLon())
                minLon = feature.getLon();
            if (maxLon < feature.getLon())
                maxLon = feature.getLon();
        }
        return new ReferencedEnvelope(minLat, maxLat, minLon, maxLon, getReferenceSystem());
    }

    @Override
    @NotNull
    public CoordinateReferenceSystem getReferenceSystem() {
        return DefaultGeographicCRS.WGS84;
    }

}
