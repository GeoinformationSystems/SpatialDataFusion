package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Geometry data binding
 */
public class GeometryBindingConstraint implements IRuntimeConstraint {

    private Set<Class<?>> geometryBindings;

    /**
     * constructor
     *
     * @param geometryBindings supported bindings
     */
    public GeometryBindingConstraint(@NotNull Set<Class<?>> geometryBindings) {
        this.geometryBindings = geometryBindings;
    }

    /**
     * constructor
     *
     * @param geometryBindings supported bindings
     */
    public GeometryBindingConstraint(@NotNull Class<?>[] geometryBindings) {
        this(Sets.newHashSet(geometryBindings));
    }

    /**
     * constructor
     *
     * @param geometryBinding supported binding
     */
    public GeometryBindingConstraint(@NotNull Class<?> geometryBinding) {
        this(Sets.newHashSet(geometryBinding));
    }

    @Override
    public boolean compliantWith(@Nullable IData target) {
        if (target == null)
            return true;
        if (target instanceof IFeature) {
            Object geometry = ((IFeature) target).getRepresentation().getDefaultGeometry();
            for (Class<?> binding : geometryBindings) {
                if (binding.isAssignableFrom(geometry.getClass()))
                    return true;
            }
        }
        return false;
    }
}
