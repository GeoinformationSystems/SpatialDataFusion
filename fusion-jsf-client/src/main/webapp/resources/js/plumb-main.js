/**
 * main jsPlumb instance
 */
var jsPlumbInstance;

/**
 * active processes used for orchestration
 */
var activeProcesses = {};

/**
 * string for identifier separation
 */
var ioSeparator = '---';

/**
 * remove process description
 * @param identifier process identifier
 */
function f_removeProcess(identifier){
	if(identifier.length == 0)
		return;
	//remove process
	delete activeProcesses[identifier];
	f_removeProcessInstance(f_getNormIdentifier(identifier));	
}

/**
 * reset jsPlumb
 */
function f_reset() {
	jsPlumbInstance.deleteEveryEndpoint();
	var identifiers = [];
	for(var process in activeProcesses){
		identifiers.push(process);
	}
	for(var i=0; i<identifiers.length; i++) {
		f_removeProcess(identifiers[i]);
	}
	//uncheck all processes
	var selection = PrimeFaces.widgets.p_selectedProcesses;
	selection.inputs.each(function() {
		if($(this).prop('checked') === true)
			$(this).trigger('click');
	});
}

/**
 * parse and add process description
 * @param descriptionAsJSON
 */
function f_addProcessfromJSON(descriptionAsJSON){
	if(descriptionAsJSON.length == 0)
		return;
	f_addProcess(JSON.parse(descriptionAsJSON));	
}

/**
 * add process description
 * @param decsription
 */
function f_addProcess(description){
	//set id properties
	description.identifier_norm = f_getNormIdentifier(description.identifier);
	description.identifier_short = f_getShortIdentifier(description.identifier);
	//add process
	connectionsValid = false;
	activeProcesses[description.identifier] = description;
	f_addProcessInstance(description);
}

/**
 * get shortened identifier
 * @param id input identifier
 * @returns shortened identifier (substring after last occurrence of '.' or '/')
 */
function f_getShortIdentifier(id){
	if(id.indexOf(".") > -1)
		return id.substring(id.lastIndexOf(".") + 1, id.length);
	if(id.indexOf("/") > -1)
		return id.substring(id.lastIndexOf("/") + 1, id.length);
	return id;
}

/**
 * get normalized identifier
 * @param id input identifier
 * @returns normalized identifier (removes '.' character)
 */
function f_getNormIdentifier(id){
	return id.replace(new RegExp('\\.', 'g'), '');
}

/**
 * init jsPlumb environment
 */
jsPlumb.ready(function() {	
	jsPlumbInstance = jsPlumb.getInstance({
		DragOptions : { cursor: 'pointer', zIndex:2000 },
		Endpoint: "Dot",
		EndpointStyle : {radius:2, fillStyle:"#000066"},
		EndpointHoverStyle: { fillStyle:"#990000" },
		HoverPaintStyle : {strokeStyle:"#990000", lineWidth:2 },
		ConnectionOverlays : [
			[ "Arrow", {
				location:1,
				id:"arrow",
				width:10,
                length:15
			} ]
		],
		Container: "plumb_processes"
	});	
	jsPlumbInstance.bind("click", function(c) {
		jsPlumbInstance.detach(c);
		f_validateConnections();
	});
	jsPlumbInstance.bind("connection", function(info, originalEvent) {
		f_validateConnections();
	});
});

/**
 * add a process instance
 * @param description process description
 */
function f_addProcessInstance(description){

	var process = $('<div>').attr('id', description.identifier_norm).addClass('plumb_process color_main');
	process.text(description.identifier_short);
	$('#plumb_processes').append(process);
	
	var process = jsPlumbInstance.getSelector("#" + description.identifier_norm);
	
	jsPlumbInstance.draggable(process);
	
	for(var i=0; i<description.inputs.length; i++) {
		f_addProcessIO(description.identifier_norm, description.inputs[i], true);
	}

	for(var i=0; i<description.outputs.length; i++) {
		f_addProcessIO(description.identifier_norm, description.outputs[i], false);
	}

}

/**
 * add a literal field to jsPlumb
 */
