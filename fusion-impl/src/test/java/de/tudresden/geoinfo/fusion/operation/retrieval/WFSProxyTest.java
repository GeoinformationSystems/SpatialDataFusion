package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.literal.LongLiteral;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WFSProxyTest {

    private final static String IN_FORMAT = "IN_FORMAT";
    private final static String IN_VERSION = "IN_VERSION";
    private final static String IN_LAYER = "IN_LAYER";
    private final static String IN_FID = "IN_FID";

    private final static String OUT_FEATURES = "OUT_FEATURES";
    private final static String OUT_RUNTIME = "OUT_RUNTIME";

    @Test
    public void readWFS_PegelOnline10() throws MalformedURLException {
        readGML(new URLLiteral(new URL("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs")),
                null,
                new StringLiteral("1.0.0"),
                new StringLiteral("gk:waterlevels"),
                null);
    }

    @Test
    public void readWFS_PegelOnline11() throws MalformedURLException {
        readGML(new URLLiteral(new URL("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs")),
                null,
                new StringLiteral("1.1.0"),
                new StringLiteral("gk:waterlevels"),
                new StringLiteral("waterlevels.1,waterlevels.2"));
    }

    @Test
    public void readWFS_PegelOnlineJSON() throws MalformedURLException {
        readGML(new URLLiteral(new URL("https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs")),
                new StringLiteral("json"),
                new StringLiteral("1.1.0"),
                new StringLiteral("gk:waterlevels"),
                new StringLiteral("waterlevels.1,waterlevels.2"));
    }

    private void readGML(@NotNull URLLiteral base, @Nullable StringLiteral format, @Nullable StringLiteral version, @NotNull StringLiteral layer, @Nullable StringLiteral fid) {

        WFSProxy parser = new WFSProxy(null, base);
        IIdentifier ID_IN_FORMAT = parser.getInputConnector(IN_FORMAT).getIdentifier();
        IIdentifier ID_IN_VERSION = parser.getInputConnector(IN_VERSION).getIdentifier();
        IIdentifier ID_IN_LAYER = parser.getInputConnector(IN_LAYER).getIdentifier();
        IIdentifier ID_IN_FID = parser.getInputConnector(IN_FID).getIdentifier();
        IIdentifier ID_OUT_FEATURES = parser.getOutputConnector(OUT_FEATURES).getIdentifier();
        IIdentifier ID_OUT_RUNTIME = parser.getOutputConnector(OUT_RUNTIME).getIdentifier();

        Map<IIdentifier, IData> input = new HashMap<>();
        input.put(ID_IN_FORMAT, format);
        input.put(ID_IN_VERSION, version);
        input.put(ID_IN_LAYER, layer);
        input.put(ID_IN_FID, fid);

        Map<IIdentifier, IData> output = parser.execute(input);

        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey(ID_OUT_FEATURES));
        Assert.assertTrue(output.get(ID_OUT_FEATURES) instanceof GTFeatureCollection);

        GTFeatureCollection features = (GTFeatureCollection) output.get(ID_OUT_FEATURES);
        Assert.assertTrue(features.size() > 0);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        System.out.print("TEST: " + parser.getTitle() + "\n\t" +
                "features read from WFS: " + features.size() + "\n\t" +
                "bounds: " + features.getBounds() + "\n\t" +
                "feature crs: " + (features.getReferenceSystem() != null ? features.getReferenceSystem().getName() : "not set") + "\n\t" +
                "process runtime (ms): " + ((LongLiteral) output.get(ID_OUT_RUNTIME)).resolve() + "\n\t" +
                "memory usage (mb): " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024L)) + "\n");
    }

}
