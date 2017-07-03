package de.tud.fusion;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
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
     * @param feature       input feature instance
     * @param constraint    binding constraints for geometry
     * @param multiToSingle flag: convert multi-geometry with single member to single geometry (prior to constraint check)
     * @return geometry or null, if feature geometry does not match any binding constraint, is empty or null
     */
    public static Geometry getGeometryFromFeature(Feature feature, BindingConstraint constraint, boolean multiToSingle) {
        if (feature == null || feature.getDefaultGeometryProperty().getValue() == null)
            return null;
        //get default geometry from feature and check geometry binding constraint
        Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
        if (geometry.isEmpty() || !geometry.isValid())
            return null;
        //check for multi-geometry with single geometry object
        if (multiToSingle && geometry instanceof GeometryCollection && geometry.getNumGeometries() == 1)
            geometry = geometry.getGeometryN(0);
        //check binding constraint
        return constraint.compliantWith(geometry, false) ? geometry : null;
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
