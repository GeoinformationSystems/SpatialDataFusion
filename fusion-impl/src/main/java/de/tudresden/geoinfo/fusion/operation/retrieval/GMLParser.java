package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.Identifier;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTIndexedFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.metadata.DC_Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.Metadata;
import de.tudresden.geoinfo.fusion.data.metadata.MetadataElement;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.apache.commons.io.FileUtils;
import org.geotools.xml.Configuration;
import org.geotools.xml.PullParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class GMLParser extends GTFeatureParser {

    private static final String PROCESS_TITLE = GMLParser.class.getName();
    private static final String PROCESS_DESCRIPTION = "Parser for OGC GML format";

    private final static String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    /**
     * constructor
     */
    public GMLParser(@Nullable IIdentifier identifier) {
        super(identifier);
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
    public GTFeatureCollection getFeatures(URL resourceURL, boolean withIndex) throws IOException {
        File gmlFile;
        //copy gml from HTTP connection to allow for multiple reads with different GML configurations
        if (resourceURL.getProtocol().toLowerCase().startsWith("http"))
            gmlFile = copy(resourceURL);
        else
            gmlFile = new File(resourceURL.getFile());
        try {
            return parseGMLFromFile(resourceURL, gmlFile, withIndex);
        } catch (XMLStreamException | SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * copy gml from http connection
     *
     * @param resourceURL http resource URL
     * @return file resource URL
     * @throws IOException
     */
    private File copy(URL resourceURL) throws IOException {
        File destination = new File(TEMP_FOLDER + "/" + UUID.randomUUID().toString() + ".gml");
        FileUtils.copyURLToFile(resourceURL, destination);
        return destination;
    }

//    /**
//     * parse GML from HTTP URL
//     *
//     * @param resourceURL input URL
//     * @param withIndex   flag: return indexed collection
//     * @return feature collection
//     * @throws IOException
//     * @throws XMLStreamException
//     * @throws SAXException
//     */
//    private AbstractFeatureCollection<?> parseGMLFromHTTP(URL resourceURL, boolean withIndex) throws IOException, XMLStreamException, SAXException {
//        //get connection
//        HttpURLConnection urlConnection = (HttpURLConnection) resourceURL.openConnection();
//        urlConnection.connect();
//        return parseGML(resourceURL, getContentTypeFromURL(urlConnection, resourceURL.toString().toLowerCase()), urlConnection.getInputStream(), withIndex);
//    }

//    /**
//     * get content type from HTTP URL
//     *
//     * @param urlConnection input connection
//     * @param sUrl          URL as string
//     * @return
//     */
//    private String getContentTypeFromURL(HttpURLConnection urlConnection, String sUrl) {
//        //check URL content type
//        if (urlConnection.getContentType() != null && urlConnection.getContentType().toLowerCase().contains("gml"))
//            return urlConnection.getContentType();
//        //analyze URL string
//        String sUrlLower = sUrl.toLowerCase();
//        if (sUrlLower.contains("outputformat=gml")) {
//            if (sUrlLower.contains("gml3.2"))
//                return "3.2";
//        } else if (sUrlLower.contains("wfs")) {
//            if (sUrlLower.contains("version=1.0.0"))
//                return "2.0";
//            else if (sUrlLower.contains("version=1.1.0"))
//                return "3.1.1";
//            else if (sUrlLower.contains("version=2.0.0"))
//                return "3.2";
//        }
//        return null;
//    }

    /**
     * parse GML from File URL
     *
     * @param resourceURL input file URL
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private GTFeatureCollection parseGMLFromFile(URL resourceURL, File file, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        if (!file.exists() || file.isDirectory())
            return null;
        //redirect based on content type
        return parseGML(resourceURL, file, getContentType(file), withIndex);
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
     * @param file        GML input file
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private GTFeatureCollection parseGML(URL resourceURL, File file, String contentType, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        //test different encodings if content type is null
        if (contentType == null)
            return parse(resourceURL, file, withIndex);
        //redirect based on content type
        if (contentType.contains("3.2"))
            return parseGML32(resourceURL, file, withIndex);
        else if (contentType.contains("3."))
            return parseGML3(resourceURL, file, withIndex);
        else if (contentType.contains("2."))
            return parseGML2(resourceURL, file, withIndex);
        else if (contentType.contains("text/xml"))
            return parse(resourceURL, file, withIndex);
        else
            throw new IOException("Could not determine GML version");
    }

    /**
     * parse GML from URL
     *
     * @param resourceURL input URL
     * @param file        GML input file
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     */
    private GTFeatureCollection parse(URL resourceURL, File file, boolean withIndex) {
        GTFeatureCollection gmlFC = null;
        try {
            gmlFC = parseGML32(resourceURL, file, withIndex);
        } catch (IOException | XMLStreamException | SAXException e) {
            //do nothing
        }
        if (gmlFC == null || gmlFC.size() == 0)
            try {
                gmlFC = parseGML3(resourceURL, file, withIndex);
            } catch (IOException | XMLStreamException | SAXException e) {
                //do nothing
            }
        if (gmlFC == null || gmlFC.size() == 0)
            try {
                gmlFC = parseGML2(resourceURL, file, withIndex);
            } catch (IOException | XMLStreamException | SAXException e) {
                //do nothing
            }
        return gmlFC;
    }

    /**
     * parse GML version 3.2
     *
     * @param resourceURL input URL
     * @param file        GML input file
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private GTFeatureCollection parseGML32(URL resourceURL, File file, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL.toString(), new FileInputStream(file), new org.geotools.gml3.v3_2.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML version 3
     *
     * @param resourceURL input URL
     * @param file        GML input file
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private GTFeatureCollection parseGML3(URL resourceURL, File file, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL.toString(), new FileInputStream(file), new org.geotools.gml3.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML version 2
     *
     * @param resourceURL input URL
     * @param file        GML input file
     * @param withIndex   flag: return indexed collection
     * @return feature collection
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     */
    private GTFeatureCollection parseGML2(URL resourceURL, File file, boolean withIndex) throws IOException, XMLStreamException, SAXException {
        return parseGML(resourceURL.toString(), new FileInputStream(file), new org.geotools.gml2.GMLConfiguration(), withIndex);
    }

    /**
     * parse GML from XML stream
     *
     * @param collectionId   collection identifier URL
     * @param input          GML input stream
     * @param configuration XML parser configuration
     * @param withIndex     flag: return indexed collection
     * @return feature collection
     */
    public static GTFeatureCollection parseGML(String collectionId, InputStream input, Configuration configuration, boolean withIndex) throws XMLStreamException, IOException, SAXException {
        Collection<GTVectorFeature> features = new HashSet<>();
        PullParser gmlParser = new PullParser(configuration, input, SimpleFeature.class);
        SimpleFeature feature;
        IIdentifier resourceId = new Identifier(collectionId);
        while ((feature = (SimpleFeature) gmlParser.parse()) != null) {
            String featureID = feature.getID();
            Metadata metadata = new Metadata();
            metadata.addElement(new MetadataElement(DC_Metadata.DC_TITLE.getResource(), featureID));
            metadata.addElement(new MetadataElement(DC_Metadata.DC_SOURCE.getResource(), resourceId.toString()));
            features.add(new GTVectorFeature(new Identifier(featureID), feature, metadata));
        }
        return withIndex ? new GTFeatureCollection(resourceId, features, null) : new GTIndexedFeatureCollection(resourceId, features, null);
    }

    @NotNull
    @Override
    public String getTitle() {
        return PROCESS_TITLE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PROCESS_DESCRIPTION;
    }

}
