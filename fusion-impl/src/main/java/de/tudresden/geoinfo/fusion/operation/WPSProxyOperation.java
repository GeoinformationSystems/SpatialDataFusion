package de.tudresden.geoinfo.fusion.operation;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.LiteralData;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.ows.WPSIODescription;
import de.tudresden.geoinfo.fusion.data.ows.WPSProcessDescription;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Proxy WPS operation instance
 */
public class WPSProxyOperation extends AbstractOperation {

    private WPSProcessDescription processDescription;

    /**
     * constructor
     *
     * @param processDescription WPS process description
     */
    public WPSProxyOperation(WPSProcessDescription processDescription) {
        super(null, processDescription.getIdentifier(), null, false);
        this.processDescription = processDescription;
        initializeConnectors();
    }

    /**
     * get underlying WPS process description
     *
     * @return WPS process description
     */
    public WPSProcessDescription getProcessDescription() {
        return this.processDescription;
    }

    @Override
    public void execute() {
        String request = getXMLRequest();
        try {
            String response = executeRequest(request);

        } catch (IOException e) {
            this.setState(ElementState.ERROR);
        }
    }

    private String executeRequest(String request) throws IOException {
        //init connection
        URL url = this.getProcessDescription().getURI().getBaseURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Length", String.valueOf(request.getBytes().length));
        //send request
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(request);
        out.flush();
        out.close();
        //get response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        //evaluate
        return response.toString();
    }

    @Override
    public void initializeInputConnectors() {
        for (WPSIODescription input : this.processDescription.getInputs().values()) {
            this.addInputConnector(input.getIdentifier(), input.getTitle(), input.getAbstract(), null, input.getDescriptionConstraints(), null);
        }
    }

    @Override
    public void initializeOutputConnectors() {
        for (WPSIODescription output : this.processDescription.getOutputs().values()) {
            this.addOutputConnector(output.getIdentifier(), output.getTitle(), output.getAbstract(), null, output.getDescriptionConstraints());
        }
    }

    @NotNull
    private String getXMLRequest() {
        StringBuilder requestBuilder = new StringBuilder();
        //append header information
        requestBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<wps:Execute service=\"WPS\" version=\"1.0.0\" " +
                "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" " +
                "xmlns:ows=\"http://www.opengis.net/ows/1.1\" " +
                "xmlns:ogc=\"http://www.opengis.net/ogc\" " +
                "xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsExecute_request.xsd\">\n");
        //append requested process
        requestBuilder.append("<ows:Identifier>" + this.getTitle() + "</ows:Identifier>\n");
        //append inputs
        requestBuilder.append("<wps:DataInputs>\n");
        for (IInputConnector inputConnector : this.getInputConnectors()) {
            requestBuilder.append(getRequestInput(inputConnector));
        }
        requestBuilder.append("</wps:DataInputs>\n");
        //append outputs
        requestBuilder.append("<wps:ResponseForm>\n" +
                "<wps:ResponseDocument storeExecuteResponse=\"false\" lineage=\"false\" status=\"false\">\n");
        for (IOutputConnector outputConnector : this.getOutputConnectors()) {
            requestBuilder.append(getRequestOutput(outputConnector));
        }
        requestBuilder.append("</wps:ResponseDocument>\n" +
                "</wps:ResponseForm>\n");
        //finalize
        requestBuilder.append("</wps:Execute>");
        return requestBuilder.toString();
    }

    private String getRequestInput(IInputConnector inputConnector) {
        if (inputConnector.getData() == null)
            return "";
        return "<wps:Input>\n" +
                "<ows:Identifier>" + inputConnector.getTitle() + "</ows:Identifier>\n" +
                getRequestInputData(inputConnector.getData()) +
                "</wps:Input>\n";
    }

    private String getRequestInputData(IData data) {
        if (data instanceof URILiteral)
            return "<wps:Reference " +
                    "schema=\"http://schemas.opengis.net/gml/3.1.1/base/feature.xsd\" " +
                    "xlink:href=\"" + ((URILiteral) data).resolve().toString() + "\" " +
                    "method=\"GET\"/>";
        if (data instanceof LiteralData) {
            return "<wps:Data><wps:LiteralData>" + ((LiteralData) data).getLiteral() + "</wps:LiteralData></wps:Data>";
        } else
            throw new RuntimeException("TODO");
    }

    private String getRequestOutput(IOutputConnector outputConnector) {
        if (outputConnector.getTitle().equals("OUT_RUNTIME") || outputConnector.getTitle().equals("OUT_START"))
            return "";
        return "<wps:Output asReference=\"true\">\n" +
                "<ows:Identifier>" + outputConnector.getTitle() + "</ows:Identifier>\n" +
                "</wps:Output>\n";
    }

}
