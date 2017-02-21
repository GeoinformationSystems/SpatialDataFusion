package de.tudresden.geoinfo.fusion.operation.constraint;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Data binding constraint
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class BindingConstraint implements IRuntimeConstraint {

    private Set<Class<?>> supportedBindings;

    /**
     * constructor
     *
     * @param supportedBindings supported bindings
     */
    public BindingConstraint(@NotNull Set<Class<?>> supportedBindings) {
        this.supportedBindings = supportedBindings;
    }

    /**
     * constructor
     *
     * @param supportedBindings supported bindings
     */
    public BindingConstraint(@NotNull Class<?>... supportedBindings) {
        this(Sets.newHashSet(supportedBindings));
    }

    /**
     * constructor
     *
     * @param supportedBinding supported binding
     */
    public BindingConstraint(@NotNull Class<?> supportedBinding) {
        this(Sets.newHashSet(supportedBinding));
    }

    /**
     * check, if object class is compliant with supported binding
     *
     * @param object     input object
     * @param allowsNull flag: null object is allowed (e.g. in case of non-mandatory objects)
     * @return true, if class can be assigned from one of the supported bindings
     */
    public boolean compliantWith(@Nullable Object object, boolean allowsNull) {
        if (object == null)
            return allowsNull;
        for (Class<?> binding : supportedBindings) {
            if (binding.isAssignableFrom(object.getClass()))
                return true;
        }
        return false;
    }

    @Override
    public boolean compliantWith(@Nullable IData target) {
        return compliantWith(target, true);
    }

}
