package de.tudresden.geoinfo.fusion.operation.analysis;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import de.tud.fusion.RConnectionHandler;
import de.tud.fusion.Utilities;
import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.BooleanLiteral;
import de.tudresden.geoinfo.fusion.data.literal.DecimalLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.IRuntimeConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.BindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.GeometryBindingConstraint;
import de.tudresden.geoinfo.fusion.operation.constraint.MandatoryDataConstraint;
import de.tudresden.geoinfo.fusion.operation.enhancement.ResampleGeometry;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.Feature;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class SinuosityMatrix extends AbstractOperation {

    private static final String PROCESS_TITLE = SinuosityMatrix.class.getName();
    private static final String PROCESS_DESCRIPTION = "Builds a sinuosity matrix for a provided linear input feature";

    private final static String IN_FEATURE_TITLE = "IN_FEATURE";
    private final static String IN_FEATURE_DESCRIPTION = "Input feature";
    private final static String IN_INTERVAL_TITLE = "IN_INTERVAL";
    private final static String IN_INTERVAL_DESCRIPTION = "Resampling interval";
    private final static String IN_PLOT_TITLE = "IN_PLOT";
    private final static String IN_PLOT_DESCRIPTION = "Flag: produce graphical output (uses RServe)";

    private final static String OUT_MATRIX_TITLE = "OUT_MATRIX";
    private final static String OUT_MATRIX_DESCRIPTION = "Sinuosity Matrix";
    private final static String OUT_PLOT_TITLE = "OUT_PLOT";
    private final static String OUT_PLOT_DESCRIPTION = "Sinuosity Matrix plot (created in R)";

    /**
     * constructor
     */
    public SinuosityMatrix(@Nullable IIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void executeOperation() {
        //get input
        GTVectorFeature feature = (GTVectorFeature) this.getInputData(IN_FEATURE_TITLE);
        IData interval = getInputConnector(IN_INTERVAL_TITLE).getData();
        double dInterval = interval != null ? ((DecimalLiteral) interval).resolve() : setInterval(feature.resolve());
        boolean bPlot = ((BooleanLiteral) this.getInputData(IN_PLOT_TITLE)).resolve();
        try {
            //create matrix
            URLLiteral pathToMatrix = createMatrix(feature, dInterval);
            connectOutput(OUT_MATRIX_TITLE, pathToMatrix);
            if(bPlot){
                //create plot
                URLLiteral pathToPlot = createPlot(pathToMatrix, dInterval);
                connectOutput(OUT_PLOT_TITLE, pathToPlot);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //set output connector

    }

    /**
     * computes intersections within a line network
     *
     * @param inFeature input line feature
     * @return path to sinuosity matrix
     */
    private URLLiteral createMatrix(@NotNull GTVectorFeature inFeature, double interval) throws IOException {
        File tmpFile = Utilities.createTempFile(null, "csv");
        LineString line = this.getGeometry(inFeature.resolve());
        createMatrix(tmpFile, line, interval);
        return new URLLiteral(tmpFile.toURI().toURL());
    }

    /**
     * set default interval (line length / 1024)
     * @param feature input feature
     * @return default interval
     */
    private double setInterval(Feature feature) {
        return this.getGeometry(feature).getLength() / 1024;
    }

    private void createMatrix(@NotNull File tmpFile, @NotNull LineString line, double interval) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(tmpFile);
        DecimalFormat formatter = new DecimalFormat("#.####");

        if(!isResampled(line, interval))
            line = resampleLine(line, interval);

        int length = line.getNumPoints() - 1;

        for(int i=2; i<length; i+=2){
            StringBuilder sb = new StringBuilder();
            double[] sinuosities = calculateSinuosities(line, i, interval);
            for(double sinuosity : sinuosities){
                if(sinuosity != 0)
                    sb.append(formatter.format(sinuosity));
                sb.append(';');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
            pw.write(sb.toString());
        }

        pw.close();
    }

    /**
     * check if line is sampled equally
     * @param line input line
     * @param interval sampling interval
     * @return true, if line is sampled by interval
     */
    private boolean isResampled(@NotNull LineString line, double interval) {
        double tolerance = interval / 10;
        Coordinate[] coords = line.getCoordinates();
        for(int i = 0; i < coords.length - 1; i++){
            if(coords[i].distance(coords[i+1]) > interval + tolerance)
                return false;
        }
        return true;
    }

    /**
     * resample line
     * @param line input line
     * @param interval resampling interval
     * @return resampled line
     */
    private @NotNull LineString resampleLine(@NotNull LineString line, double interval) {
        return (LineString) ResampleGeometry.resampleGeometry(line, interval);
    }

    /**
     * get linestring geometry from feature
     *
     * @param feature input feature
     * @return linestring geometry
     */
    private @NotNull LineString getGeometry(Feature feature) {
        Geometry geometry = Utilities.getGeometryFromFeature(feature, new BindingConstraint(LineString.class, MultiLineString.class), true);
        if(geometry instanceof LineString)
            return (LineString) geometry;
        else if(geometry instanceof MultiLineString && geometry.getNumGeometries() == 1)
            return (LineString) geometry.getGeometryN(0);
        else
            throw new RuntimeException("Feature geometry is null or not supported");
    }

    /**
     * calculate sinuosity from geometry
     * @param geometry input geometry
     * @param length total length (number of sampled elements)
     * @param elementLength element length
     * @return sinuosity list for geometry
     */
    private double[] calculateSinuosities(@NotNull Geometry geometry, int length, double elementLength){
        if(length % 2 != 0)
            throw new IllegalArgumentException();
        //get coordinates
        Coordinate[] coords = geometry.getCoordinates();
        int halfLength = length / 2;
        double[] sinuosities = new double[coords.length];
        for(int i=halfLength; i<coords.length - halfLength; i++){
            //calculate sinuosity
            double basis = coords[i - halfLength].distance(coords[i + halfLength]);
            sinuosities[i] = (length * elementLength) / basis;
        }
        return sinuosities;
    }

    private URLLiteral createPlot(URLLiteral urlPathToMatrix, double interval) throws IOException {

        String pathToMatrix = urlPathToMatrix.resolve().getPath().substring(1);
        String pathToPlot = FilenameUtils.removeExtension(pathToMatrix) + ".png".replace("\\", "/");
        String pathToRData = new File("src/main/resources/SinuosityPlot.RData").getAbsolutePath().replace("\\", "/");
        String function = "function.createPyramid";

        //use Rserve to generate plot
        RConnectionHandler rHandler = null;
        try {
            rHandler = new RConnectionHandler();
            rHandler.loadRData(pathToRData);
            rHandler.loadLibraries(new String[]{"raster", "rasterVis", "grid"});
            rHandler.executeVoidFunction(function, new String[]{"'" + pathToMatrix + "'", "'" + pathToPlot + "'", String.valueOf(interval)});
        } catch (REXPMismatchException | REngineException e) {
            throw new IOException("Could not generate plot", e);
        } finally {
            if(rHandler != null)
                rHandler.closeConnection();
        }
        //caution: asynchronous call to RServe - actual file might not be available upon return
        return new URLLiteral("file:/" + pathToPlot);

    }

    @Override
    public void initializeInputConnectors() {
        addInputConnector(null, IN_FEATURE_TITLE, IN_FEATURE_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(GTVectorFeature.class),
                        new GeometryBindingConstraint(LineString.class, MultiLineString.class),
                        new MandatoryDataConstraint()},
                null,
                null);
        addInputConnector(null, IN_INTERVAL_TITLE, IN_INTERVAL_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(DecimalLiteral.class)},
                null,
                null);
        addInputConnector(null, IN_PLOT_TITLE, IN_PLOT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(BooleanLiteral.class)},
                null,
                new BooleanLiteral(false));
    }

    @Override
    public void initializeOutputConnectors() {
        addOutputConnector(null, OUT_MATRIX_TITLE, OUT_MATRIX_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class),
                        new MandatoryDataConstraint()},
                null);
        addOutputConnector(null, OUT_PLOT_TITLE, OUT_PLOT_DESCRIPTION,
                new IRuntimeConstraint[]{
                        new BindingConstraint(URLLiteral.class)},
                null);
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
