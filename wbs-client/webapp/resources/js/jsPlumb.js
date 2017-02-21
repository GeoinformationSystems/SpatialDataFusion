/**
 * string for identifier separation and replacements
 */
var ioSeparator = '---';
var spaceReplacement = '§§§';
var dotReplacement = '___';

/**
 * style definitions
 */
var style_single_center = "left:40%;top:10%";
var style_output = "right:0;top:10%";
var style_input = "left:0;";

/**
 * jsPlumb object identifier
 */
var singlePlumb = 'single';
var multiPlumb = 'multi';
var activePlumb;

/**
 * jsPlumb object array
 */
var plumb = {};
plumb[singlePlumb] = {};
plumb[multiPlumb] = {};

/**
 * connection update functions
 */
var updateConnectionFunctions = {};
updateConnectionFunctions[singlePlumb] = pf_updateConnections_single;
//updateConnectionFunctions[multiPlumb] = pf_updateConnections_multi;

/**
 * initialize jsPlumb object properties
 */
for (var key in plumb) {
    //set array for active processes
    plumb[key].activeProcesses = []
    //set divs
    plumb[key].div_main = "plumb_main_" + key;
    plumb[key].div_desc = "plumb_desc_" + key;
    //set connections
    plumb[key].connections = [];
    //set plumb key
    plumb[key].key = key;
}

/**
 * set active plumb object
 * @param plumb active plumb identifier
 */
function f_setActivePlumb(identifier) {
    activePlumb = identifier;
}

/**
 * initialize jsPlumb environment
 */
jsPlumb.ready(function () {
    for (var key in plumb) {
        f_initPlumb(plumb[key]);
    }
});

/**
 * initialize jsPlumb environment
 * @param plumb jsPlumb object
 */
function f_initPlumb(plumb) {
    plumb.jsPlumb = jsPlumb.getInstance({
        DragOptions: {cursor: 'pointer', zIndex: 2000},
        Endpoint: ["Dot", {radius: 10}],
        ConnectionOverlays: [
            ["Arrow", {
                location: 1,
                id: "arrow",
                width: 10,
                length: 15,
                foldback: 1
            }]
        ],
        Container: plumb.div_main
    });
    plumb.jsPlumb.bind("click", function (c) {
        plumb.jsPlumb.detach(c);
        f_updateConnections();
    });
    plumb.jsPlumb.bind("connection", function (info, originalEvent) {
        f_updateConnections();
    });
}

/**
 * clear active plumb
 */
function f_clearPlumb() {
    plumb[activePlumb].jsPlumb.detachEveryConnection();
    plumb[activePlumb].jsPlumb.deleteEveryEndpoint();
    for (var i = 0; i < plumb[activePlumb].activeProcesses.length; i++) {
        f_removeProcess(plumb[activePlumb].activeProcesses[i])
    }
    plumb[activePlumb].connections.length = 0;
}

/**
 * remove a process from plumb
 * @param description process description
 */
function f_removeProcess(description) {
    plumb[activePlumb].jsPlumb.remove(plumb[activePlumb].jsPlumb.getSelector("#" + description.identifier));
}

/**
 * add a process element
 * @param description process description
 * @param style element style
 */
function f_addProcess(description, style) {
    //normalize decsription
    description = f_normalizeDescription(description);
    //add to active processes
    plumb[activePlumb].activeProcesses.push(description);
    //append div
    var div = f_createProcessDiv(description, style);
    $('#' + plumb[activePlumb].div_main).append(div);
    //init draggable jsPlumb process
    plumb[activePlumb].jsPlumb.draggable(div);
    //add inputs
    for (var i = 0; i < description.inputs.length; i++) {
        f_addProcessIO(description.identifier, description.inputs[i], true);
    }
    //add outputs
    for (var i = 0; i < description.outputs.length; i++) {
        f_addProcessIO(description.identifier, description.outputs[i], false);
    }
}

/**
 * normalize process description
 * @param description input description
 * @returns {void|string|XML|*} normalized description
 */
