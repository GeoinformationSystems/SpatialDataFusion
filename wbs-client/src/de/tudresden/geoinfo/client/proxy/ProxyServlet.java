package de.tudresden.geoinfo.client.proxy;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * proxy for accessing external OWS from openlayers
 */
public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * constructor
     */
    public ProxyServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //get proxy URL
        String sUrl = request.getQueryString().substring(4);
        URL url = new URL(sUrl);

        //read data from URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer urlResponse = new StringBuffer();

        //write data to response
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        while ((inputLine = in.readLine()) != null) {
            urlResponse.append(inputLine);
            out.write(inputLine.getBytes());
        }
        in.close();
        out.flush();

    }

}
