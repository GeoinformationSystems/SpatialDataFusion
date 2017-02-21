package de.tudresden.geoinfo.fusion.data.feature.osm;

import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
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

    private transient Set<OSMNode> nodes;
    private transient Set<OSMWay> ways;

    /**
     * constructor
     *
     * @param sIdentifier       resource identifier
     * @param featureCollection GeoTools GTVectorFeature collection
     */
    public OSMFeatureCollection(@Nullable IIdentifier sIdentifier, @NotNull Collection<T> featureCollection, @Nullable IMetadata metadata) {
        super(sIdentifier, featureCollection, metadata);
    }

    /**
     * get OSM nodes
     *
     * @return OSM nodes in the collection
     */
    @NotNull
    public OSMFeatureCollection<OSMNode> getNodes() {
        if (nodes == null) {
            nodes = new HashSet<>();
            for (OSMVectorFeature feature : this.resolve()) {
                if (feature instanceof OSMNode) {
                    nodes.add((OSMNode) feature);
                }
            }
        }
        return new OSMFeatureCollection<>(this.getIdentifier(), nodes, null);
    }

    /**
     * get OSM ways
     *
     * @return OSM ways in the collection
     */
    @NotNull
    public OSMFeatureCollection<OSMWay> getWays() {
        if (ways == null) {
            ways = new HashSet<>();
            for (OSMVectorFeature feature : this.resolve()) {
                if (feature instanceof OSMWay) {
                    ways.add((OSMWay) feature);
                }
            }
        }
        return new OSMFeatureCollection<>(getIdentifier(), ways, null);
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
