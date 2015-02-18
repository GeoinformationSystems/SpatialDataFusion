//init ol resources
var r_resource;
var r_olMaps = [];
function f_initMapResource_r(fRequest, crs, center_wgs84, extent){
	r_resource = f_initOlResource(r_resource, r_olMaps, fRequest, crs, extent, styleDefault);
	r_olMaps['map_r'] = new olMap(r_resource, 'map_r', crs, center_wgs84, defaultZoom);
	r_olMaps['map_r'].registerBasicInteractions();
	r_olMaps['map_cr'] = new olMap(r_resource, 'map_cr', crs, center_wgs84, defaultZoom);
	r_olMaps['map_cr'].registerBasicInteractions();
	f_initStats();
	f_initComparison();
}

var t_resource;
var t_olMaps = [];
function f_initMapResource_t(fRequest, crs, center_wgs84, extent){
	t_resource = f_initOlResource(t_resource, t_olMaps, fRequest, crs, extent, styleDefault);
	t_olMaps['map_t'] = new olMap(t_resource, 'map_t', crs, center_wgs84, defaultZoom);
	t_olMaps['map_t'].registerBasicInteractions();
	t_olMaps['map_ct'] = new olMap(t_resource, 'map_ct', crs, center_wgs84, defaultZoom);
	t_olMaps['map_ct'].registerBasicInteractions();
	f_initStats();
	f_initComparison();
}

//init stats
function f_initStats(){
	f_showStats(r_resource, t_resource, "stats_rt");
}

//init comparison of reference and target
function f_initComparison(){
	if(r_resource == null || t_resource == null || r_olMaps["map_cr"] == null || t_olMaps["map_ct"] == null)
		return;
	//bind map views
	r_olMaps["map_cr"].map.bindTo('view', t_olMaps["map_ct"].map);
	//change interaction if fusion results are activated
	f_setCompareInteraction();
}

//function: get mapObject by div name
function f_getMapObject(div_map){
	if(div_map == "map_r") return r_olMaps["map_r"];
	if(div_map == "map_cr") return r_olMaps["map_cr"];
	if(div_map == "map_t") return t_olMaps["map_t"];
	if(div_map == "map_ct") return t_olMaps["map_ct"];
	return null;
}

//set fusion result usage handler
var useFusionResult = false;
document.getElementById('form:f_useFusionResult').addEventListener("click", function(){
	value = document.getElementById('form:f_useFusionResult').getElementsByTagName('span')[0].innerHTML;
	if(value == 'Yes') useFusionResult = true;
	else useFusionResult = false;
	f_setCompareInteraction();
});

//set interaction based on fusion result flag
function f_setCompareInteraction(){
	if(r_resource == null || t_resource == null || r_olMaps["map_cr"] == null || t_olMaps["map_ct"] == null)
		return;
	r_olMaps["map_cr"].setCompareInteraction(useFusionResult, r_olMaps["map_cr"], t_olMaps["map_ct"], true);
	t_olMaps["map_ct"].setCompareInteraction(useFusionResult, t_olMaps["map_ct"], r_olMaps["map_cr"], false);
}
