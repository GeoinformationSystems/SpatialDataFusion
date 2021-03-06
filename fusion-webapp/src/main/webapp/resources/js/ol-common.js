//ol zoom and style defaults
var defaultZoom = 15;
var styleHighlight = new ol.style.Style({
    stroke: new ol.style.Stroke({color: '#c30000', width: 3}),
    fill: new ol.style.Fill({color: '#c30000'}),
    image: new ol.style.Circle({radius: 3, fill: new ol.style.Fill({color: '#c30000'})})
});
var styleSelect = new ol.style.Style({
    stroke: new ol.style.Stroke({color: '#ffff00', width: 2}),
    fill: new ol.style.Fill({color: '#ffff00'}),
    image: new ol.style.Circle({radius: 3, fill: new ol.style.Fill({color: '#ffff00'})})
});

//create a GeoJSON source from server resource
function f_createGeoJSONSourceFromServer(url, crs) {
    var source = null;
    url = url + "&outputformat=application/json";
    source = new ol.source.Vector({
        format: new ol.format.GeoJSON(),
        url: url,
    });
    return source;
}

//create a GML source from server resource
function f_createGMLSourceFromServer(url, crs) {
    var source = null;
    source = new ol.source.Vector({
        format: new ol.format.GML(),
        loader: function (extent, resolution, projection) {
            $.ajax(encodeURI(url)).done(function (response) {
                source.addFeatures(source.readFeatures(response));
            });
        },
        defaultProjection: crs,
    });
    return source;
}

//create a Vector layer from source
function f_createVectorLayer(source, extent, style) {
    return new ol.layer.Vector({
        source: source,
        extent: extent,
        style: style
    });
}

//create Map object from Source
function f_createMap(layer, target, crs, center, zoom) {
    return new ol.Map({
        layers: [
            layer
        ],
        controls: [
            new ol.control.Zoom()
        ],
        interactions: ol.interaction.defaults({mouseWheelZoom: false}),	//disable zoom with mouse wheel
        view: new ol.View({
            center: center,
            projection: crs,
            zoom: zoom
        }),
        target: target
    });
}

//get select interaction
function f_getSelectInteraction() {
    return new ol.interaction.Select({
        condition: ol.events.condition.click,
        style: styleSelect
    });
}

//get bbox select interaction
function f_getSelectBBoxInteraction() {
    return new ol.interaction.DragBox({
        condition: ol.events.condition.platformModifierKeyOnly,
    });
}

//get highlight select interaction
function f_getHighlightInteraction() {
    return new ol.interaction.Select({
//		  condition: ol.events.condition.pointerMove, //quite slow, especially if used with WMS GetFeatureInfo
        condition: ol.events.condition.never,
        style: styleHighlight
    });
}

//add mouse position control for map
function f_addMousePosition(map, crs, div) {
    map.addControl(
        new ol.control.MousePosition({
            coordinateFormat: ol.coordinate.createStringXY(4),
            projection: crs,
            className: 'custom-mouse-position',
            target: document.getElementById(div)
        }));
}

//function: clone feature with id
function f_cloneFeatureWithId(feature) {
    result = feature.clone();
    result.setId(feature.getId());
    return result;
}

//function: get marker overlay
function f_getMarkerOverlay(marker) {
    return new ol.Overlay({
        positioning: 'center-center',
        element: document.getElementById(marker),
        stopEvent: false
    });
}

//function: reset marker
function f_resetMarker() {
    mapMarker = document.getElementById('mapMarker');
    //remove markers
    while (mapMarker.firstChild) {
        mapMarker.removeChild(mapMarker.firstChild);
    }
    //add marker
    f_addMarker(mapMarker, 'rMarker');
    f_addMarker(mapMarker, 'tMarker');
};

//function: add marker div to parent object
function f_addMarker(parent, marker) {
    markerDiv = document.createElement('div');
    markerDiv.id = marker;
    markerDiv.className = 'marker';
    parent.appendChild(markerDiv);
};