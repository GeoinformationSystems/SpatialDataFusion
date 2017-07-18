package de.tudresden.geoinfo.fusion.operation.provision;

import com.hp.hpl.jena.sparql.modify.request.Target;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.IntegerLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.*;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.PatternConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RDFTutleProvider extends AbstractOperation {

    private static final String PROCESS_TITLE = RDFTutleProvider.class.getName();
    private static final String PROCESS_DESCRIPTION = "Generator for W3C RDF format";

    private final static String IN_RDF_TITLE = "IN_TRIPLES";
    private final static String IN_RDF_DESCRIPTION = "Input triples";
    private final static String IN_URI_BASE_TITLE = "IN_URI_BASE";
    private final static String IN_URI_BASE_DESCRIPTION = "RDF Base URI";
    private final static String IN_URI_PREFIXES_TITLE = "IN_URI_PREFIXES";
    private final static String IN_URI_PREFIXES_DESCRIPTION = "RDF Prefixes (schema,prefix;schema,prefix...)";
    private final static String IN_TRIPLE_STORE_TITLE = "IN_TRIPLE_STORE";
    private final static String IN_TRIPLE_STORE_DESCRIPTION = "Triple Store URI";
    private final static String IN_CLEAR_STORE_TITLE = "IN_CLEAR_STORE";
    private final static String IN_CLEAR_STORE_DESCRIPTION = "Flag: Clear triple store before insert";
    private final static String IN_TRIPLE_BAG_SIZE_TITLE = "IN_TRIPLE_BAG_SIZE";
    private final static String IN_TRIPLE_BAG_SIZE_DESCRIPTION = "Number of subjects written to SPARQL endpoint in one INSERT request";

    private final static String OUT_RESOURCE_TITLE = "OUT_RESOURCE";
    private final static String OUT_RESOURCE_DESCRIPTION = "Link to RDF encoded file or SPARQL Endpoint";

    public RDFTutleProvider() {
        super(PROCESS_TITLE, PROCESS_DESCRIPTION);
    }

    @Override
    public void executeOperation() {

        //get data
        IRDFResource rdfData = (IRDFResource) (this.getMandatoryInputData(IN_RDF_TITLE)).resolve();
        StringLiteral base = (StringLiteral) this.getInputData(IN_URI_BASE_TITLE);
        Map<String, String> prefixes = parsePrefixes((StringLiteral) this.getInputData(IN_URI_PREFIXES_TITLE));
        URLLiteral tripleStore = (URLLiteral) this.getInputData(IN_TRIPLE_STORE_TITLE);

        //init result
        URLLiteral rdfResource;
        //check for triple store URI
        if (tripleStore != null) {
            try {
                URL tripleStoreURL = tripleStore.resolve();
                boolean clearStore = ((BooleanLiteral) this.getMandatoryInputData(IN_CLEAR_STORE_TITLE)).resolve();
                int bagSize = ((IntegerLiteral) this.getMandatoryInputData(IN_TRIPLE_BAG_SIZE_TITLE)).resolve();
                //add base prefix to prefixes
                if (base != null)
                    prefixes.put(base.resolve(), "base");
                writeRDFToStore(tripleStoreURL, rdfData, prefixes, clearStore, bagSize);
                rdfResource = new URLLiteral(tripleStoreURL);
            } catch (IOException e) {
                throw new RuntimeException("Could not access or write to triple store", e);
            }
        }

        //else: provide RDF file
        else {
            try {
                rdfResource = generateRDFFile(rdfData, base != null ? base.resolve() : null, prefixes);
            } catch (IOException e) {
                throw new RuntimeException("Could not write RDF file", e);
            }
        }

        //set output connector
        setOutput(OUT_RESOURCE_TITLE, rdfResource);
    }

    /**
     * get RDF prefixes
     *
     * @param data input prefix string
     * @return prefix map
     */
    private Map<String, String> parsePrefixes(@Nullable StringLiteral data) {
        Map<String, String> prefixes = new HashMap<>();
        if (data != null && !data.resolve().isEmpty()) {
            String[] prefixesArray = data.resolve().split(";");
            for (String prefix : prefixesArray) {
                prefixes.put(prefix.split(",")[0], prefix.split(",")[1]);
            }
        }
        return prefixes;
    }

    /**
     * write triples to triple store
     *
     * @param tripleStore triple store URI
     * @param rdfData     input subjects
     * @param prefixes    prefix URLs
     * @param clearStore  flag: clear triple store before insert
     * @throws IOException if triple store update fails
     */
    private void writeRDFToStore(@NotNull URL tripleStore, @NotNull IRDFResource rdfData, @NotNull Map<String, String> prefixes, boolean clearStore, int bagSize) throws IOException {
        if (clearStore)
            clearStore(tripleStore);
        updateStore(tripleStore, rdfData, prefixes, bagSize);
    }

    private void clearStore(@NotNull URL tripleStore) {
        UpdateRequest request = new UpdateRequest().add(new UpdateClear(Target.ALL));
        updateStore(tripleStore, request);
    }

    private void updateStore(@NotNull URL tripleStore, @NotNull UpdateRequest request) {
        UpdateExecutionFactory.createRemote(request, tripleStore.toString()).execute();
    }

    private void updateStore(@NotNull URL tripleStore, @NotNull IRDFResource rdfData, @NotNull Map<String, String> prefixes, int bagSize) throws MalformedURLException {
        StringBuilder sRequest = new StringBuilder();
        //insert data
        if (rdfData instanceof IRDFGraph) {
            List<String> rdfInserts = encodeGraph((IRDFGraph) rdfData, null, prefixes, bagSize);
            for (String insert : rdfInserts) {
                sRequest.append(getPrefixHeader(prefixes));
                sRequest.append("INSERT DATA {\n");
                sRequest.append(insert);
                sRequest.append("}");
                updateStore(tripleStore, new UpdateRequest().add(sRequest.toString()));
                sRequest.setLength(0);
            }
        } else {
            sRequest.append(getPrefixHeader(prefixes));
            sRequest.append("INSERT DATA {\n");
            sRequest.append(encodeResource(rdfData, null, prefixes));
            sRequest.append("}");
            updateStore(tripleStore, new UpdateRequest().add(sRequest.toString()));
        }
    }

    private String getPrefixHeader(@NotNull Map<String, String> prefixes) {
        StringBuilder sHeader = new StringBuilder();
        //append prefixes
        for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
            sHeader.append("PREFIX ").append(prefix.getValue()).append(": <").append(prefix.getKey()).append(">\n");
        }
        return sHeader.toString();
    }

    /**
     * generate RDF file
     *
     * @param rdfData  input subjects
     * @param base     base URL
     * @param prefixes prefix URLs
     * @return URL literal instance pointing to file
     * @throws IOException if RDF file cannot be created
     */
    private URLLiteral generateRDFFile(@NotNull IRDFResource rdfData, @Nullable String base, @NotNull Map<String, String> prefixes) throws IOException {
        //init file
        File file = File.createTempFile("relations_" + UUID.randomUUID(), ".rdf");
        //write RDF turtles
        writeTriplesToFile(rdfData, base, prefixes, file);
        //return
        return new URLLiteral(file.toURI().toURL());
    }

    /**
     * write RDF data to file
     *
     * @param rdfData  input subjects
     * @param base     base URL
     * @param prefixes prefix URLs
     * @param file     output file
     * @throws IOException if triples cannot be written to file
     */
    private void writeTriplesToFile(@NotNull IRDFResource rdfData, @Nullable String base, @NotNull Map<String, String> prefixes, @NotNull File file) throws IOException {
        //create file writer
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            //write base and prefixes
            if (base != null)
                writer.write("@base <" + base + "> .\n");
            if (prefixes.size() > 0) {
                for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
                    writer.write("@prefix " + prefix.getValue() + ": <" + prefix.getKey() + "> .\n");
                }
            }
            //write graph data
            if (rdfData instanceof IRDFGraph) {
                List<String> rdfInserts = encodeGraph((IRDFGraph) rdfData, base, prefixes, 1000);
                for (String insert : rdfInserts) {
                    writer.append(insert);
                }
            } else
                writer.append(encodeResource(rdfData, base, prefixes));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * encode RDF representation collection
     *
     * @param collection RDF representation collection
     * @param base       RDF base
     * @param prefixes   RDF prefixes
     * @param chunkSize  number of RDF representations in one String
     * @return list of encoded RDF representation Strings
     */
    private List<String> encodeGraph(@NotNull IRDFGraph collection, @Nullable String base, @NotNull Map<String, String> prefixes, int chunkSize) throws MalformedURLException {
        List<String> sList = new ArrayList<>();
        StringBuilder sTriple = new StringBuilder();
        StringBuilder sTripleRoot = new StringBuilder();
        //iterate collection
        int i = 0;
        for (IRDFSubject rdfSubject : collection.getRDFSubjects()) {
            i++;
            sTriple.append(encodeSubject(rdfSubject, base, prefixes, "", true, true, sTripleRoot));
            //cut relation string by chunk size
            if (i % chunkSize == 0) {
                sList.add(sTriple.append(sTripleRoot).toString());
                sTriple.setLength(0);
                sTripleRoot.setLength(0);
            }
        }
        //add remaining relations
        if (sTriple.length() > 0)
            sList.add(sTriple.append(sTripleRoot).toString());
        //return list
        return sList;
    }

    /**
     * encode RDF triple representation
     *
     * @param rdf      RDF triple representation
     * @param base     RDF base
     * @param prefixes RDF prefixes
     * @return encoded RDF triple representation String
     */
    private String encodeSubject(@NotNull IRDFSubject rdf, @Nullable String base, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        StringBuilder sTripleRoot = new StringBuilder();
        String sTriple = encodeSubject(rdf, base, prefixes, "", true, true, sTripleRoot);
        return sTripleRoot.append(sTriple).toString();
    }

    /**
     * encode RDF triple set
     *
     * @param subject      RDF subject
     * @param base         RDF base
     * @param prefixes     RDF prefixes
     * @param indent       styling indent
     * @param writeSubject set true to include encoded subject node
     * @param close        set true to close the set (. instead of ;)
     * @return encoded RDF triple set String
     */
    private String encodeSubject(@NotNull IRDFSubject subject, @Nullable String base, @NotNull Map<String, String> prefixes, @NotNull String indent, boolean writeSubject, boolean close, @NotNull StringBuilder sTripleRoot) throws MalformedURLException {
        StringBuilder sTriple = new StringBuilder();
        //simplified encoding (subject predicate object ;) for subjects with degree = 1
        if(subject.getDegree() == 1){
            IRDFProperty predicate = subject.getPredicates().iterator().next();
            sTriple.append(indent).append(encodeResource(subject, base, prefixes)).append(" ");
            sTriple.append(encodeResource(predicate, base, prefixes));
            sTriple.append(encodeObject(subject.getObjects(predicate).iterator().next(), base, prefixes, indent, close, sTripleRoot));
        }
        else {

            if (writeSubject)
                sTriple.append(indent).append(encodeResource(subject, base, prefixes)).append("\n");
            //increase indent
            indent += "\t";

            Set<IRDFProperty> predicates = subject.getPredicates();
            int i = predicates.size();
            for (IRDFProperty predicate : (subject).getPredicates()) {
                Collection<IRDFNode> objects = subject.getObjects(predicate);
                //iterate objects for predicate
                for (IRDFNode object : objects) {
                    //write predicate
                    sTriple.append(indent).append(encodeResource(predicate, base, prefixes));
                    //add object (close if last object in object set)
                    i--;
                    boolean lastObject = (i == 0);
                    boolean setClose = (close && lastObject);
                    sTriple.append(encodeObject(object, base, prefixes, indent, setClose, sTripleRoot));
                }
            }
        }
        //return
        return sTriple.toString();
    }

    private String encodeObject(@NotNull IRDFNode object, @Nullable String base, @NotNull Map<String, String> prefixes, @NotNull String indent, boolean close, @NotNull StringBuilder sTripleRoot) throws MalformedURLException {
        StringBuilder sTriple = new StringBuilder();
        //encode subject
        if (object instanceof IRDFSubject) {
            if (((IRDFSubject) object).getPredicates().size() > 0) {
                if (((IRDFSubject) object).isBlank()) {
                    sTriple.append(" [\n");
                    sTriple.append(encodeSubject((IRDFSubject) object, base, prefixes, indent, false, false, sTripleRoot));
                    sTriple.append(indent).append("]").append(close ? " ." : " ;").append("\n");
                } else {
                    sTriple.append(" ").append(encodeResource((IRDFSubject) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
                    sTripleRoot.append(encodeSubject((IRDFSubject) object, base, prefixes, "", true, true, sTripleRoot));
                }
            } else
                sTriple.append(" ").append(encodeResource((IRDFSubject) object, base, prefixes)).append(" ;\n");
        }
        //encode literal
        else if (object instanceof IRDFLiteral)
            sTriple.append(" ").append(encodeLiteral((IRDFLiteral) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
            //encode identifiable resource
        else if (object instanceof IRDFResource && !((IRDFResource) object).isBlank()) {
            sTriple.append(" ").append(encodeResource((IRDFResource) object, base, prefixes)).append(close ? " ." : " ;").append("\n");
        } else
            //this should not happen
            sTriple.append(" NoIdentifiableRDFNode").append(close ? " ." : " ;").append("\n");

        return sTriple.toString();
    }

    /**
     * encode RDF resource
     *
     * @param resource RDF resource
     * @param base     RDF base
     * @param prefixes RDF prefixes
     * @return encoded resource String
     */
    private String encodeResource(@NotNull IRDFResource resource, @Nullable String base, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        if (resource.getIRI() == null)
            return "_:" + resource.getClass().getSimpleName() + "_" + UUID.randomUUID();
        else {
            String relative = relativizeIdentifier(resource.getIRI(), base, prefixes);
            if (resource.getIRI().equals(relative))
                return "<" + resource.getIRI() + ">";
            else
                return relative;
        }
    }

    /**
     * encode RDF typed literal
     *
     * @param literal  RDF typed literal
     * @param base     RDF base
     * @param prefixes RDF prefixes
     * @return encoded literal String
     */
    private String encodeTypedLiteral(@NotNull IRDFLiteral literal, @Nullable String base, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        IRDFProperty resource = literal.getLiteralType();
        String relative = relativizeIdentifier(resource.getIRI(), base, prefixes);
        return "\"" + literal.getLiteralValue() + "\"^^" + (resource.getIRI().equals(relative) ? "<" + resource.getIRI() + ">" : relative);
    }

    /**
     * encode RDF plain literal
     *
     * @param literal RDF plain literal
     * @return encoded literal String
     */
    private String encodePlainLiteral(@NotNull IRDFLiteral literal) {
        return "\"" + literal.getLiteralValue() + "\"@" + literal.getLanguage();
    }

    /**
     * encode RDF literal
     *
     * @param literal  RDF literal
     * @param base     RDF base
     * @param prefixes RDF prefixes
     * @return encoded literal String
     */
    private String encodeLiteral(@NotNull IRDFLiteral literal, @Nullable String base, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        if(literal.getLanguage() != null)
            return encodePlainLiteral(literal);
        else
            return encodeTypedLiteral(literal, base, prefixes);
    }

    /**
     * encode RDF literal
     *
     * @param literal  RDF literal
     * @param prefixes RDF prefixes
     * @return encoded literal String
     */
    private String encodeLiteral(@NotNull IRDFLiteral literal, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        return encodeLiteral(literal, null, prefixes);
    }

    /**
     * relativize a resource identifier
     *
     * @param iri identifier
     * @param base     URI base
     * @param prefixes URI prefixes
     * @return relativized resource identifier
     */
    private String relativizeIdentifier(@NotNull String iri, @Nullable String base, @NotNull Map<String, String> prefixes) throws MalformedURLException {
        String relative = iri;
        if(base != null)
            relative = relativizeIdentifier(iri, base);
        if (iri.equals(relative)) {
            for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
                relative = relativizeIdentifier(iri, prefix.getKey(), prefix.getValue());
                if (!iri.equals(relative))
                    return relative;
            }
        }
        return iri;
    }

    /**
     * relativize identifier
     *
     * @param iri identifier
     * @param root root string to be removed from identifier
     * @return relativized resource identifier
     */
    private String relativizeIdentifier(@NotNull String iri, @NotNull String root) {
        return relativizeIdentifier(iri, root, "");
    }

    /**
     * relativize identifier
     * @param iri identifier
     * @param root root string to be removed from identifier
     * @return relativized identifier
     */
    private String relativizeIdentifier(@NotNull String iri, @NotNull String root, @NotNull String prefix) {
        //special case: http://www.w3.org/1999/02/22-rdf-syntax-ns#type --> a
        if (iri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
            return "a";
        if (!iri.startsWith(root))
            return iri;
        String relative = iri.replaceFirst(root, "");
        if(relative.startsWith("#") || relative.startsWith("/"))
            relative = relative.substring(1);
        if(relative.contains("#") || relative.contains("/"))
            return iri;
        return prefix.isEmpty() ? relative : prefix + ":" + relative;
    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(IN_RDF_TITLE, IN_RDF_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(IRDFResource.class),
                        new BindingConstraint(IData.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(IN_URI_BASE_TITLE, IN_URI_BASE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class)},
                null,
                null);
        addInputConnector(IN_URI_PREFIXES_TITLE, IN_URI_PREFIXES_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(StringLiteral.class),
                        new PatternConstraint("^(" + URLLiteral.getURLRegex() + ",([a-z]+);)+$")},
                null,
                null);
        addInputConnector(IN_TRIPLE_STORE_TITLE, IN_TRIPLE_STORE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class)},
                null,
                null);
        addInputConnector(IN_CLEAR_STORE_TITLE, IN_CLEAR_STORE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
        addInputConnector(IN_TRIPLE_BAG_SIZE_TITLE, IN_TRIPLE_BAG_SIZE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(IntegerLiteral.class)},
                null,
                new IntegerLiteral(1000));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(OUT_RESOURCE_TITLE, OUT_RESOURCE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class)},
                null);
    }

}
