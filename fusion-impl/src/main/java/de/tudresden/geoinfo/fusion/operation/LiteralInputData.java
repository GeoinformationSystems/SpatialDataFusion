package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.ows.IOFormat;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.constraint.IOFormatConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Literal workflow input
 */
public class LiteralInputData extends InputData<LiteralData> {

    private final static IOFormat DEFAULT_FORMAT = new IOFormat(null, null, "xs:string");
    private final static String DEFAULT_CONNECTOR_TITLE = "Literal Data Input";
    private final static String DEFAULT_NODE_TITLE = "Literal Data Input Node";

    /**
     * constructor
     *
     * @param data data object map (title --> data object)
     */
    public LiteralInputData(@Nullable IIdentifier identifier, @NotNull Map<String, LiteralData> data, @NotNull String nodeTitle) {
        super(identifier, data, nodeTitle);
    }

    /**
     * constructor
     *
     * @param data data object
     * @param connectorTitle title of data connector
     * @param nodeTitle title of node
     */
    public LiteralInputData(@Nullable IIdentifier identifier, @NotNull LiteralData data, @NotNull String connectorTitle, @NotNull String nodeTitle) {
        super(identifier, data, connectorTitle, nodeTitle);
    }

    /**
     * constructor
     *
     * @param data data object
     */
    public LiteralInputData(@NotNull LiteralData data) {
        this(null, data, DEFAULT_CONNECTOR_TITLE, DEFAULT_NODE_TITLE);
    }

    /**
     * constructor
     *
     * @param data data object map (title --> data object)
     */
    public LiteralInputData(@NotNull Map<String, LiteralData> data) {
        this(null, data, DEFAULT_NODE_TITLE);
    }

    @Override
    protected void initializeOutputConnectors() {
        super.initializeOutputConnectors();
        for(IOutputConnector connector : this.getOutputConnectors()){
            if(connector.getData() == null)
                //should not happen
                throw new RuntimeException("Literal data output was not set");
            connector.getConnectionConstraints().add(getLiteralFormatConstraint((LiteralData) connector.getData()));
        }
    }

    /**
     * get format constraint
     * @return literal format constraint
     */
    private @NotNull IConnectionConstraint getLiteralFormatConstraint(@NotNull LiteralData literal) {
        return new IOFormatConstraint(this.getSupportedFormats(literal));
    }

    /**
     * get supported formats for literal value
     * @return supported formats
     */
    private @NotNull Set<IOFormat> getSupportedFormats(LiteralData literal) {
        Set<IOFormat> supportedFormats = new HashSet<>();
        supportedFormats.add(DEFAULT_FORMAT);
        if(literal instanceof BooleanLiteral || this.isBoolean(literal))
            supportedFormats.add(new IOFormat(null, null, "xs:boolean"));
        if(literal instanceof IntegerLiteral || this.isInteger(literal))
            supportedFormats.add(new IOFormat(null, null, "xs:integer"));
        if(literal instanceof DecimalLiteral || this.isDecimal(literal))
            supportedFormats.add(new IOFormat(null, null, "xs:double"));
        return supportedFormats;
    }

    /**
     * get string representation of literal data
     * @param literal input literal
     * @return string representation of input literal
     */
    private @NotNull String getLiteralString(@NotNull LiteralData literal) {
        return literal.resolve().toString();
    }

    /**
     * check if literal can be parse as boolean
     * @return true, if data matches ^(?i)(true|false|0|1)$
     */
    private boolean isBoolean(@NotNull LiteralData literal) {
        return this.getLiteralString(literal).matches("^(?i)(true|false|0|1)$");
    }

    /**
     * check if literal can be parse as integer
     * @return true, if data matches ^\d+$
     */
    private boolean isInteger(@NotNull LiteralData literal) {
        return this.getLiteralString(literal).matches("^\\d+$");
    }

    /**
     * check if literal can be parse as double
     * @return true, if data matches ^\d+$
     */
    private boolean isDecimal(@NotNull LiteralData literal) {
        return this.getLiteralString(literal).matches("^\\d+\\.?\\d*$");
    }

}
