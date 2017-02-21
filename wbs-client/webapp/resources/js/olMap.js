/**
 * OpenLayers map handler
 * @param div map display div
 * @param crs default crs
 * @param zoom initial zoom level
 * @param center_wgs84 initial map center
 * @param coordDiv coordinate display div
 */
function olMap(div, crs, zoom, center_wgs84, coordDiv) {

    this.crs = crs;
    this.zoom = zoom;
    this.center = ol.proj.transform(center_wgs84, ol.proj.get("EPSG:4326"), this.crs);

    this.swipe = document.getElementById('swipe');
    this.div_swipe = document.getElementById('d_swipe');

    // basemaps
    this.basemaps = [];
    this.selectedBasemaps = [];

    // vector layers
    this.layers = [];
    this.selectedLayers = [];

    /**
     * add basemap
     */
    this.addBasemap = function (key, layer) {
        this.addLayer(this.basemaps, key, layer);
    };

    /**
     * add layer
     */
    this.addVectorLayer = function (key, layer) {
        this.addLayer(this.layers, key, layer);
    };

    /**
     * add a layer
     */
    this.addLayer = function (layers, key, layer) {
        layers[key] = layer;
    };

    /**
     * select a basemap
     * @param layers input layers
     * @param selection input layer selection
     * @param key layer key to be selected
     */
    this.select = function (layers, selection, key) {
        if (key in layers && selection.indexOf(key) === -1) {
            selection.push(key);
            this.map.addLayer(layers[key]);
        }
    };

    /**
     * unselect a layer
     * @param layers input layers
     * @param selection input layer selection
     * @param key layer key to be unselected
     */
    this.unselect = function (layers, selection, key) {
        if (selection.indexOf(key) !== -1) {
            this.map.removeLayer(layers[key]);
            selection.splice(selection.indexOf(key));
        }
    };

    /**
     * initialize map display with default key
     * @param defaultKey default basemap key
     */
    this.initMap = function (defaultKey) {
        if (!(defaultKey in this.basemaps))
            return;
        this.map = new ol.Map({
            controls: ol.control.defaults().extend([f_getMousePositionControl(coordDiv, "4326")]),
            target: div,
            layers: [this.basemaps[defaultKey]],
            view: new ol.View({
                projection: this.crs,
                center: this.center,
                zoom: this.zoom
            })
        });
    };

    /**
     * update basemap selection from bean
     * @param sKeys layer keys, separated by semicolon
     */
    this.updateBasemapSelection = function (sKeys) {
        this.updateSelection(this.basemaps, this.selectedBasemaps, sKeys);
        // update swipe effect
        this.updateSwipe();
    };

    /**
     * update overlay selection from bean
     * @param sKeys layer keys, separated by semicolon
     */
    this.updateLayerSelection = function (sKeys) {
        this.updateSelection(this.layers, this.selectedLayers, sKeys);
    };

    /**
     * update layer selection
     * @param layers input layers
     * @param selection input layer selection
     * @param sKeys layer keys, separated by semicolon
     */
    this.updateSelection = function (layers, selection, sKeys) {
        var keys = sKeys.split(";");
        //unselect
        for (var i = 0; i < selection.length; i++) {
            if (keys.indexOf(selection[i]) === -1)
                this.unselect(layers, selection, selection[i]);
        }
        //select
        for (i = 0; i < keys.length; i++) {
            this.select(layers, selection, keys[i]);
        }
    };

    /**
     * update swipe interaction based on number of selected basemaps
     */
    this.updateSwipe = function () {
        if (this.selectedBasemaps.length == 2)
            this.activateSwipe(this.selectedBasemaps[0], this.selectedBasemaps[1]);
        else
            this.deactivateSwipe();
    };

    /**
     * activate swipe interaction
     * @param key1 key for basemap 1
     * @param key2 key for basemap 2
     */
    this.activateSwipe = function (key1, key2) {
        this.swipeLayer = key2;
        this.div_swipe.style.visibility = "visible";
        this.basemaps[this.swipeLayer].on('precompose', this.f_swipeOnPrecompose);
        this.basemaps[this.swipeLayer].on('postcompose', this.f_swipeOnPostcompose);
        this.swipe.addEventListener('input', this.f_swipeInputListener, false);
        this.swipe.value = 50;
    };

    /**
     * deactivate swipe interaction
     */
    this.deactivateSwipe = function () {
        if (this.swipeLayer == null || this.swipeLayer == undefined)
            return;
        this.div_swipe.style.visibility = "hidden";
        this.basemaps[this.swipeLayer].un('precompose', this.f_swipeOnPrecompose);
        this.basemaps[this.swipeLayer].un('postcompose', this.f_swipeOnPostcompose);
        this.swipe.removeEventListener('input', this.f_swipeInputListener);
    };

    /**
     * swipe function (onprecompose)
     * @param event swipe event
     */
    this.f_swipeOnPrecompose = function (event) {
        var ctx = event.context;
        var width = ctx.canvas.width * (swipe.value / 100);
        ctx.save();
        ctx.beginPath();
        ctx.rect(width, 0, ctx.canvas.width - width, ctx.canvas.height);
        ctx.clip();
    };

    /**
     * swipe function (onpostcompose)
     * @param event swipe event
     */
    this.f_swipeOnPostcompose = function (event) {
        var ctx = event.context;
        ctx.restore();
    };

    /**
     * swipe listener
     */
    this.f_swipeInputListener = function () {
        olMap.map.render();
    };

    /**
     * add select interaction for vector layers
     * @param layer input layer
     */
    this.f_registerInteractions = function () {
        // add single select on click
        this.selectInteration = f_getSelectInteraction();
        this.map.addInteraction(this.selectInteration);
        this.selectedFeatures = this.selectInteration.getFeatures();
        // add bbox selection
        this.bboxInteraction = f_getBBoxInteraction();
        this.map.addInteraction(this.bboxInteraction);
        this.bboxInteraction.on('boxstart', function (e) {
            olMap.selectedFeatures.clear();
        });
        this.bboxInteraction.on('boxend', function () {
            var extent = olMap.bboxInteraction.getGeometry().getExtent();
            for (var i = 0; i < olMap.selectedLayers.length; i++) {
                olMap.layers[olMap.selectedLayers[i]].getSource().forEachFeatureIntersectingExtent(extent, function (feature) {
                    olMap.selectedFeatures.push(feature);
                });
            }
        });
        //add highlicht
        this.highlight = f_getHighlightInteraction();
        this.map.addInteraction(this.highlight);
        //add JSON representation for selected features
        this.selectedFeatures.on('change:length', function (evt) {
            olMap.f_updateSelectInfo();
            //JSF function: set selected layer features
            pf_setSelectedFeatures(olMap.f_getJSONFeatures());
        });
        //add show info
        this.map.on('pointermove', function (evt) {
            if (evt.dragging)
                return;
            var pixel = olMap.map.getEventPixel(evt.originalEvent);
            olMap.f_updateHoverInfo(pixel);
        });
    };

    /**
     * update information on feature on pointer
     * @param pixel map pixel
     */
    this.f_updateHoverInfo = function (pixel) {
        var features = [];
        olMap.map.forEachFeatureAtPixel(pixel, function (feature) {
            if (olMap.selectedFeatures.getArray().indexOf(feature) == -1)
                features.push(feature);
        });
        olMap.f_updateInfo('info_hover', features, false, false);
    };

    /**
     * update information on selected features
     */
    this.f_updateSelectInfo = function () {
        if (olMap.selectedFeatures)
            olMap.f_updateInfo('info_select', olMap.selectedFeatures.getArray(), true, true);
        olMap.f_updateInfo('info_hover', [], false, false);
    };

    /**
     * update feature info
     */
    this.f_updateInfo = function (div, features, getProperties, withHighlight) {
        var info = document.getElementById(div);
        if (features && features.length > 0) {
            info.style.display = "block";
            info.innerHTML = olMap.f_getFeatureInfo(features, getProperties, withHighlight);
        } else {
            info.innerHTML = '&nbsp;';
            info.style.display = "none";
        }
    }

    /**
     * get feature info
     * @param features input features
     * @returns {string}
     */
    this.f_getFeatureInfo = function (features, getProperties, withHighlight) {
        var info = "";
        for (var i = 0; i < features.length; i++) {
            var feature = features[i];
            info += olMap.f_getHTMLFeatureId(feature, withHighlight);
            if (getProperties) {
                for (var property in feature.getProperties()) {
                    if (!property || property == 'geometry')
                        continue;
                    info += olMap.f_getHTMLPropertyName(property) + ": " + olMap.f_getHTMLPropertyValue(feature, property) + "<br />";
                }
            }
        }
        return info;
    }

    this.f_getHTMLFeatureId = function (feature, withHighlight) {
        var html = '<div class="featureId"'
        var featureId = feature.getId();
        if (withHighlight && typeof featureId !== 'undefined')
            html += ' onmouseover="olMap.f_highlightFeature(\'' + featureId + '\')" onmouseout="olMap.f_clearHighlight()"';
        html += '>' + featureId + '</div>';
        return html;
    }

    this.f_getHTMLPropertyName = function (property) {
        return '<span class="propertyName">' + property + '</span>';
    }

    this.f_getHTMLPropertyValue = function (feature, property) {
        return '<span class="propertyValue">' + feature.get(property) + '</span>';
    }

    //function: highlight feature identified by id
    this.f_highlightFeature = function (featureId) {
        this.f_clearHighlight();
        var feature = this.f_getFeatureById(featureId);
        if (feature !== 'undefined')
            this.highlight.getFeatures().push(feature);
    };

    /**
     * clear highlight
     */
    this.f_clearHighlight = function () {
        this.highlight.getFeatures().clear();
    };

    /**
     * get feature by id
     * @param featureId
     */
    this.f_getFeatureById = function (featureId) {
        var featureById;
        this.selectedFeatures.forEach(function (feature) {
            if (feature.getId() == featureId)
                featureById = feature;
        });
        return featureById;
    };

    /**
     * get selected features by layer
     * @param sLayer input layer
     * @return {Array} selected features from input layer
     */
    this.f_getSelectedFeaturesByLayer = function (sLayer) {
        var features = [];
        this.selectedFeatures.forEach(function (feature) {
            if (this.f_featureInLayer(sLayer, feature))
                features.push(feature);
        }, this);
        return features;
    };

    /**
     * check if feature is within spaecified layer
     * @param sLayer input layer
     * @param feature input feature
     * @returns true, if feature is in layer
     */
    this.f_featureInLayer = function (sLayer, feature) {
        return this.layers[sLayer].getSource().getFeatureById(feature.getId()) != null;
    };

    /**
     * get features as json string
     * @returns {string}
     */
    this.f_getJSONFeatures = function () {
        var features = olMap.selectedFeatures.getArray();
        if (features.length === 0)
            return "";
        var jFormat = new ol.format.GeoJSON();
        return JSON.stringify(jFormat.writeFeatures(features));
    }

}

