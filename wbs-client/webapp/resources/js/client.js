//create olMap
olMap = new olMap("map", getDefaultCRS(), 12, [13.73, 51.05], "coord");

//init layer
basemaps = [];
basemaps["OpenStreetMap"] = f_getOSMLayer();
basemaps["Corine Land Cover"] = f_getWMSLayer("http://localhost:8081/geoserver/gi/wms", "gi:clc2006");
basemaps["Digital Elevation Model"] = f_getWMSLayer("http://localhost:8081/geoserver/gi/wms", "gi:dgm-10m");

//init overlays
overlays = [];
overlays["River Network"] = f_getWFSLayer("http://localhost:8081/geoserver/gi/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=gi:sn_river_network", new ol.style.Style({stroke: new ol.style.Stroke({color:'#0000DD', width:2 })}));
overlays["WFD Quality Classification"] = f_getWMSLayer("http://localhost:8081/geoserver/gi/wms", "gi:sn_wfd_quality");
overlays["Designated Flood Plains"] = f_getWFSLayer("http://localhost:8081/geoserver/gi/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=gi:ueg", new ol.style.Style({stroke: new ol.style.Stroke({color:'#0000DD',width:2}), fill: new ol.style.Fill({color:'rgba(16,170,220,0.6)'})}));
overlays["COBWEB Observations"] = f_getWFSLayer("http://localhost:8081/geoserver/gi/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=gi:cobweb", new ol.style.Style({image: new ol.style.Circle({stroke: new ol.style.Stroke({color:'#118C00',width:1}), fill: new ol.style.Fill({color: '#118C00'}), radius: 7})}));


//init map with OSM as default
olMap.initMap(basemaps, overlays, "OpenStreetMap");

//register interactions
olMap.f_addSelectInteraction("River Network");
olMap.f_addSelectInteraction("WFD Quality Classification");