function f_normalizeDescription(description) {
    description.identifier = f_normalizeIdentifier(description.identifier);
    for (var i = 0; i < description.inputs.length; i++) {
        description.inputs[i].identifier = f_normalizeIdentifier(description.inputs[i].identifier);
    }
    for (var i = 0; i < description.outputs.length; i++) {
        description.outputs[i].identifier = f_normalizeIdentifier(description.outputs[i].identifier);
    }
    return description;
}

/**
 * normalize identifier
 * @param identifier input identifier
 * @returns {void|string|XML|*} normalized identifier
 */
function f_normalizeIdentifier(identifier) {
    return identifier.replace(/\./g, dotReplacement).replace(/ /g, spaceReplacement);
}

/**
 * get process div
 * @param description process description
 * @param style process style
 * @returns div element
 */
function f_createProcessDiv(description, style) {
    return $('<div>')
        .attr('id', description.identifier)
        .attr('style', style)
        .append('<div class="plumb_process_title">' + description.title + '</div>')
        .addClass('plumb_process')
}

/**
 * add process IO to jsPlumb process
 * @param identifier identifier for process
 * @param ioDescription IO description
 * @param isInput true if IO description is input
 */
function f_addProcessIO(identifier, ioDescription, isInput) {
    var processIOId = identifier + ioSeparator + ioDescription.identifier;
    var processIO = $('<div>').attr('id', processIOId).addClass('plumb_process_io').addClass((isInput ? 'plumb_process_in' : 'plumb_process_out'));
    processIO.text(ioDescription.title);
    processIO.mouseover(function () {
        $('#' + plumb[activePlumb].div_desc).html(f_getIODetails(ioDescription));
    });
    processIO.mouseleave(function () {
        $('#' + plumb[activePlumb].div_desc).html('');
    });
    $('#' + identifier).append(processIO);
    var io = plumb[activePlumb].jsPlumb.getSelector("#" + processIOId);
    if (isInput) {
        plumb[activePlumb].jsPlumb.makeTarget(io, {
            anchor: "LeftMiddle",
            maxConnections: 1
        });
    }
    else {
        plumb[activePlumb].jsPlumb.makeSource(io, {
            anchor: "RightMiddle",
            connector: ["Flowchart", {stub: 30, cornerRadius: 5, gap: 10}]
        });
    }
}

/**
 * get WPS IO details (for mouseover event)
 * @param ioDescription
 * @returns {String}
 */
function f_getIODetails(ioDescription) {
    var details = '<span class="io_id">' + ioDescription.identifier + '</span><br /><span class="io_title">' + ioDescription.title + '</span><br />' +
        '<span class="io_format_title">defaultFormat</span><br />' + f_getFormatString(ioDescription.defaultFormat) +
        '<span class="io_format_title">supportedFormats</span><br />';
    for (var i = 0; i < ioDescription.supportedFormats.length; i++) {
        details += f_getFormatString(ioDescription.supportedFormats[i]);
        if (i >= 5) {
            details += '<div class="ioFormat">... (' +
                (ioDescription.supportedFormats.length - 5) + ' more)</div>';
            break;
        }
    }
    return details;
}

/**
 * get formatted string for WPS io
 * @param format io format
 * @returns {String} formatted string
 */
function f_getFormatString(format) {
    var string = '<div class="io_format">';
    if (typeof format === 'undefined')
        string += 'no format defined<br />';
    else
        string += (typeof format.mimetype !== 'undefined' && format.mimetype.length > 0 ? 'mimetype: ' + format.mimetype + '<br />' : '') +
            (typeof format.schema !== 'undefined' && format.schema.length > 0 ? 'schema: ' + format.schema + '<br />' : '') +
            (typeof format.type !== 'undefined' && format.type.length > 0 ? 'type: ' + format.type + '<br />' : '');
    if (string.lastIndexOf('<br />') > -1)
        string = string.substring(0, string.lastIndexOf('<br />'));
    return string + '</div>';
}

/**
 * initialize single plumb
 * @param processDescription input process description
 */
