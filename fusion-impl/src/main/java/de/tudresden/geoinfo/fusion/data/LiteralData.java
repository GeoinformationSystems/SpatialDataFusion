package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IRDFLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFProperty;
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
public abstract class LiteralData<T> extends Data<T> implements IRDFLiteral {

    private IRDFProperty literalType;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param value      literal value
     * @param metadata   literal metadata
     */
    public LiteralData(@NotNull IIdentifier identifier, @NotNull T value, @Nullable IMetadata metadata, @NotNull IRDFProperty literalType) {
        super(identifier, value, metadata);
        this.literalType = literalType;
    }

    /**
     * get Java binding for object string representation
     *
     * @param sLiteral string representation of object
     * @return Java binding
     */
    private @NotNull static Class<?> getLiteralBinding(@NotNull String sLiteral) {
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
    public static Object parseTypedLiteral(@NotNull String sLiteral) {
        Class<?> targetClass = getLiteralBinding(sLiteral);
        try {
            // initialize primitive (constructor with String argument)
            return targetClass.getConstructor(new Class[]{String.class}).newInstance(sLiteral);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not parse " + sLiteral, e);
        }
    }

    @NotNull
    @Override
    public String getLiteralValue() {
        return String.valueOf(this.resolve());
    }

    @NotNull
    @Override
    public IRDFProperty getLiteralType() {
        return this.literalType;
    }

    @Override
    public String toString() {
        return this.getLiteralValue();
    }

}
