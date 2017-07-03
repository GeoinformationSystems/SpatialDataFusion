package de.tudresden.geoinfo.fusion.operation.retrieval;

import de.tudresden.geoinfo.fusion.data.IData;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTFeatureCollection;
import de.tudresden.geoinfo.fusion.data.feature.geotools.GTVectorFeature;
import de.tudresden.geoinfo.fusion.data.literal.StringLiteral;
import de.tudresden.geoinfo.fusion.data.literal.URLLiteral;
import de.tudresden.geoinfo.fusion.operation.AbstractOperation;
import de.tudresden.geoinfo.fusion.operation.AbstractTest;
import de.tudresden.geoinfo.fusion.operation.ows.WFSProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WFSProxyTest extends AbstractTest {

    private final static String IN_FORMAT = "IN_FORMAT";
    private final static String IN_VERSION = "IN_VERSION";
    private final static String IN_LAYER = "IN_LAYER";
    private final static String IN_FID = "IN_FID";

    private final static String OUT_FEATURES = "OUT_FEATURES";

    private final static String sResource = "https://www.pegelonline.wsv.de/webservices/gis/aktuell/wfs";

    @Test
    public void readWFS_100() throws MalformedURLException {
        readGML(null,
                new StringLiteral("1.0.0"),
                new StringLiteral("gk:waterlevels"),
                null);
    }

    @Test
    public void readWFS_110() throws MalformedURLException {
        readGML(null,
                new StringLiteral("1.1.0"),
                new StringLiteral("gk:waterlevels"),
                new StringLiteral("waterlevels.1,waterlevels.2"));
    }

    @Test
    public void readWFS_JSON() throws MalformedURLException {
        readGML(new StringLiteral("json"),
                new StringLiteral("1.1.0"),
                new StringLiteral("gk:waterlevels"),
                new StringLiteral("waterlevels.1,waterlevels.2"));
    }

    private void readGML(@Nullable StringLiteral format, @Nullable StringLiteral version, @NotNull StringLiteral layer, @Nullable StringLiteral fids) throws MalformedURLException {

        AbstractOperation operation = new WFSProxy(null, new URLLiteral(new URL(sResource)));

        Map<String,IData> inputs = new HashMap<>();
        inputs.put(IN_FORMAT, format);
        inputs.put(IN_VERSION, version);
        inputs.put(IN_LAYER, layer);
        inputs.put(IN_FID, fids);

        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);
        if (fids != null) {
            for (String fid : fids.resolve().split(",")) {
                outputs.put(fid, GTVectorFeature.class);
            }
        }

        this.execute(operation, inputs, outputs);

    }

    private Map<String,Class<? extends IData>> getOuputs(StringLiteral fids){
        Map<String,Class<? extends IData>> outputs = new HashMap<>();
        outputs.put(OUT_FEATURES, GTFeatureCollection.class);
        if (fids != null) {
            for (String fid : fids.resolve().split(",")) {
                outputs.put(fid, GTVectorFeature.class);
            }
        }
        return outputs;
    }

}
