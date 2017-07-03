/**
 * default map reference system
 * @type {ol.proj.Projection}
 */
var basemap_default_crs = new ol.proj.Projection({code: 'EPSG:3857', units: 'm', axisOrientation: 'neu'});

/**
 * map div
 * @type {string}
 */
var div_map = "map";

/**
 * coordinate div
 * @type {string}
 */
var div_coord = "coord";

/**
 * register WMS layer to map and JSF
 * @param olMap olMap object
 * @param key WMS layer key
 * @param url WMS base URL
 * @param sLayer WMS layer name
 * @param selected flag: select layer
 */
f_registerWMSBasemap = function (uid, olMap, url, sLayer, selected) {
    f_registerJSBasemap(uid, olMap, sLayer, f_getWMSLayer(url, sLayer));
    f_registerJSFBasemap(uid, url, sLayer, selected);
};

/**
 * register WMS layer to map from JSF
 * @param olMap olMap object
 * @param key WMS layer key
 * @param url WMS base URL
 * @param sLayer WMS layer name
 */
f_registerWMSBasemapFromJSF = function (uid, olMap, url, sLayer) {
    f_registerJSBasemap(uid, olMap, sLayer, f_getWMSLayer(url, sLayer));
};

/**
 * register OSM layer to map
 * @param olMap olMap object
 * @param key OSM layer key
 * @param selected flag: select layer
 */
f_registerOSMBasemap = function (uid, olMap, selected) {
    f_registerJSBasemap(uid, olMap, "OpenStreetMap", f_getOSMLayer());
    f_registerJSFBasemap(uid, "", "OpenStreetMap", selected);
};

/**
 * register OL layer to map and JSF
 * @param olMap ol map handler
 * @param sLayer basemap layer key
 * @param olLayer OpenLayers layer object
 */
f_registerJSBasemap = function (uid, olMap, sLayer, olLayer) {
    olMap.addBasemap(uid, sLayer, olLayer);
};

/**
 * register OL layer to JSF
 * @param key basemap layer key
 * @param url WMS url
 * @param sLayer layer name
 * @param selected flag: select layer
 */
f_registerJSFBasemap = function (uid, url, sLayer, selected) {
    pf_registerBasemap(uid, url, sLayer, selected);
};

/**
 * register WFS layer to map and JSF
 * @param olMap olMap object
 * @param key WFS layer key
 * @param baseURL WFS base URL
 * @param typename WFS layer name
 * @param selected flag: select layer
 */
f_registerWFSLayer = function (uid, olMap, baseURL, typename, selected, style) {
    f_registerJSLayer(uid, olMap, typename, f_getWFSLayer(baseURL, typename, style));
    f_registerJSFLayer(uid, baseURL, typename, selected);
};

/**
 * register WFS layer to map from JSF
 * @param olMap olMap object
 * @param key WFS layer key
 * @param baseURL WFS base URL
 * @param typename WFS layer name
 */
f_registerWFSLayerFromJSF = function (uid, olMap, baseURL, typename, style) {
    f_registerJSLayer(uid, olMap, typename, f_getWFSLayer(baseURL, typename, style));
};

/**
 * register OL layer to map and JSF
 * @param olMap ol map handler
 * @param typename basemap layer name
 * @param olLayer OpenLayers layer object
 */
f_registerJSLayer = function (uid, olMap, typename, olLayer) {
    olMap.addVectorLayer(uid, typename, olLayer);
};

/**
 * register OL layer to JSF
 * @param baseURL WFS url
 * @param typename layer name
 * @param selected flag: select layer
 */
f_registerJSFLayer = function (uid, baseURL, typename, selected) {
    pf_registerLayer(uid, baseURL, typename, selected);
};

/**
 * global OL map handler
 * @type {olMap}
 */
var olMap = new olMap(div_map, basemap_default_crs, 12, [13.73, 51.05], div_coord);

//register basemaps
f_registerOSMBasemap("predefinedBasemap_01", olMap, true);
f_registerWMSBasemap("predefinedBasemap_02", olMap, "http://localhost:8081/geoserver/gi/wms", "clc2006", false);
f_registerWMSBasemap("predefinedBasemap_03", olMap, "http://localhost:8081/geoserver/gi/wms", "dgm-10m", false);
f_registerWMSBasemap("predefinedBasemap_04", olMap, "http://localhost:8081/geoserver/gi/wms", "sn_wfd_quality", false);

//init map with default
olMap.initMap("predefinedBasemap_01");

//register vector layers
f_registerWFSLayer("predefinedOverlay_01", olMap, "http://localhost:8081/geoserver/gi/wfs", "gi:sn_river_network", false, new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#0000DD',
        width: 2
    })
}))
f_registerWFSLayer("predefinedOverlay_02", olMap, "http://localhost:8081/geoserver/gi/wfs", "gi:ueg", false, new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#0000DD',
        width: 1
    }), fill: new ol.style.Fill({color: 'rgba(16,170,220,0.6)'})
}))
f_registerWFSLayer("predefinedOverlay_03", olMap, "http://localhost:8081/geoserver/gi/wfs", "gi:cobweb", false, new ol.style.Style({
    image: new ol.style.Circle({
        stroke: new ol.style.Stroke({
            color: '#118C00',
            width: 1
        }), fill: new ol.style.Fill({color: '#118C00'}), radius: 3
    })
}))

//register interactions
olMap.f_registerInteractions();

//append a processing result
f_appendResultMessage = function(message) {
    var results = $(document.getElementById(":form:div_results")).innerHTML;
    $(document.getElementById("div_results")).html(results + message);
};