package de.tudresden.geoinfo.fusion.operation.constraint;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFLiteral;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * String pattern constraint
 *
 * @author Stefan Wiemann, TU Dresden
 */
public class PatternConstraint implements IRuntimeConstraint {

    private String pattern;

    /**
     * constructor
     *
     * @param pattern input pattern
     */
    public PatternConstraint(@NotNull String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean compliantWith(@Nullable IData data) {
        return data == null || data instanceof IRDFLiteral && ((IRDFLiteral) data).getLiteralValue().matches(pattern);
    }

}