function f_addLiteral() {
	//get literal value
	var literal = document.getElementById('form:p_literal').value;
	if(literal.length == 0)
		return;
	//default format = xs:string
	var defaultFormat = 'xs:string';
	//identify supported formats
	var supportedFormats = ['xs:string'];
	if( /^(true|false|0|1)$/i.test(literal) )
		supportedFormats.push("xs:boolean");
	if( /^\d+$/.test(literal) )
		supportedFormats.push("xs:integer");
	if( /^\d+\.?\d+$/.test(literal) )
		supportedFormats.push("xs:double");
	
	f_addLiteralProcess(literal, defaultFormat, supportedFormats);

	//clear input field
	document.getElementById('form:p_literal').value = '';
}

/**
 * add literal input to jsPlumb
 * @param formats
 * @param value
 */
function f_addLiteralProcess(value, defaultFormat, supportedformats){
	var description = {};
	description.identifier = 'Value_' + value.replace('.','_');
	description.title = description.identifier;
	description.inputs = [];
	description.outputs = [];
	//set output
	var output = {};
	output.identifier = 'LITERAL';
	output.title = 'Literal output: ' + value;
	output.defaultFormat = f_createLiteralFormat(defaultFormat);
	output.supportedFormats = [];
	for(var i=0; i<supportedformats.length; i++){
		output.supportedFormats.push(f_createLiteralFormat(supportedformats[i]));
	}
	description.outputs.push(output);
	console.log(description);
	//add process
	f_addProcess(description);
}

/**
 * create literal format type
 * @param format
 * @returns format
 */
function f_createLiteralFormat(sFormat){
	var format = {};
	format.type = sFormat;
	return format;
}

/**
 * remove a process instance
 * @param id_norm normalized process identifier
 */
function f_removeProcessInstance(id_norm){
	var process = jsPlumbInstance.getSelector("#" + id_norm);
	jsPlumbInstance.remove(process);
}

/**
 * add process IO to jsPlumb process
 * @param identifier_norm normalized identifier for process
 * @param ioDescription IO description
 * @param input true if IO description is input
 */
function f_addProcessIO(identifier_norm, ioDescription, input){
	var processIOId = identifier_norm + ioSeparator + ioDescription.identifier;
	var processIO = $('<div>').attr('id', processIOId).addClass((input ? 'plumb_process_in color_subsub_border' : 'plumb_process_out color_subsub_border'));
	processIO.text(ioDescription.identifier);
	processIO.mouseover(function() {
		$('#plumb_desc').html(f_getIODetails(ioDescription));
	});
//	processIO.mouseleave(function() {
//		$('#plumb_desc').html('');
//	});
	$('#' + identifier_norm).append(processIO);
	
	var io = jsPlumb.getSelector("#" + processIOId);
		
	if(input) {
		jsPlumbInstance.makeTarget(io, {
			dropOptions:{ hoverClass:"dragHover" },
			anchor:"LeftMiddle",
			maxConnections:1
		});
	}
	else {
		jsPlumbInstance.makeSource(io, {
			anchor:"RightMiddle",
			connector:"Bezier",
			connectorStyle:{strokeStyle:"#000066", lineWidth:2, outlineColor:"transparent", outlineWidth:4 }
		});
	}
}

/**
 * get WPS IO details (for mouseover event)
 * @param ioDescription
 * @returns {String}
 */
function f_getIODetails(ioDescription){
	var details = ioDescription.identifier + '<br />' + ioDescription.title + '<br />' +
		'defaultFormat<br />' + f_getFormatString(ioDescription.defaultFormat) +
		'supportedFormats<br />';
	for(var i=0; i<ioDescription.supportedFormats.length; i++) {
		details += f_getFormatString(ioDescription.supportedFormats[i]);
	}
	return details;
}

/**
 * get formatted string for WPS io
 * @param format io format
 * @returns {String} formatted string
 */
function f_getFormatString(format){
	var string = '<div class="ioFormat color_txt_sub">';
	if(typeof format === 'undefined')
		string += 'no format defined<br />';
	else
		string += (typeof format.mimetype !== 'undefined' && format.mimetype.length > 0 ? 'mimetype: ' + format.mimetype + '<br />' : '') + 
			(typeof format.schema !== 'undefined' && format.schema.length > 0 ? 'schema: ' + format.mimetype + '<br />' : '') + 
			(typeof format.type !== 'undefined' && format.type.length > 0 ? 'type: ' + format.type + '<br />' : '');
	if(string.lastIndexOf('<br />') > -1)
		string = string.substring(0, string.lastIndexOf('<br />'));
	return string + '</div>';
}

/**
 * set connections valid, used for enabling execute button
 * @param flag
 */