function f_initSinglePlumb(sProcessDescription) {
    var processDescription = f_getProcessDescriptionFromJSONString(sProcessDescription);
    if (!isValidDescription(processDescription))
        return;
    f_setActivePlumb(singlePlumb);
    f_addProcess(processDescription, style_single_center);
    //add inputs
    var inputDescriptions = f_getInputDescriptions();
    for (var i = 0; i < inputDescriptions.length; i++) {
        f_addProcess(inputDescriptions[i], style_input + "top:" + (i * 20 + 10) + "%;");
    }
    //add output
    f_addProcess(f_getOutputDescription(), style_output);
}

/**
 * parse process description from JSON
 * @param sProcessDescription JSON string
 */
function f_getProcessDescriptionFromJSONString(sProcessDescription) {
    return JSON.parse(sProcessDescription);
}

/**
 * check if process description is valid
 * @param processDescription input description
 */
function isValidDescription(processDescription) {
    return processDescription != null && processDescription.identifier !== 'undefined';
}

/**
 * clear single plumb
 */
function f_clearSinglePlumb() {
    f_setActivePlumb(singlePlumb);
    f_clearPlumb();
}

/**
 * get selected process inputs from openlayers
 * @returns input description array
 */
function f_getInputDescriptions() {
    var inputDescriptions = [];
    //uses reference to olMap object
    for (var i = 0; i < olMap.selectedLayers.length; i++) {
        sLayer = olMap.selectedLayers[i];
        var selection = olMap.f_getSelectedFeaturesByLayer(sLayer);
        inputDescriptions.push(f_getInputDescription(sLayer, selection));
    }
    return inputDescriptions;
}

/**
 * get input description for layer
 * @returns input description
 */
function f_getInputDescription(sLayer, selection) {
    var description = {
        title: sLayer, identifier: sLayer,
        inputs: []
    };
    description.outputs = [];
    if (selection.length > 0) {
        for (var i = 0; i < selection.length; i++) {
            description.outputs.push({
                identifier: selection[i].getId(), title: selection[i].getId(),
                defaultFormat: {mimetype: 'application/json'},
                supportedFormats: [{mimetype: 'application/json'}]
            });
        }
    }
    else
        description.outputs.push({
            identifier: 'ALL_features', title: 'all features',
            defaultFormat: {mimetype: 'application/json'},
            supportedFormats: [{mimetype: 'application/json'}]
        });
    return description;
}

/**
 * get default process output description
 * @returns output description
 */
function f_getOutputDescription() {
    return {
        title: 'Output', identifier: 'Output',
        inputs: [{
            identifier: 'IN_OUTPUT', title: 'Output of the process',
            defaultFormat: {mimetype: 'any'},
            supportedFormats: [{mimetype: 'any'}]
        }],
        outputs: []
    };
}

/**
 * update jsPlumbConnections in JSF
 */
function f_updateConnections() {
    plumb[activePlumb].connections = f_getConnections();
    updateConnectionFunctions[plumb[activePlumb].key](JSON.stringify(plumb[activePlumb].connections));
}

/**
 * get jsPlumb connections
 */
function f_getConnections() {
    var connections = [];
    var plumbConnections = plumb[activePlumb].jsPlumb.getAllConnections();
    for (var i = 0; i < plumbConnections.length; i++) {
        var connection = {};
        //split identifier
        var source = plumbConnections[i].sourceId.split(ioSeparator);
        var target = plumbConnections[i].targetId.split(ioSeparator);
        //get descriptions
        var sourceProcess = f_getProcessById(source[0]);
        var targetProcess = f_getProcessById(target[0]);
        //set connection
        connection['s_identifier'] = sourceProcess;
        connection['t_identifier'] = targetProcess;
        connection['s_output'] = source[1];
        connection['t_input'] = target[1];
        connections.push(connection);
    }
    return connections;
}

/**
 * get process description by identifier
 */
function f_getProcessById(identifier) {
    for (var i = 0; i < plumb[activePlumb].activeProcesses.length; i++) {
        if (plumb[activePlumb].activeProcesses[i].identifier === identifier)
            return plumb[activePlumb].activeProcesses[i];
    }
    return null;
}