//get WMS layer object
function f_getWMSLayer(url, layer) {
    return new ol.layer.Image({
        source: new ol.source.ImageWMS({
            ratio: 1,
            url: url,
            params: {
                'FORMAT': 'image/png',
                'VERSION': '1.1.1',
                STYLES: '',
                LAYERS: layer,
            }
        })
    });
}

// get OSM layer object
function f_getOSMLayer() {
    return new ol.layer.Tile({
        source: new ol.source.OSM()
    });
}

// get vector layers from WFS (uses JSON)
function f_getWFSLayer(baseURL, typename, style) {
    var url = (baseURL.endsWith('?') ? baseURL : baseURL + "?") +
        "service=WFS" +
        "&version=1.0.0" +
        "&request=GetFeature" +
        "&outputformat=json" +
        "&typeName=" + typename;
    return new ol.layer.Vector({
        source: f_getJSONSource(url),
        style: style
    });
}

// get JSON resource from URL
function f_getJSONSource(url) {
    return new ol.source.Vector({
        format: new ol.format.GeoJSON(),
        url: function (extent) {
            return url + '&bbox=' + extent.join(',') + ',EPSG:3857';
        },
        strategy: ol.loadingstrategy.bbox
    });
}

// // get vector layers from WFS (uses JSON)
// function f_getWFSLayer2(url, sLayer, style) {
//     var vectorSource = new ol.source.Vector();
//     var layer = new ol.layer.Vector({
//         source: vectorSource,
//         style: style
//     });
//     fetch(url, {
//         method: 'POST',
//         body: new XMLSerializer().serializeToString(f_getWFSGetFeatureRequest(sLayer))
//     }).then(function(response) {
//         return response.json();
//     }).then(function(json) {
//         var features = new ol.format.GeoJSON().readFeatures(json);
//         vectorSource.addFeatures(features);
//     });
//     return layer;
// }
//
// function f_getWFSGetFeatureRequest(sLayer) {
//     return new ol.format.WFS().writeGetFeature({
//         srsName: 'EPSG:3857',
//         featureTypes: [sLayer],
//         outputFormat: 'application/json',
//     });
//
// }

