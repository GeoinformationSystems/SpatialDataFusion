//init map resource
function f_initOlResource(resource, olMap, fRequest, crs, extent, style) {
	//reset maps if resource is not null
	if(resource != null) {
		olMap.clear();
	}
	return new olResource(fRequest, crs, extent, style);
}

/*
 * OpenLayers map resource object
 */
function olResource(fRequest, crs, extent, style){
	
	this.fRequest = fRequest;
	this.crs = crs;
	this.extent = extent;
	
	//init feature source (requires JSON format from server resource)
	this.source = f_createGeoJSONSourceFromServer(this.fRequest, this.crs);
//	this.source = f_createGMLSourceFromServer(this.fRequest, this.crs);	
	
	//init layer
	if(typeof this.source === 'undefined') return;
	this.layer = f_createVectorLayer(this.source, extent, style);

	//init statistics
	this.stats = new olStats(this);

}

/*
 * OpenLayers map object
 */
function olMap(resource, div, crs, center_wgs84, zoom, marker){
	
	//set divs
	this.div = div;
	this.div_coord = div + "_coord";
	this.div_info = div + "_info";
	
	//set parameter
	this.resource = resource;
	this.crs = crs;
	this.zoom = zoom;
	this.marker = marker;
	
	//get and transform center from WGS84 to target crs
	this.center_wgs84 = center_wgs84;
	this.center_proj = ol.proj.transform(center_wgs84, ol.proj.get("EPSG:4326"), this.crs);
	
	//init map
	this.map = f_createMap(this.resource.layer, div, this.crs, this.center_proj, zoom);
	
	//register basic select interactions
	this.registerBasicInteractions = function(){
		this.addMousePosition();
		this.addSelectInteraction(this);
		this.addHighlightInteraction(this);
		this.addBboxSelectInteraction(this);
	};
	
	//function: add mouse control for map
	this.addMousePosition = function(){
		f_addMousePosition(this.map, this.crs, this.div_coord);
	};
	
	//function: switch interactions for comparison
	this.setCompareInteraction = function(useFusionResult, r_olMapObject, t_olMapObject, isReference){
		//clear selections
		r_olMapObject.clearSelection(r_olMapObject);
		//clear previous interactions (if exist)
		r_olMapObject.removeBboxSelectInteraction();
		r_olMapObject.removeCompareInteraction(r_olMapObject);
		//set interactions
		if(useFusionResult && t_olMapObject !== 'undefined'){
			r_olMapObject.addCompareInteraction(r_olMapObject, t_olMapObject, isReference);
		}
		else {
			r_olMapObject.addBboxSelectInteraction(r_olMapObject);
		}
	};
	
	//function: add interaction
	this.addInteraction = function(interaction){
		this.map.addInteraction(interaction);
	};
	
	//function: add select interaction with event
	this.addSelectInteraction = function(olMapObject){
		olMapObject.selection = f_getSelectInteraction();
		olMapObject.addInteraction(olMapObject.selection);
		olMapObject.selectListenerKey = olMapObject.selection.getFeatures().on('change:length', function(evt) {
			olMapObject.updateFeatureInfo(olMapObject.selection.getFeatures().getArray());
		});
	};
	
	//function: remove select interaction
	this.removeSelectInteraction = function(){
		this.map.removeInteraction(this.selection);
	};
	
	//function: add highlight interaction
	this.addHighlightInteraction = function(olMapObject){
		olMapObject.highlight = f_getHighlightInteraction();
		olMapObject.addInteraction(olMapObject.highlight);
	};
	
	//function: remove highlight interaction
	this.removeHighlightInteraction = function(){
		this.map.removeInteraction(this.highlight);
	};
	
	//function: add bbox select interaction
	this.addBboxSelectInteraction = function(olMapObject){
		if(typeof olMapObject.selection === 'undefined') return;
		olMapObject.selectionBBox = f_getSelectBBoxInteraction();
		olMapObject.addInteraction(olMapObject.selectionBBox);
		olMapObject.selectionBBox.on('boxstart', function() {
			olMapObject.clearSelection(olMapObject);
		});
		olMapObject.selectionBBox.on('boxend', function() {
			var extent = olMapObject.selectionBBox.getGeometry().getExtent();
			olMapObject.resource.layer.getSource().forEachFeatureIntersectingExtent(extent, function(feature) {
				olMapObject.selection.getFeatures().push(feature);
		    });
		});
	};
	
	//function: remove bbox select interaction
	this.removeBboxSelectInteraction = function(){
		this.map.removeInteraction(this.selectionBBox);
	};
	
	//function: add compare interaction
	this.isActive = false;
	this.addCompareInteraction = function(r_olMapObject, t_olMapObject, isReference){
		//change interaction event function for selection
		r_olMapObject.selection.getFeatures().unByKey(r_olMapObject.selectListenerKey);
		r_olMapObject.selectListenerKey = r_olMapObject.selection.getFeatures().on('change:length', function(evt) {
			//update feature info
			r_olMapObject.updateFeatureInfo(r_olMapObject.selection.getFeatures().getArray());
			//return, if opposite map interaction is not active
			if(t_olMapObject.isActive) return;
			//set this map interaction active
			r_olMapObject.isActive = true;
			//clear target selection
			t_olMapObject.clearSelection(t_olMapObject);
			var array = f_getRelatedFeatures(r_olMapObject.selection.getFeatures().getArray(), isReference);
			//iterate: 1st selected reference features - 2nd referenced target features
			for(var i=0; i<array.length; i++){
				for(var j=0; j<array[i].length; j++){
					//add relation attributes and add feature to target selection
					var feature = f_cloneFeatureWithId(t_olMapObject.getFeatureById(f_getSimpleId(array[i][j].featureId, true)));
					var measures = array[i][j].relation.relationMeasures;
					for(var k=0; k<measures.length; k++){
						feature.set(f_getSimpleId(measures[k].relationType, false), f_getSimpleMeasurementValue(measures[k].relationValue));
					}
					t_olMapObject.selection.getFeatures().push(feature);
				}
			}
			//set this map interaction inactive
			r_olMapObject.isActive = false;
		});
	};
	
	//function: remove compare interaction
	this.removeCompareInteraction = function(olMapObject){
		//restore basic select interaction
		olMapObject.selection.getFeatures().unByKey(olMapObject.selectListenerKey);
		selectListenerKey = olMapObject.selection.getFeatures().on('change:length', function(evt) {
			olMapObject.updateFeatureInfo(olMapObject.selection.getFeatures().getArray());
		});
	};
 
	//function: clear map
	this.clear = function(){
		f_clearText(this.div_info, true);
//		this.map.unbindAll();
		this.map.setTarget(null);
		this.map.setView(null);
		this.map.setLayerGroup(null);
		//remove overlays
		for(var overlay in this.map.getOverlays()){
			this.map.removeOverlay(overlay);
		}
	};
	
	//function: update feature info field
	this.updateFeatureInfo = function(featureArray){
		//clear info
		f_clearText(this.div_info, true);
		if(typeof featureArray === 'undefined') return;
		//add info for each feature
		var html = '<div class="infoProperties">';
		for(var feature in featureArray){
			html += this.getHTMLFeatureInfo(featureArray[feature]);
		}
		html += '</div>';
		f_setText(this.div_info, html, true);
	};
	
	//function: get feature info as html
	this.getHTMLFeatureInfo = function(feature) {
		//check if feature is set
		if(!feature || !(feature instanceof ol.Feature)) return '&nbsp;';
		//get feature properties
		var featureIdDefined = false;
		var featureId = feature.getId();
		if(typeof featureId !== 'undefined')
			featureIdDefined = true;
		var properties = feature.getProperties();
		//check, if align is right
		var alignRight = (document.getElementById(this.div_info).className.indexOf("alignRight") > -1);
		//add id property
		var html = '<div>' +
			(alignRight ? 
					(this.getHTMLIdValue(featureId, featureIdDefined) + this.getHTMLIdProperty()) :
					(this.getHTMLIdProperty() + this.getHTMLIdValue(featureId, featureIdDefined))) + '<br />';
		//add properties (add to featureId class to enable visibility toggling)
		html += '<div class="elementProperties ' + featureId + '">';
		for(var property in properties){
			if(!property || property == 'geometry') continue;
			//add property
			html += (alignRight ? 
					(this.getHTMLAttValue(feature, property) + this.getHTMLAttProperty(property)) :
					(this.getHTMLAttProperty(property) + this.getHTMLAttValue(feature, property))) + '<br />';
		}
		html += '<span style="width:100%;"></div>';
		//return
		return html;
	};
	
	//function: get id property field as HTML
	this.getHTMLIdProperty = function() {
		return '<span class="idproperty">&nbsp;ID&nbsp;</span>';
	};

	//function: get id property value as HTML
	this.getHTMLIdValue = function(featureId, featureIdDefined) {
		return '<span class="idvalue" ' +
			(featureIdDefined ?
				'onmouseover="f_highlightFeature(\'' + this.div + '\', \'' + featureId + '\')" ' +
				'onmouseout="f_clearHighlight(\'' + this.div + '\')"' : '') +
				'onclick="f_changeVisibility(\'' + featureId + '\')"' +
		'>' + featureId + '</span>';
	};

	//function: get feature property field as HTML
	this.getHTMLAttProperty = function(property) {
		return '<span class="property">' + property + '</span>';
	};

	//function: get feature property value as HTML
	this.getHTMLAttValue = function(feature, property) {
		return '<span class="value">' + ((feature.get(property) == null || feature.get(property).length == 0) ? '<i>null</i>' : feature.get(property)) + '</span>';
	};
	
	//function: highlight feature identified by id
	this.highlightFeature = function(featureId){
		this.clearHighlight();
		var feature = this.getFeatureById(featureId);
		if(feature !== 'undefined')
			this.highlight.getFeatures().push(feature);
	};
	
	//function: clear selections
	this.clearSelection = function(olMapObject){
		olMapObject.selection.getFeatures().clear();
	};
	
	//function: clear highlights
	this.clearHighlight = function(){
		this.highlight.getFeatures().clear();
	};
	
	//function: get feature by id
	this.getFeatureById = function(featureId){
		return this.resource.layer.getSource().getFeatureById(featureId);
	};
	
	this.removeSynch = function(){
		document.getElementById(this.div).removeEventListener("mouseenter", function(){
			olMap.markerVisible(true);
		});
		document.getElementById(this.div).removeEventListener("mouseleave", function(){
			olMap.markerVisible(false);
		});
	};
	
	//synch cursor with second olMap object
	var markerOverlay;
	var pointermove;
	this.synchCursor = function(olMap){
		//reset
		f_resetMarker();
		//synch
		markerOverlay = f_getMarkerOverlay(this.marker);
		this.map.addOverlay(markerOverlay);
		pointermove = this.map.on('pointermove', function(evt) {
			var position = evt.coordinate;
			olMap.setMarker(position);
		});
		document.getElementById(this.div).addEventListener("mouseenter", olMap.markerVisible);
		document.getElementById(this.div).addEventListener("mouseleave", olMap.markerInvisible);
	};
	
	//synch cursor with second olMap object
	this.unsynchCursor = function(olMap){
		if(markerOverlay)
			this.map.removeOverlay(markerOverlay);
		if(pointermove)
			this.map.unByKey(pointermove);
		document.getElementById(this.div).removeEventListener("mouseenter", olMap.markerVisible);
		document.getElementById(this.div).removeEventListener("mouseleave", olMap.markerInvisible);
	}
	
	//set marker position
	this.setMarker = function(position){
		this.map.getOverlays().item(0).setPosition(position);
	};
	
	//set marker visible
	this.markerVisible = function(){
		document.getElementById(marker).style.display = "block";
	};
	
	//set marker invisible
	this.markerInvisible = function(){
		document.getElementById(marker).style.display = "none";
	};
	
}

function f_updateFeatureInfo(olMapObject){
	olMapObject.updateFeatureInfo();
}

//function: highlight feature from specified olMap
function f_highlightFeature(div_map, featureId) {
	olMapObject = f_getMapObject(div_map);
	olMapObject.highlightFeature(featureId);
}

//function: clear highlights from specified olMap
function f_clearHighlight(div_map) {
	olMapObject = f_getMapObject(div_map);
	olMapObject.clearHighlight();
}