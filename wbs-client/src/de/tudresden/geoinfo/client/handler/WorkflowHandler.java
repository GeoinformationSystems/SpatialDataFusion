package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.metadata.DC_Metadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IOutputConnector;
import de.tudresden.geoinfo.fusion.operation.IWorkflowConnector;
import de.tudresden.geoinfo.fusion.operation.IWorkflowNode;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class WorkflowHandler {

    private final static String MSG_SUCCESS = "<span class='validation_valid'>Connections are valid</span>";
    private final static String WORKFLOW_PROCESSES = "processes";
    private final static String WORKFLOW_CONNECTIONS = "connections";
    private final static String JSON_IDENTIFIER = "identifier";
    private final static String JSON_TITLE = "title";
    private final static String PROCESS_INPUTS = "inputs";
    private final static String PROCESS_OUTPUTS = "outputs";
    private final static String PROCESS_LITERAL_INPUTS = "LiteralInputs";
    private final static String OUTPUT_NODE = "Output";
    private final static String OUTPUT_DESCRIPTION = "Output of the process";
    private final static String IO_FORMATS = "supportedFormats";
    private final static String IO_MINOCCURS = "minOccurs";
    private final static String FORMAT_TYPE = "type";
    private final static String FORMAT_SCHEMA = "schema";
    private final static String FORMAT_MIMETYPE = "mimetype";
    private final static String CONNECTION_S_ID = "s_identifier";
    private final static String CONNECTION_T_ID = "t_identifier";
    private final static String CONNECTION_S_OUT = "s_output";
    private final static String CONNECTION_T_IN = "t_input";

    private Workflow workflow;
    private JSONObject jsonWorkflowDescription;
    private String validationMessage;
    private Map<IIdentifier,IData> outputs;

    public void setWorkflowDescription(Map<String, IWorkflowNode> nodes, String sWorkflowDescription) {
        this.jsonWorkflowDescription = new JSONObject(sWorkflowDescription);
        this.setValidationMessage();
        if (isValidDescription()) {
            //initialize workflow with connections
            this.workflow = initWorkflow(nodes);
        }
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public Map<IIdentifier,IData> getOutput() {
        return this.outputs;
    }

    public CamundaBPMNModel getBPMNModel() {
        return new CamundaBPMNModel(null, this.getWorkflow());
    }

    private JSONArray getProcesses() {
        return this.jsonWorkflowDescription.getJSONArray(WORKFLOW_PROCESSES);
    }

    private JSONArray getConnections() {
        return this.jsonWorkflowDescription.getJSONArray(WORKFLOW_CONNECTIONS);
    }

    public boolean isValidDescription() {
        return this.getValidationMessage().equals(MSG_SUCCESS);
    }

    public String getValidationMessage() {
        return this.validationMessage;
    }

    public void setValidationMessage() {
        StringBuilder builder = new StringBuilder();
        for (Object process : this.getProcesses()) {
            setProcessValidationMessage((JSONObject) process, builder);
        }
        setConnectionsValidationMessage(builder);
        if (builder.length() == 0)
            builder.append(MSG_SUCCESS);
        this.validationMessage = builder.toString();
    }

    private void setProcessValidationMessage(JSONObject process, StringBuilder builder) {
        JSONArray inputs = process.getJSONArray(PROCESS_INPUTS);
        for (Object input : inputs) {
            String identifier = ((JSONObject) input).getString(JSON_IDENTIFIER);
            if (((JSONObject) input).getInt(IO_MINOCCURS) > 0) {
                boolean check = false;
                for (Object connection : this.getConnections()) {
                    if (connection != null && ((JSONObject) connection).get("t_input").toString().equals(identifier))
                        check = true;
                }
                if (!check)
                    builder.append("<span class='validation_hint'>Input '").append(identifier).append("' must be connected</span><br />");
            }
        }
    }

    private void setConnectionsValidationMessage(StringBuilder builder) {
        for (Object connection : this.getConnections()) {
            if (connection != null) {
                String sourceId = ((JSONObject) connection).getString(CONNECTION_S_ID);
                String targetId = ((JSONObject) connection).getString(CONNECTION_T_ID);
                String sourceIOId = ((JSONObject) connection).getString(CONNECTION_S_OUT);
                String targetIOId = ((JSONObject) connection).getString(CONNECTION_T_IN);
                JSONArray sourceFormats = getSupportedFormats(sourceId, sourceIOId, false);
                JSONArray targetFormats = getSupportedFormats(targetId, targetIOId, true);
                if (!containCommonFormat(sourceFormats, targetFormats))
                    builder.append("<span class='validation_error'>Connection ").append(sourceIOId).append(" : ").append(targetIOId).append(" is not valid (no common format)</span>");
            }
        }
    }

    private JSONArray getSupportedFormats(String processId, String ioId, boolean input) {
        for (Object process : this.getProcesses()) {
            if (!(((JSONObject) process).getString(JSON_IDENTIFIER).equals(processId)))
                continue;
            if (input)
                return getSupportedFormats(((JSONObject) process).getJSONArray(PROCESS_INPUTS), ioId);
            else
                return getSupportedFormats(((JSONObject) process).getJSONArray(PROCESS_OUTPUTS), ioId);
        }
        //should not happen
        return new JSONArray();
    }

    private JSONArray getSupportedFormats(JSONArray ioArray, String ioId) {
        for (Object jIO : ioArray) {
            if (!(((JSONObject) jIO).getString(JSON_IDENTIFIER).equals(ioId)))
                continue;
            return ((JSONObject) jIO).getJSONArray(IO_FORMATS);
        }
        //should not happen
        return new JSONArray();
    }

    private boolean containCommonFormat(JSONArray sourceFormats, JSONArray targetFormats) {
        for (Object jsourceFormat : sourceFormats) {
            for (Object jtargetFormat : targetFormats) {
                if (commonFormat((JSONObject) jsourceFormat, (JSONObject) jtargetFormat))
                    return true;
            }
        }
        return false;
    }

    private boolean commonFormat(JSONObject jsourceFormat, JSONObject jtargetFormat) {
        if (this.isWildcard(jsourceFormat) || this.isWildcard(jtargetFormat))
            return true;
        boolean common = false;
        if (!jsourceFormat.isNull(FORMAT_MIMETYPE) && !jtargetFormat.isNull(FORMAT_MIMETYPE))
            common = equals(jsourceFormat.getString(FORMAT_MIMETYPE), jtargetFormat.getString(FORMAT_MIMETYPE));
        if (!jsourceFormat.isNull(FORMAT_SCHEMA) && !jtargetFormat.isNull(FORMAT_SCHEMA))
            common = equals(jsourceFormat.getString(FORMAT_SCHEMA), jtargetFormat.getString(FORMAT_SCHEMA));
        if (!jsourceFormat.isNull(FORMAT_TYPE) && !jtargetFormat.isNull(FORMAT_TYPE))
            common = equals(jsourceFormat.getString(FORMAT_TYPE), jtargetFormat.getString(FORMAT_TYPE));
        return common;
    }

    private boolean isWildcard(JSONObject jFormat) {
        return !jFormat.isNull(FORMAT_MIMETYPE) && jFormat.getString(FORMAT_MIMETYPE).equals("*") &&
                !jFormat.isNull(FORMAT_SCHEMA) && jFormat.getString(FORMAT_SCHEMA).equals("*") &&
                !jFormat.isNull(FORMAT_TYPE) && jFormat.getString(FORMAT_TYPE).equals("*");
    }

    private boolean equals(@NotNull String io1, @NotNull String io2) {
        return io1.equalsIgnoreCase(io2);
    }

    private Workflow initWorkflow(Map<String, IWorkflowNode> nodes) {

        Set<IOutputConnector> outputConnectors = new HashSet<>();

        //add connections
        for (Object connection : this.getConnections()) {

            //get connection target node
            IWorkflowNode targetNode = getNode(nodes, connection, CONNECTION_T_ID);
            if (targetNode == null) {
                //set output data node
                if (isWorkflowOutputConnection((JSONObject) connection)) {
                    IWorkflowNode sourceNode = this.getNode(nodes, connection, CONNECTION_S_ID);
                    IOutputConnector out = (IOutputConnector) this.getConnector(sourceNode, connection, CONNECTION_S_OUT, false);
                    if(out != null)
                        outputConnectors.add(out);
                    continue;
                }
            }

            //get target connector
            IInputConnector in = (IInputConnector) this.getConnector(targetNode, connection, CONNECTION_T_IN, true);
            if (in == null)
                throw new IllegalArgumentException("Input connector " + getConnectionString(connection, CONNECTION_T_IN) + " not found");

            //get connection source node
            IWorkflowNode sourceNode = this.getNode(nodes, connection, CONNECTION_S_ID);
            if (sourceNode == null){
                //add literal data to input connector
                if(getConnectionString(connection, CONNECTION_S_ID).equals(PROCESS_LITERAL_INPUTS))
                    in.setData(getInputLiteral(getConnectionString(connection, CONNECTION_S_OUT)));
                continue;
            }

            //get source connector
            IOutputConnector out = (IOutputConnector) this.getConnector(sourceNode, connection, CONNECTION_S_OUT, false);
            if (out == null)
                throw new IllegalArgumentException("Output connector " + getConnectionString(connection, CONNECTION_S_OUT) + " not found");

            //establish connection
            in.connect(out);
        }
        Workflow workflow = new Workflow(null, nodes.values());
        workflow.initializeOutputConnectors(outputConnectors);
        return workflow;
    }

    private @Nullable LiteralData getInputLiteral(String literalIdentifier) {
        for (Object process : this.getProcesses()) {
            if ((((JSONObject) process).getString(JSON_IDENTIFIER).equals(PROCESS_LITERAL_INPUTS))){
                return getInputLiteral(((JSONObject) process), literalIdentifier);
            }
        }
        return null;
    }

    private @Nullable LiteralData getInputLiteral(JSONObject literalProcess, String literalIdentifier){
        JSONArray outputs = literalProcess.getJSONArray(PROCESS_OUTPUTS);
        for(Object output : outputs){
            if(((JSONObject) output).getString(JSON_IDENTIFIER).equals(literalIdentifier))
                return new StringLiteral(((JSONObject) output).getString(JSON_TITLE));
        }
        return null;
    }

    private @Nullable IWorkflowNode getNode(Map<String, IWorkflowNode> nodes, Object connection, String identifier){
        return nodes.get(getConnectionString(connection, identifier));
    }

    private @Nullable IWorkflowConnector getConnector(IWorkflowNode node, Object connection, String identifier, boolean isInput){
        return isInput ? node.getInputConnector(getConnectionString(connection, identifier)) : node.getOutputConnector(getConnectionString(connection, identifier));
    }

    private String getConnectionString(Object connection, String identifier){
        return ((JSONObject) connection).getString(identifier);
    }

    private boolean isWorkflowOutputConnection(JSONObject connection) {
        return connection.getString(CONNECTION_T_ID).equals(OUTPUT_NODE);
    }

    private LiteralData getLiteralData(JSONObject output) {
        return new StringLiteral(new Identifier(output.getString(JSON_IDENTIFIER)), output.getString(JSON_TITLE), null);
    }

    public void execute() {
        this.outputs = this.getWorkflow().execute(null);
    }

    public String getResultMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<h3>Results for Workflow: ").append(this.getWorkflow().getIdentifier()).append("</h3>");
        for (Map.Entry<IIdentifier,IData> output : this.getOutput().entrySet()) {
            builder.append("<span class='validation_valid'>")
                    .append(output.getValue().getMetadata() != null && output.getValue().getMetadata().hasElement(DC_Metadata.DC_TITLE.getResource()) ?
                            output.getValue().getMetadata().getElement(DC_Metadata.DC_TITLE.getResource()).getValue() :
                            output.getKey().toString())
                    .append(": </span><span>")
                    .append(output.getValue().resolve())
                    .append("</span><br />");
        }
        return builder.toString();
    }

}
