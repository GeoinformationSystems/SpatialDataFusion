package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.ITypedLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LiteralData data implementation
 */
public class LiteralData<T> extends Data<T> implements ITypedLiteral<T> {

    private IResource dataType;

    /**
     * constructor
     *
     * @param identifier literal identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public LiteralData(@Nullable IIdentifier identifier, @NotNull T value, @Nullable IMetadata metadata, IResource dataType) {
        super(identifier, value, metadata);
        this.dataType = dataType;
    }

    /**
     * constructor
     *
     * @param identifier  literal identifier
     * @param value       literal value
     * @param title       literal title
     * @param description literal description
     */
    public LiteralData(@Nullable IIdentifier identifier, @NotNull T value, @NotNull String title, @Nullable String description, IResource dataType) {
        this(identifier, value, new Metadata(title, description), dataType);
    }

    /**
     * get Java binding for object string representation
     *
     * @param sLiteral string representation of object
     * @return Java binding
     */
    private static Class<?> getLiteralBindingFromString(@NotNull String sLiteral) {
        //check boolean
        if (sLiteral.matches("^(?i)(true|false)$"))
            return Boolean.class;
            // check integer
        else if (sLiteral.matches("^\\d{1,9}$"))
            return Integer.class;
            // check big integer
        else if (sLiteral.matches("^\\d+$"))
            return BigInteger.class;
            // check float
        else if (sLiteral.matches("^\\d+\\.?\\d*$"))
            return Double.class;
        // check date
        try {
            new SimpleDateFormat().parse(sLiteral);
            return Date.class;
        } catch (ParseException e) {
            // do nothing
        }
        // define as string
        return String.class;
    }

    /**
     * parse Java primitive from String
     *
     * @param sLiteral input String
     * @return Java primitive
     */
    @NotNull
    public static Object parseObjectFromString(@NotNull String sLiteral) {
        Class<?> targetClass = getLiteralBindingFromString(sLiteral);
        try {
            // initialize primitive (constructor with String argument)
            return targetClass.getConstructor(new Class[]{String.class}).newInstance(sLiteral);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not parse " + sLiteral, e);
        }
    }

    @NotNull
    @Override
    public String getLiteral() {
        return String.valueOf(this.resolve());
    }

    @NotNull
    @Override
    public IResource getLiteralType() {
        return this.dataType;
    }

    @Override
    public String toString() {
        return this.resolve().toString();
    }
}
