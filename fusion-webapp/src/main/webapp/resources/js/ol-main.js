//init ol resources
var r_resource;
var r_map;
function f_initMapResource_r(fRequest, crs, center_wgs84, extent) {
    //if fRequest == map.fRequest, do nothing
    if (r_resource != null && r_resource.fRequest == fRequest)
        return;
    r_resource = f_initOlResource(r_resource, r_map, fRequest, crs, extent, f_getStyle(r_color));
    r_map = new olMap(r_resource, 'map_r', crs, center_wgs84, defaultZoom, 'rMarker');
    r_map.registerBasicInteractions();
    f_initStats();
    f_initComparison();
}

var t_resource;
var t_map;
function f_initMapResource_t(fRequest, crs, center_wgs84, extent) {
    //if fRequest == map.fRequest, do nothing
    if (t_resource != null && t_resource.fRequest == fRequest)
        return;
    t_resource = f_initOlResource(t_resource, t_map, fRequest, crs, extent, f_getStyle(t_color));
    t_map = new olMap(t_resource, 'map_t', crs, center_wgs84, defaultZoom, 'tMarker');
    t_map.registerBasicInteractions();
    f_initStats();
    f_initComparison();
}

//init stats
function f_initStats() {
    f_showStats(r_resource, t_resource, "stats_rt");
}

//init comparison of reference and target
function f_initComparison() {
    if (!f_bothMapsSet())
        return;
    //change interaction if fusion results are activated
    f_setCompareInteraction();
}

//function: get mapObject by div name
function f_getMapObject(div_map) {
    if (div_map == "map_r") return r_map;
    if (div_map == "map_t") return t_map;
    return null;
}

var viewBinding = false;
function f_toggleViewBind(bind) {
    viewBinding = !viewBinding;
    if (viewBinding) {
        f_bindTargetToReference();
        f_bindReferenceToTarget();
        //synch cursors
        f_synchCursors();
    }
    else {
        f_unbindReference();
        f_unbindTarget();
        //unsynch cursors
        f_unsynchCursors();
    }
}

//function: set fusion result usage handler
var useRelations = false;
function f_showRelations(checked) {
    useRelations = !useRelations;
    f_setCompareInteraction();
}

//set interaction based on fusion result flag
function f_setCompareInteraction() {
    if (r_resource == null || t_resource == null || r_map == null || t_map == null)
        return;
    r_map.setCompareInteraction(useRelations, r_map, t_map, true);
    t_map.setCompareInteraction(useRelations, t_map, r_map, false);
}

function f_synchCursors() {
    //synch
    r_map.synchCursor(t_map);
    t_map.synchCursor(r_map);
}

function f_unsynchCursors() {
    //synch
    r_map.unsynchCursor(t_map);
    t_map.unsynchCursor(r_map);
}

//synch target view with reference
function f_synchTarget() {
    if (viewBinding)
        f_unbindReference();
    t_map.map.getView().setProperties(r_map.map.getView().getProperties())
    if (viewBinding)
        f_bindReferenceToTarget();
}

//synch reference view with target
function f_synchReference() {
    if (viewBinding)
        f_unbindTarget();
    r_map.map.getView().setProperties(t_map.map.getView().getProperties())
    if (viewBinding)
        f_bindTargetToReference();
}

//manage synch listener to target view
var listener_tr;
function f_bindReferenceToTarget() {
    f_synchReference();
    listener_tr = t_map.map.getView().on('propertychange', function (evt) {
        f_synchReference();
    });
}
function f_unbindReference() {
    t_map.map.getView().unByKey(listener_tr);
}

//manage synch listener to reference view
var listener_rt;
function f_bindTargetToReference() {
    listener_rt = r_map.map.getView().on('propertychange', function (evt) {
        f_synchTarget();
    });
}
function f_unbindTarget() {
    r_map.map.getView().unByKey(listener_rt);
}

//toggle view binding
var viewBinding = false;
function f_toggleViewBind(bind) {
    viewBinding = !viewBinding;
    if (viewBinding) {
        f_bindTargetToReference();
        f_bindReferenceToTarget();
        //synch cursors
        f_synchCursors();
    }
    else {
        f_unbindReference();
        f_unbindTarget();
        //unsynch cursors
        f_unsynchCursors();
    }
}

/**
 * check if both map objects are set
 * @returns {Boolean}
 */
function f_bothMapsSet() {
    return (r_resource != null && t_resource != null && r_map != null && t_map != null);
}

//style listener
document.body.onload = function () {
    $(".ui-colorpicker-container").each(function () {
        $(this).data('colorpicker').onHide = function () {
            f_applyReferenceStyle();
            f_applyTargetStyle();
        }
    })
};

//apply reference style
var r_color = document.getElementById('form:menu_top:r_color_input').value
function f_applyReferenceStyle() {
    if (r_color == document.getElementById('form:menu_top:r_color_input').value)
        return;
    r_color = document.getElementById('form:menu_top:r_color_input').value;
    if (r_map != null)
        r_map.map.getLayers().item(0).setStyle(f_getStyle(r_color));
}

//apply target style
var t_color = document.getElementById('form:menu_top:t_color_input').value
function f_applyTargetStyle() {
    if (t_color == document.getElementById('form:menu_top:t_color_input').value)
        return;
    t_color = document.getElementById('form:menu_top:t_color_input').value;
    if (t_map != null)
        t_map.map.getLayers().item(0).setStyle(f_getStyle(t_color));
}

//get target style
function f_getStyle(color) {
    newColor = '#' + color;
    style = new ol.style.Style({
        stroke: new ol.style.Stroke({color: newColor, width: 1}),
        fill: new ol.style.Fill({color: '#d1e9ff'}),
        image: new ol.style.Circle({radius: 3, fill: new ol.style.Fill({color: newColor})})
    });
    return style;
}