//mouse position control for map
function f_getMousePositionControl(div, epsg) {
    return new ol.control.MousePosition({
        className: 'custom-mouse-position',
        projection: 'EPSG:' + epsg,
        target: document.getElementById(div),
        coordinateFormat: ol.coordinate.createStringXY(5),
        undefinedHTML: '&nbsp;'
    });
}

//get select interaction
function f_getSelectInteraction() {
    return new ol.interaction.Select({
        condition: ol.events.condition.click,
        multi: true
    });
}

// get bbox interaction
function f_getBBoxInteraction() {
    return new ol.interaction.DragBox({
        condition: ol.events.condition.platformModifierKeyOnly,
    });
}

// get highlight interaction
function f_getHighlightInteraction() {
    return new ol.interaction.Select({
        condition: ol.events.condition.pointerMove, //quite slow, especially if used with WMS GetFeatureInfo
//		  condition: ol.events.condition.never,
        style: [
            new ol.style.Style({
                stroke: new ol.style.Stroke({color: '#FFFFFF', width: 5}),
                fill: new ol.style.Fill({color: [195, 0, 0, 0.5]}),
                image: new ol.style.Circle({radius: 5, fill: new ol.style.Fill({color: '#FFFFFF'})})
            }),
            new ol.style.Style({
                stroke: new ol.style.Stroke({color: '#C30000', width: 3}),
                image: new ol.style.Circle({radius: 3, fill: new ol.style.Fill({color: '#C30000'})})
            }),
        ]
    });
}