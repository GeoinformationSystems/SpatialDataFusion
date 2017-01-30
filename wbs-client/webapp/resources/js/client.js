/**
 * Available Basemaps
 * @type {Array}
 */
var basemaps = [];

/**
 * default OL basemap
 * @type {string}
 */
var basemap_default = "OpenStreetMap";

/**
 * default map reference system
 * @type {ol.proj.Projection}
 */
var basemap_default_crs = new ol.proj.Projection({ code: 'EPSG:3857', units: 'm', axisOrientation: 'neu' });

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
 * info div
 * @type {string}
 */
var div_info = "info";

/**
 * register WMS layer to map and JSF
 * @param olMap olMap object
 * @param key WMS layer key
 * @param url WMS base URL
 * @param sLayer WMS layer name
 * @param selected flag: select layer
 */
f_registerWMSBasemap = function(olMap, key, url, sLayer, selected){
    f_registerJSBasemap(olMap, key, f_getWMSLayer(url, sLayer));
    f_registerJSFBasemap(key, url, sLayer, selected);
};

/**
 * register WMS layer to map from JSF
 * @param olMap olMap object
 * @param key WMS layer key
 * @param url WMS base URL
 * @param sLayer WMS layer name
 */
f_registerWMSBasemapFromJSF = function(olMap, key, url, sLayer){
    f_registerJSBasemap(olMap, key, f_getWMSLayer(url, sLayer));
};

/**
 * register OSM layer to map
 * @param olMap olMap object
 * @param key OSM layer key
 * @param selected flag: select layer
 */
f_registerOSMBasemap = function(olMap, key, selected){
    f_registerJSBasemap(olMap, key, f_getOSMLayer());
    f_registerJSFBasemap(key, null, null, selected);
};

/**
 * register OL layer to map and JSF
 * @param olMap ol map handler
 * @param key basemap layer key
 * @param olLayer OpenLayers layer object
 */
f_registerJSBasemap = function(olMap, key, olLayer){
    olMap.addBasemap(key, olLayer);
};

/**
 * register OL layer to JSF
 * @param key basemap layer key
 * @param url WMS url
 * @param sLayer layer name
 * @param selected flag: select layer
 */
f_registerJSFBasemap = function(key, url, sLayer, selected){
    pf_registerBasemap(key, url, sLayer, selected);
};

/**
 * register WFS layer to map and JSF
 * @param olMap olMap object
 * @param key WFS layer key
 * @param baseURL WFS base URL
 * @param typename WFS layer name
 * @param selected flag: select layer
 */
f_registerWFSLayer = function(olMap, key, baseURL, typename, selected, style){
    f_registerJSLayer(olMap, key, f_getWFSLayer(baseURL, typename, style));
    f_registerJSFLayer(key, baseURL, typename, selected);
};

/**
 * register WFS layer to map from JSF
 * @param olMap olMap object
 * @param key WFS layer key
 * @param baseURL WFS base URL
 * @param typename WFS layer name
 */
f_registerWFSLayerFromJSF = function(olMap, key, baseURL, typename, style){
    f_registerJSLayer(olMap, key, f_getWFSLayer(baseURL, typename, style));
};

/**
 * register OL layer to map and JSF
 * @param olMap ol map handler
 * @param key basemap layer key
 * @param olLayer OpenLayers layer object
 */
f_registerJSLayer = function(olMap, key, olLayer){
    olMap.addVectorLayer(key, olLayer);
};

/**
 * register OL layer to JSF
 * @param key basemap layer key
 * @param baseURL WFS url
 * @param typename layer name
 * @param selected flag: select layer
 */
f_registerJSFLayer = function(key, baseURL, typename, selected){
    pf_registerLayer(key, baseURL, typename, selected);
};

/**
 * OL map handler
 * @type {olMap}
 */
var olMap = new olMap(div_map, basemap_default_crs, 12, [13.73, 51.05], div_coord);

//register basemaps
f_registerOSMBasemap(olMap, basemap_default, true);
f_registerWMSBasemap(olMap, "Corine Land Cover", "http://localhost:8081/geoserver/gi/wms", "clc2006", false);
f_registerWMSBasemap(olMap, "Digital Elevation Model", "http://localhost:8081/geoserver/gi/wms", "dgm-10m", false);
f_registerWMSBasemap(olMap, "WFD Quality Classification", "http://localhost:8081/geoserver/gi/wms", "sn_wfd_quality", false);

//init map with default
olMap.initMap(basemap_default);

//register vector layers
f_registerWFSLayer(olMap, "River Network", "http://localhost:8081/geoserver/gi/wfs", "gi:sn_river_network", false, new ol.style.Style({stroke: new ol.style.Stroke({color:'#0000DD', width:2 })}))
f_registerWFSLayer(olMap, "Designated Flood Plains", "http://localhost:8081/geoserver/gi/wfs", "gi:ueg", false, new ol.style.Style({stroke: new ol.style.Stroke({color:'#0000DD',width:2}), fill: new ol.style.Fill({color:'rgba(16,170,220,0.6)'})}))
f_registerWFSLayer(olMap, "COBWEB Observations", "http://localhost:8081/geoserver/gi/wfs", "gi:cobweb", false, new ol.style.Style({image: new ol.style.Circle({stroke: new ol.style.Stroke({color:'#118C00',width:1}), fill: new ol.style.Fill({color: '#118C00'}), radius: 3})}))

//register interactions
olMap.f_registerInteractions();