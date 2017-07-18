package de.tud.fusion;

import com.vividsolutions.jts.geom.*;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.data.feature.IFeatureRepresentation;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.Feature;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Set of utility methods
 */
public class Utilities {

    /**
     * get JTS linestring geometry from OpenGIS feature
     *
     * @param geometry       input feature geometry
     * @param constraint    binding constraints for geometry
     * @param multiToSingle flag: convert multi-geometry with single member to single geometry (prior to constraint check)
     * @return geometry or null, if feature geometry does not match any binding constraint, is empty or null
     */
    public static @Nullable Geometry getGeometry(@NotNull Geometry geometry, @Nullable BindingConstraint constraint, boolean multiToSingle) {
        if (geometry.isEmpty() || !geometry.isValid())
            return null;
        //check for multi-geometry with single geometry object
        if (multiToSingle && geometry instanceof GeometryCollection && geometry.getNumGeometries() == 1)
            geometry = geometry.getGeometryN(0);
        //check binding constraint
        return constraint == null || constraint.compliantWith(geometry, false) ? geometry : null ;
    }

    /**
     *
     * @param feature input feature
     * @param constraint binding constraints for geometry
     * @param multiToSingle: convert multi-geometry with single member to single geometry (prior to constraint check)
     * @return geometry or null, if feature geometry does not match any binding constraint, is empty or null
     */
    public static @Nullable Geometry getGeometry(@NotNull Feature feature, @Nullable BindingConstraint constraint, boolean multiToSingle){
        Object geometry = feature.getDefaultGeometryProperty().getValue();
        return geometry != null && geometry instanceof Geometry ? getGeometry((Geometry) geometry, constraint, multiToSingle) : null;
    }

    /**
     *
     * @param featureRepresentation input feature representation
     * @param constraint binding constraints for geometry
     * @param multiToSingle: convert multi-geometry with single member to single geometry (prior to constraint check)
     * @return geometry or null, if feature geometry does not match any binding constraint, is empty or null
     */
    public static @Nullable Geometry getGeometry(@NotNull IFeatureRepresentation featureRepresentation, @Nullable BindingConstraint constraint, boolean multiToSingle){
        Object geometry = featureRepresentation.getDefaultGeometry();
        return geometry != null && geometry instanceof Geometry ? getGeometry((Geometry) geometry, constraint, multiToSingle) : null;
    }

    /**
     *
     * @param feature input feature
     * @param constraint binding constraints for geometry
     * @param multiToSingle: convert multi-geometry with single member to single geometry (prior to constraint check)
     * @return geometry or null, if feature geometry does not match any binding constraint, is empty or null
     */
    public static @Nullable Geometry getGeometry(@NotNull IFeature feature, @Nullable BindingConstraint constraint, boolean multiToSingle){
        IFeatureRepresentation featureRepresentation = feature.getRepresentation();
        return featureRepresentation != null ? getGeometry(featureRepresentation, constraint, multiToSingle) : null;
    }

    /**
     *
     * @param feature input feature
     * @return linestring geometry or null, if feature geometry is empty or not a linestring
     */
    public static @Nullable Geometry getGeometry(@NotNull IFeature feature){
        return getGeometry(feature, null, true);
    }

    /**
     *
     * @param feature input feature
     * @return linestring geometry or null, if feature geometry is empty or not a linestring
     */
    public static @Nullable Envelope getEnvelope(@NotNull IFeature feature){
        Geometry geometry = getGeometry(feature, null, false);
        return geometry != null ? geometry.getEnvelopeInternal() : null;
    }

    /**
     *
     * @param feature input feature
     * @return linestring geometry or null, if feature geometry is empty or not a linestring
     */
    public static @Nullable Point getPoint(@NotNull IFeature feature){
        return (Point) getGeometry(feature, new BindingConstraint(Point.class), true);
    }

    /**
     *
     * @param feature input feature
     * @return linestring geometry or null, if feature geometry is empty or not a linestring
     */
    public static @Nullable LineString getLineString(@NotNull IFeature feature){
        return (LineString) getGeometry(feature, new BindingConstraint(LineString.class), true);
    }

    /**
     *
     * @param feature input feature
     * @return linestring geometry or null, if feature geometry is empty or not a linestring
     */
    public static @Nullable Polygon getPolygon(@NotNull IFeature feature){
        return (Polygon) getGeometry(feature, new BindingConstraint(Polygon.class), true);
    }

    /**
     * create a temporary file
     * @param name file name
     * @param suffix file suffix
     * @return file object
     * @throws IOException if file could not be created
     */
    public static @NotNull File createTempFile(@Nullable String name, @Nullable String suffix) throws IOException {
        return File.createTempFile(
                name != null ? name : UUID.randomUUID().toString(),
                suffix != null ? (suffix.startsWith(".") ? suffix : "." + suffix) : ".tmp");
    }

}