function setConnectionsValid(flag){
	if(flag === true)
		PrimeFaces.widgets.p_execute_button.enable();
	else
		PrimeFaces.widgets.p_execute_button.disable();
}

/**
 * validate connections
 */
function f_validateConnections() {
	var connectionsValid = true;
	var errors = "";
	var connections = f_getConnections();
	if(connections.length == 0){
		setConnectionsValid(false);
		document.getElementById('p_validationResult').innerHTML = '';
		document.getElementById('form:p_connections').value = '';
		return;
	}
	//check connections
	for(var i=0; i<connections.length; i++) {
		var validationError = f_validateConnection(connections[i]);
		if(validationError !== null){
			connectionsValid = false;
			errors += validationError + '<br />';
		}
	}
	//set connections input field
	if(connectionsValid) {
		setConnectionsValid(true);
		document.getElementById('p_validationResult').innerHTML = '<span class="good">Connections are valid</span>';
		document.getElementById('form:p_connections').value = JSON.stringify(f_getConnections());
	}
	else {
		setConnectionsValid(false);
		document.getElementById('p_validationResult').innerHTML = '<span class="bad">Connections are not valid</span><br />' + errors;
		document.getElementById('form:p_connections').value = '';
	}
}

/**
 * validate connection
 * @param connection input connection
 */
function f_validateConnection(connection) {
	
	//get output list
	var outputs = null;
	for(var i=0; i<connection['ref_description'].outputs.length; i++) {
		if(connection['ref_description'].outputs[i].identifier == connection['ref_output'])
			outputs = connection['ref_description'].outputs[i].supportedFormats;
	}
	//get input list
	var inputs = null;
	for(i=0; i<connection['tar_description'].inputs.length; i++) {
		if(connection['tar_description'].inputs[i].identifier == connection['tar_input'])
			inputs = connection['tar_description'].inputs[i].supportedFormats;
	}
	//check if inputs and outputs are set	
	if(typeof outputs === null || typeof inputs === null)
		return 'inputs or outputs are not set properly for ' + connection['ref_output'] + ' --> ' + connection['tar_input'];
	else if(!f_haveCommonFormat(inputs, outputs))
		return 'inputs and outputs have no common format ' + connection['ref_output'] + ' --> ' + connection['tar_input'];
	else
		return null;
}

/**
 * check two lists for common entry
 * @param inputs first list
 * @param outputs second list
 */
function f_haveCommonFormat(inputs, outputs){
	for(var i=0; i<inputs.length; i++){
		for(var j=0; j<outputs.length; j++){
			if(f_compareFormats(inputs[i], outputs[j]))
				return true;
		}
	}
	return false;
}

/**
 * compare two formats, returns 0 if equal
 * @param input first format
 * @param output second format
 */
function f_compareFormats(input, output){
	
	if(typeof input.mimetype !== 'undefined' || typeof output.mimetype !== 'undefined')
		if(input.mimetype !== output.mimetype)
			return false;
	if(typeof input.schema !== 'undefined' || typeof output.schema !== 'undefined')
		if(input.schema !== output.schema)
			return false;
	if(typeof input.type !== 'undefined' || typeof output.type !== 'undefined')
		if(input.type !== output.type)
			return false;		
	
	return true;
}

/**
 * get jsPlumb connections
 */
function f_getConnections() {
	var connections = [];
	//get all connections
	var plumbConnections = jsPlumbInstance.getAllConnections();
	for(var i=0; i<plumbConnections.length; i++) {
		var connection = {};
		//split identifier
		var reference = plumbConnections[i].sourceId.split(ioSeparator);
		var target = plumbConnections[i].targetId.split(ioSeparator);
		//get descriptions
		var referenceDescription = f_getDescriptionForNormIdentifier(reference[0]);
		var targetDescription = f_getDescriptionForNormIdentifier(target[0]);
		//set connection
		connection['ref_description'] = referenceDescription;
		connection['tar_description'] = targetDescription;
		connection['ref_output'] = reference[1];
		connection['tar_input'] = target[1];
		connections.push(connection);
	}
	return connections;
}

/**
 * get description from active processes that matches provided normalized identifier
 */
function f_getDescriptionForNormIdentifier(identifier_norm){
	for(var identifier in activeProcesses){
		if(activeProcesses[identifier].identifier_norm === identifier_norm)
			return activeProcesses[identifier];
	}
	return null;
}