//SPARQL endpoint
var sparql_endpoint = "http://localhost:3030/fusion/sparql?&output=json&query=";

//default prefixes
var prefixes = {};
prefixes["rdf"] = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
prefixes["xsd"] = "http://www.w3.org/TR/xmlschema11-2#";
prefixes["fusion"] = "http://tu-dresden.de/uw/geo/gis/fusion#";
prefixes["process"] = "http://tu-dresden.de/uw/geo/gis/fusion/process#";
prefixes["measurement"] = "http://tu-dresden.de/uw/geo/gis/measurement/";
prefixes["fn"] = "http://www.w3.org/2005/xpath-functions#";

//function: get SPARQL PREFIX definition
var f_getSPARQLPrefixes = function(){
	var request = "";
	for(var prefix in prefixes){
		request += "PREFIX " + prefix + ": <" + prefixes[prefix] + "> ";
	}
	return request;
};

//function: GET request with provided URL
var f_getSPARQLResponse = function(url){
	//get xmlHTTP object
	if (window.XMLHttpRequest) xmlhttp=new XMLHttpRequest();
	else xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	//open connection
	xmlhttp.open("GET", url, false);
	xmlhttp.send();
	return xmlhttp.responseText;
};

//get SPARQL request url
var f_getSPARQLUrl = function(request){
	return sparql_endpoint + request;
};

//function: get SPARQL request
var f_getSPARQLFeatureRequest = function(featureId, isReference){
	var request = 
		f_getSPARQLPrefixes() +
		"SELECT ?id ?relationType ?relationValue " +
		"WHERE {" +
			"?relation rdf:type fusion:featureRelation . " +
			"?relation fusion:" + (isReference ? "relationHasReference" : "relationHasTarget") + " ?featureURI . " +
			"FILTER fn:ends-with(str(?featureURI), \"" + featureId + "\") . " +
			"?relation fusion:" + (isReference ? "relationHasTarget " : "relationHasReference ") + "?id . " +
			"?relation fusion:relationHasRelationMeasurement ?relationMeasure . " +
			"?relationMeasure fusion:measurementHasDescription ?relationType . " +
			"?relationMeasure rdf:value ?relationValue" +
		"}"; +
		"ORDER BY ?id";
	return f_getSPARQLUrl(encodeURIComponent(request));
};

//function: parse json response 
var f_parseResponse = function(response){
	//use built-in JSON parser
	var jsonObj = JSON.parse(response);
	//get relations
	var jsonResults = jsonObj.results.bindings;
	
	//build result array
	relatedFeatures = [];
	relation = [];
	relation.relationMeasures = [];
	for(var i in jsonResults){
		//init new relation, if id changed
		if(relation.id && relation.id != jsonResults[i].id.value){
			relatedFeatures.push({
				"featureId": relation.id,
				"relation": relation
			});
			relation = [];
			relation.relationMeasures = [];
		}
		//set id if not set
		if(!relation.id) {
			relation.id = jsonResults[i].id.value;
		}
		//get relation measure
		relation.relationMeasures.push({
			"relationType": jsonResults[i].relationType.value, 
			"relationValue": jsonResults[i].relationValue.value
		});
		//add last element
		if(i == jsonResults.length-1){
			relatedFeatures.push({
				"featureId": relation.id,
				"relation": relation
			});
		}
	}
	return relatedFeatures;
};

//function: get related features from reference array
function f_getRelatedFeatures(featureArray, isReference){
	var result = [];
	for(var feature in featureArray){
		result.push(f_getRelatedFeaturesForFeature(featureArray[feature], isReference));
	}
	return result;
}

//function: get related features from reference feature
function f_getRelatedFeaturesForFeature(feature, isReference){
	var request = f_getSPARQLFeatureRequest(feature.getId(), isReference);
	var response = f_getSPARQLResponse(request);
	return f_parseResponse(response);
}