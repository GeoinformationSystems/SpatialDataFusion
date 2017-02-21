package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.AbstractFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URILiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IInputConnector;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.InputData;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryConstraint;
import org.geotools.xml.Configuration;
import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

public class GMLParser extends AbstractOperation {

    private static final String PROCESS_TITLE = GMLParser.class.getSimpleName();
    private static final String PROCESS_DESCRIPTION = "Parser for OGC GML format";

    private final static String IN_RESOURCE_TITLE = "IN_RESOURCE";
    private final static String IN_RESOURCE_DESCRIPTION = "GML resource";
    private final static String IN_WITH_INDEX_TITLE = "IN_WITH_INDEX";
    private final static String IN_WITH_INDEX_DESCRIPTION = "Flag: create spatial index";

    private final static String OUT_FEATURES_TITLE = "OUT_FEATURES";
    private final static String OUT_FEATURES_DESCRIPTION = "Parsed feature collection";

    /**
     * constructor
     */
    public GMLParser() {
        super(null, PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void execute() {
        //get input connectors
        IInputConnector resourceConnector = getInputConnector(IN_RESOURCE_TITLE);
        IInputConnector indexConnector = getInputConnector(IN_WITH_INDEX_TITLE);
        //get data
        URI resourceURI = ((URILiteral) resourceConnector.getData()).resolve();
        boolean withIndex = ((BooleanLiteral) indexConnector.getData()).resolve();
        //parse features
        AbstractFeatureCollection<?> features;
        try {
            features = parseGML(resourceURI.toURL(), withIndex);
        } catch (IOException | XMLStreamException | SAXException e) {
            throw new RuntimeException("Could not parse GML source", e);
        }
        //set output connector
        connectOutput(OUT_FEATURES_TITLE, features);
    }

    /**
     * parse GML from URL
     *
     * @param resourceURL input URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML(URL resourceURL, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        //parse HTTP connection
        if (resourceURL.getProtocol().toLowerCase().startsWith("http"))
            return parseGMLFromHTTP(resourceURL, withIndex);
            //parse file
        else if (resourceURL.getProtocol().toLowerCase().startsWith("file"))
            return parseGMLFromFile(resourceURL, withIndex);
            //else: throw IOException
        else
            throw new IOException("Unsupported GML source: " + resourceURL.toString());
    }

    /**
     * parse GML from HTTP URL
     *
     * @param resourceURL input URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGMLFromHTTP(URL resourceURL, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        //get connection
        HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
        urlConnection.connect();
        return parseGML(resourceURL, getContentTypeFromURL(urlConnection, resourceURL.toString().toLowerCase()), urlConnection.getInputStream(), withIndex);
    }

    /**
     * get content type from HTTP URL
     *
     * @param urlConnection input connection
     * @param sUrl          URL as string
     * @return
     */
    private String getContentTypeFromURL(HttpURLConnection urlConnection, String sUrl) {
        //check URL content type
        if (urlConnection.getContentType() != null && urlConnection.getContentType().toLowerCase().contains("gml"))
            return urlConnection.getContentType();
        //analyze URL string
        String sUrlLower = sUrl.toLowerCase();
        if (sUrlLower.contains("outputformat=gml")) {
            if (sUrlLower.contains("gml3.2"))
                return "3.2";
        } else if (sUrlLower.contains("wfs")) {
            if (sUrlLower.contains("version=1.0.0"))
                return "2.0";
            else if (sUrlLower.contains("version=1.1.0"))
                return "3.1.1";
            else if (sUrlLower.contains("version=2.0.0"))
                return "3.2";
        }
        return null;
    }

    /**
     * parse GML from File URL
     *
     * @param resourceURL input file URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGMLFromFile(URL resourceURL, boolean withIndex) throws FileNotFoundException, IOException, XMLStreamException, SAXException {
        File file = new File(resourceURL.getFile());
        if (!file.exists() || file.isDirectory())
            return null;
        //redirect based on content type
        return parseGML(resourceURL, getContentType(file), new FileInputStream(file), withIndex);
    }

    /**
     * get content type from file
     *
     * @param file input file
     * @return file content type
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private String getContentType(File file) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(file));
        String contentType = null;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                //get content type from xml namespace
                for (int i = 0; i < reader.getNamespaceCount(); i++) {
                    String nsURI = reader.getNamespaceURI(i);
                    contentType = getGMLContentTypeFromString(nsURI);
                    if (contentType != null)
                        break;
                }
                //get content type from xsi:schemaLocation
                String schemaLocation = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                contentType = getGMLContentTypeFromString(schemaLocation);
                break;
            }
        }
        reader.close();
        return contentType;
    }

    /**
     * get GML content type from string
     *
     * @param input input string
     * @return GML content type
     */
    private String getGMLContentTypeFromString(String input) {
        if (input.matches("(.*gml/3\\.2.*)|(.*wfs/2\\.0.*)")) {
            return "gml/3.2.1";
        } else if (input.matches("(.*gml/3\\.2.*)|(.*wfs/1\\.1\\.0.*)")) {
            return "gml/3.1.1";
        } else if (input.matches("(.*gml/2.*)|(.*wfs/1\\.0\\.0.*)")) {
            return "gml/2.1.2";
        }
        return null;
    }

    /**
     * parse GML from URL
     *
     * @param resourceURL input URL
     * @param contentType GML content type
     * @param stream      GML input stream
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML(URL resourceURL, String contentType, InputStream stream, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        //test different encodings if content type is null
        if (contentType == null)
            return parse(resourceURL, stream, withIndex);
        //redirect based on content type
        if (contentType.contains("3.2"))
            return parseGML32(resourceURL, stream, withIndex);
        else if (contentType.contains("3."))
            return parseGML3(resourceURL, stream, withIndex);
        else if (contentType.contains("2."))
            return parseGML2(resourceURL, stream, withIndex);
        else if (contentType.contains("text/xml"))
            return parse(resourceURL, stream, withIndex);
        else
            throw new IOException("Could not determine GML version");
    }

    /**
     * parse GML from URL
     *
     * @param resourceURL input URL
     * @param stream      GML input stream
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     */
    private AbstractFeatureCollection<?> parse(URL resourceURL, InputStream stream, boolean withIndex) {
        AbstractFeatureCollection<?> gmlFC = null;
        try {
            gmlFC = parseGML32(resourceURL, stream, withIndex);
        } catch (IOException | XMLStreamException | SAXException e) {
            //do nothing
        }
        if (gmlFC == null || gmlFC.size() == 0)
            try {
                gmlFC = parseGML3(resourceURL, stream, withIndex);
            } catch (IOException | XMLStreamException | SAXException e) {
                //do nothing
            }
        if (gmlFC == null || gmlFC.size() == 0)
            try {
                gmlFC = parseGML2(resourceURL, stream, withIndex);
            } catch (IOException | XMLStreamException | SAXException e) {
                //do nothing
            }
        return gmlFC;
    }

    /**
     * parse GML version 3.2
     *
     * @param resourceURL input URL
     * @param stream      GML input stream
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML32(URL resourceURL, InputStream stream, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL, stream, new org.geotools.gml3.v3_2.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML version 3
     *
     * @param resourceURL input URL
     * @param stream      GML input stream
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML3(URL resourceURL, InputStream stream, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL, stream, new org.geotools.gml3.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML version 2
     *
     * @param resourceURL input URL
     * @param stream      GML input stream
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML2(URL resourceURL, InputStream stream, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL, stream, new org.geotools.gml2.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML from XML stream
     *
     * @param resourceURL   input URL
     * @param stream        GML input stream
     * @param configuration XML parser configuration
     * @param withIndex     flag: return indexed collection
     * @return feature collection
     * @throws XMLStreamException
     * @throws IOException
     * @throws SAXException
     */
    private AbstractFeatureCollection<?> parseGML(URL resourceURL, InputStream stream, Configuration configuration, boolean withIndex) throws XMLStreamException, IOException, SAXException {
        IIdentifier identifier = new Identifier(resourceURL.toString());
        Collection<GTVectorFeature> features = new HashSet<>();
        PullParser gmlParser = new PullParser(configuration, stream, SimpleFeature.class);
        SimpleFeature feature = null;
        while ((feature = (SimpleFeature) gmlParser.parse()) != null) {
            String featureID = identifier == null ? feature.getID() : (identifier + "#" + feature.getID());
            features.add(new GTVectorFeature(new Identifier(featureID), feature, null));
        }
        return withIndex ? new GTFeatureCollection(identifier, features, null) : new GTIndexedFeatureCollection(identifier, features, null);
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RESOURCE_TITLE, IN_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URILiteral.class),
                        new MandatoryConstraint()},
                null,
                null);
        addInputConnector(IN_WITH_INDEX_TITLE, IN_WITH_INDEX_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new InputData(new BooleanLiteral(false)).getOutputConnector());
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_FEATURES_TITLE, OUT_FEATURES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTFeatureCollection.class),
                        new MandatoryConstraint()},
                null);
    }

}
