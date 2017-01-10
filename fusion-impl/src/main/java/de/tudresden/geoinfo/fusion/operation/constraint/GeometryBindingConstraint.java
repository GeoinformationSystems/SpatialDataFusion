package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.IFeature;
import de.tudresden.geoinfo.fusion.operation.IDataConstraint;

import java.util.Set;

/**
 * Geometry data binding
 */
public class GeometryBindingConstraint implements IDataConstraint {

    private Set<Class<?>> geometryBindings;

    /**
     * constructor
     * @param geometryBindings supported bindings
     */
    public GeometryBindingConstraint(Set<Class<?>> geometryBindings){
        this.geometryBindings = geometryBindings;
    }

    /**
     * constructor
     * @param geometryBindings supported bindings
     */
    public GeometryBindingConstraint(Class<?>[] geometryBindings){
        this(Sets.newHashSet(geometryBindings));
    }

    /**
     * constructor
     * @param geometryBinding supported binding
     */
    public GeometryBindingConstraint(Class<?> geometryBinding){
        this(Sets.newHashSet(geometryBinding));
    }

    @Override
    public boolean compliantWith(IData target){
        if(target instanceof IFeature){
            Object geometry = ((IFeature) target).getRepresentation().getDefaultGeometry();
            for(Class<?> binding : geometryBindings){
                if(binding.isAssignableFrom(geometry.getClass()))
                    return true;
            }
        }
        return false;
    }
}
