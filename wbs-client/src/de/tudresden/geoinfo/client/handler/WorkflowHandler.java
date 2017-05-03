package de.tudresden.geoinfo.client.handler;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.operation.*;
import de.tudresden.geoinfo.fusion.operation.workflow.CamundaBPMNModel;
import de.tudresden.geoinfo.fusion.operation.workflow.Workflow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class WorkflowHandler {

    private CamundaBPMNModel bmpnModel;
    private JSONObject jsonWorkflowDescription;

    private String validationMessage;
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

    public void setWorkflowDescription(Map<String,IWorkflowNode> nodes, String sWorkflowDescription){
        this.jsonWorkflowDescription = new JSONObject(sWorkflowDescription);
        this.setValidationMessage();
        if(isValidDescription()) {
            //add literal node
            IWorkflowNode literalNode = getLiteralNode();
            if(literalNode != null)
                nodes.put(literalNode.getIdentifier().toString(), literalNode);
            //add output node
            IWorkflowNode outputNode = getOutputNode();
            nodes.put(outputNode.getIdentifier().toString(), outputNode);
            //initialize workflow
            IWorkflow workflow = initWorkflow(nodes);
            bmpnModel = new CamundaBPMNModel(null, "BPMN Model", null);
            bmpnModel.initModel(workflow);
            System.out.println(bmpnModel.asXML());
        }
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
        for(Object process : this.getProcesses()){
            setProcessValidationMessage((JSONObject) process, builder);
        }
        setConnectionsValidationMessage(builder);
        if(builder.length() == 0)
            builder.append(MSG_SUCCESS);
        this.validationMessage = builder.toString();
    }

    private void setProcessValidationMessage(JSONObject process, StringBuilder builder){
        JSONArray inputs = process.getJSONArray(PROCESS_INPUTS);
        for(Object input : inputs){
            String identifier = ((JSONObject) input).getString(JSON_IDENTIFIER);
            if(((JSONObject) input).getInt(IO_MINOCCURS) > 0){
                boolean check = false;
                for(Object connection : this.getConnections()){
                    if(connection != null && ((JSONObject) connection).get("t_input").toString().equals(identifier))
                        check = true;
                }
                if(!check)
                    builder.append("<span class='validation_hint'>Input '").append(identifier).append("' must be connected</span><br />");
            }
        }
    }

    private void setConnectionsValidationMessage(StringBuilder builder){
        for(Object connection : this.getConnections()){
            if(connection != null){
                String sourceId = ((JSONObject) connection).getString(CONNECTION_S_ID);
                String targetId = ((JSONObject) connection).getString(CONNECTION_T_ID);
                String sourceIOId = ((JSONObject) connection).getString(CONNECTION_S_OUT);
                String targetIOId = ((JSONObject) connection).getString(CONNECTION_T_IN);
                JSONArray sourceFormats = getSupportedFormats(sourceId, sourceIOId, false);
                JSONArray targetFormats = getSupportedFormats(targetId, targetIOId, true);
                if(!containCommonFormat(sourceFormats, targetFormats))
                    builder.append("<span class='validation_error'>Connection ").append(sourceIOId).append(" : ").append(targetIOId).append(" is not valid (no common format)</span>");
            }
        }
    }

    private JSONArray getSupportedFormats(String processId, String ioId, boolean input) {
        for(Object process : this.getProcesses()){
            if(!(((JSONObject) process).getString(JSON_IDENTIFIER).equals(processId)))
                continue;
            if(input)
                return getSupportedFormats(((JSONObject) process).getJSONArray(PROCESS_INPUTS), ioId);
            else
                return getSupportedFormats(((JSONObject) process).getJSONArray(PROCESS_OUTPUTS), ioId);
        }
        //should not happen
        return new JSONArray();
    }

    private JSONArray getSupportedFormats(JSONArray ioArray, String ioId) {
        for(Object jIO : ioArray){
            if(!(((JSONObject) jIO).getString(JSON_IDENTIFIER).equals(ioId)))
                continue;
            return ((JSONObject) jIO).getJSONArray(IO_FORMATS);
        }
        //should not happen
        return new JSONArray();
    }

    private boolean containCommonFormat(JSONArray sourceFormats, JSONArray targetFormats) {
        for(Object jsourceFormat : sourceFormats){
            for(Object jtargetFormat : targetFormats){
                if(commonFormat((JSONObject) jsourceFormat, (JSONObject) jtargetFormat))
                    return true;
            }
        }
        return false;
    }

    private boolean commonFormat(JSONObject jsourceFormat, JSONObject jtargetFormat) {
        if(this.isWildcard(jsourceFormat) || this.isWildcard(jtargetFormat))
            return true;
        boolean common = false;
        if(!jsourceFormat.isNull(FORMAT_MIMETYPE) && !jtargetFormat.isNull(FORMAT_MIMETYPE))
            common = equals(jsourceFormat.getString(FORMAT_MIMETYPE), jtargetFormat.getString(FORMAT_MIMETYPE));
        if(!jsourceFormat.isNull(FORMAT_SCHEMA) && !jtargetFormat.isNull(FORMAT_SCHEMA))
            common = equals(jsourceFormat.getString(FORMAT_SCHEMA), jtargetFormat.getString(FORMAT_SCHEMA));
        if(!jsourceFormat.isNull(FORMAT_TYPE) && !jtargetFormat.isNull(FORMAT_TYPE))
            common = equals(jsourceFormat.getString(FORMAT_TYPE), jtargetFormat.getString(FORMAT_TYPE));
        return common;
    }

    private boolean isWildcard(JSONObject jFormat) {
        return !jFormat.isNull(FORMAT_MIMETYPE) && jFormat.getString(FORMAT_MIMETYPE).equals("*") &&
                !jFormat.isNull(FORMAT_SCHEMA) && jFormat.getString(FORMAT_SCHEMA).equals("*") &&
                !jFormat.isNull(FORMAT_TYPE) && jFormat.getString(FORMAT_TYPE).equals("*");
    }

    private boolean equals(@NotNull String io1, @NotNull String io2){
        return io1.equalsIgnoreCase(io2);
    }

    private IWorkflow initWorkflow(Map<String,IWorkflowNode> nodes) {
        //add connections
        for(Object connection : this.getConnections()) {
            //get workflow nodes
            IWorkflowNode sourceNode = nodes.get(((JSONObject) connection).getString(CONNECTION_S_ID));
            if(sourceNode == null)
                throw new IllegalArgumentException("Source node not found");
            IWorkflowNode targetNode = nodes.get(((JSONObject) connection).getString(CONNECTION_T_ID));
            if(targetNode == null)
                throw new IllegalArgumentException("Target node not found");
            //get workflow connectors
            IOutputConnector out = sourceNode.getOutputConnector(((JSONObject) connection).getString(CONNECTION_S_OUT));
            if(out == null)
                throw new IllegalArgumentException("Output connector not found");
            IInputConnector in = targetNode.getInputConnector(((JSONObject) connection).getString(CONNECTION_T_IN));
            if(in == null)
                throw new IllegalArgumentException("Input connector not found");
            //establish connection
            in.addOutputConnector(out);
        }
        return new Workflow(nodes.values());
    }

    private @Nullable IWorkflowNode getLiteralNode() {
        for (Object process : this.getProcesses()) {
            if (((JSONObject) process).getString(JSON_IDENTIFIER).equals(PROCESS_LITERAL_INPUTS)){
                Map<String, LiteralData> literalInputMap = new HashMap<>();
                for(Object output : ((JSONObject) process).getJSONArray(PROCESS_OUTPUTS)){
                    LiteralData data = getLiteralData((JSONObject) output);
                    literalInputMap.put(data.getIdentifier().toString(), data);
                }
                return new LiteralInputData(new Identifier(PROCESS_LITERAL_INPUTS), literalInputMap, "Literal Inputs");
            }
        }
        return null;
    }

    private LiteralData getLiteralData(JSONObject output) {
        return new StringLiteral(new Identifier(output.getString(JSON_IDENTIFIER)), output.getString(JSON_TITLE), null, output.getString(JSON_TITLE));
    }

    private @NotNull IWorkflowNode getOutputNode() {
        return new OutputData<>(new Identifier(OUTPUT_NODE), null, OUTPUT_NODE, OUTPUT_DESCRIPTION);
    }
}
