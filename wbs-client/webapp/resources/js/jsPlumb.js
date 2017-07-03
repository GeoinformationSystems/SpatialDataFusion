/**
 * string for identifier separation
 */
var ioSeparator = '---';

/**
 * style definitions
 */
var style_single_center = "left:30%;top:10%";
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
 * input descriptions (selected features for each selected layer)
 */
var inputDescriptions;

/**
 * connection update functions
 */
var updateConnectionFunctions = {};
updateConnectionFunctions[singlePlumb] = pf_updateWorkflow_single;
//updateConnectionFunctions[multiPlumb] = pf_updateWorkflow_multi;

/**
 * initialize jsPlumb object properties
 */
for (var key in plumb) {
    //set array for active processes
    plumb[key].activeProcesses = []
    //set divs
    plumb[key].div_main = "plumb_main_" + key;
    plumb[key].div_desc = "plumb_desc_" + key;
    plumb[key].div_desc_txt = "plumb_desc_text_" + key;
    plumb[key].div_validation = "plumb_validation_" + key;
    //set connections
    plumb[key].workflow = {};
    //set plumb key
    plumb[key].key = key;
    //set update workflow function name
    plumb[key].updateConnections = updateConnectionFunctions[key];
}

/**
 * set active plumb object
 * @param identifier active plumb identifier
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
        f_updateWorkflow();
    });
    plumb.jsPlumb.bind("connection", function (info, originalEvent) {
        f_updateWorkflow();
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
    plumb[activePlumb].workflow = {};
}

/**
 * remove a process from plumb
 * @param description process description
 */
function f_removeProcess(description) {
    plumb[activePlumb].jsPlumb.remove($(document.getElementById(description.identifier_norm)));
}

/**
 * add a process element
 * @param description process description
 * @param style element style
 */
function f_addProcess(description, style) {
    //normalize description
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
        f_addProcessIO(description.identifier_norm, description.inputs[i], true);
    }
    //add outputs
    for (i = 0; i < description.outputs.length; i++) {
        f_addProcessIO(description.identifier_norm, description.outputs[i], false);
    }
}

/**
 * normalize process description
 * @param description input description
 * @returns {void|string|XML|*} normalized description
 */
function f_normalizeDescription(description) {
    description.identifier_norm = f_encodeIdentifier(description.identifier);
    description.title_norm = f_encodeTitle(description.title);
    return description;
}

/**
 * encode identifier
 * @param identifier input identifier
 * @returns {string} encoded identifier
 */
function f_encodeIdentifier(identifier) {
    //replace special characters
    return identifier.replace(/(:|\.|\[|\]|,|=|@|\/)/g, "\\\\$1");
}

/**
 * decode identifier
 * @param identifier input identifier
 * @returns {string} decoded identifier
 */
function f_decodeIdentifier(identifier) {
    //undo replacements from f_encodeIdentifier(identifier)
    return identifier.replace(/(\\\\)/g, "");
}

/**
 * encode title
 * @param title input title
 * @returns {string} encoded title
 */
function f_encodeTitle(title) {
    return title;
}

/**
 * get process div
 * @param description process description
 * @param style process style
 * @returns div element
 */
function f_createProcessDiv(description, style) {
    return $('<div>')
        .attr('id', description.identifier_norm)
        .attr('style', style)
        .append('<div class="plumb_process_title">' + description.title_norm + '</div>')
        .addClass('plumb_process')
}

/**
 * add process IO to jsPlumb process
 * @param identifier_norm identifier for process
 * @param ioDescription IO description
 * @param isInput true if IO description is input
 */
function f_addProcessIO(identifier_norm, ioDescription, isInput) {
    ioDescription = f_normalizeDescription(ioDescription);
    var processIOId = identifier_norm + ioSeparator + ioDescription.identifier_norm;
    var processIO = $('<div>').attr('id', processIOId).addClass('plumb_process_io').addClass((isInput ? 'plumb_process_in' : 'plumb_process_out'));
    processIO.text(ioDescription.title);
    processIO.mouseover(function () {
        plumb[activePlumb].desc_txt.html(f_getIODetails(ioDescription));
        plumb[activePlumb].desc.css("display", "block");
        plumb[activePlumb].desc.css("position", "fixed");
        var rightX = processIO.offset().left + processIO.width();
        var bottomY = processIO.offset().top + processIO.height();
        plumb[activePlumb].desc.css("left", rightX - 50 + "px");
        plumb[activePlumb].desc.css("top", bottomY + 5 + "px");
    });
    processIO.mouseleave(function () {
        plumb[activePlumb].desc_txt.html('');
        plumb[activePlumb].desc.css("display", "none");
        plumb[activePlumb].desc.css("position", "relative");
    });

    $(document.getElementById(identifier_norm)).append(processIO);
    if (isInput) {
        plumb[activePlumb].jsPlumb.makeTarget(processIO, {
            anchor: "LeftMiddle",
            maxConnections: 1
        });
    }
    else {
        plumb[activePlumb].jsPlumb.makeSource(processIO, {
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
    var details = '<div class="io_description">' + ioDescription.description + '</div>' +
        '<span class="io_format_title">defaultFormat</span><br />' + f_getFormatString(ioDescription.defaultFormat) +
        '<span class="io_format_title">supportedFormats</span><br />';
    for (var i = 0; i < ioDescription.supportedFormats.length; i++) {
        if (i >= 3) {
            details += '<div class="io_format">... (' +
                (ioDescription.supportedFormats.length - i) + ' more)</div>';
            break;
        }
        details += f_getFormatString(ioDescription.supportedFormats[i]);
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
        string +=
            f_getFormatSubString('mimetype', format.mimetype) +
            f_getFormatSubString('schema', format.schema) +
            f_getFormatSubString('type', format.type);
    if (string.lastIndexOf('<br />') > -1)
        string = string.substring(0, string.lastIndexOf('<br />'));
    return string + '</div>';
}

function f_getFormatSubString(key, value) {
    if (typeof value === 'undefined' || value === null || value.length < 1)
        return '';
    else
        return key + ": " + value + '<br />';
}

/**
 * initialize single plumb
 * @param jsonProcessDescription input process description
 */
function f_initSinglePlumb(jsonProcessDescription) {
    var processDescription = JSON.parse(jsonProcessDescription);
    if (!isValidDescription(processDescription))
        return;
    f_setActivePlumb(singlePlumb);
    //set relevant div elements
    f_resetPlumb();
    //add inputs
    for (var i = 0; i < inputDescriptions.length; i++) {
        f_addProcess(inputDescriptions[i], style_input + "top:" + (i * 20 + 10) + "%;");
    }
    //add main process
    f_addProcess(processDescription, style_single_center);
    //add output
    f_addProcess(f_getOutputDescription(), style_output);
    f_updateWorkflow();
}

/**
 * set active div elements
 */
function f_resetPlumb() {
    f_initPlumb(plumb[activePlumb]);
    plumb[activePlumb].main = $(document.getElementById(plumb[activePlumb].div_main));
    plumb[activePlumb].validation = $(document.getElementById(plumb[activePlumb].div_validation));
    plumb[activePlumb].desc = $(document.getElementById(plumb[activePlumb].div_desc));
    plumb[activePlumb].desc_txt = $(document.getElementById(plumb[activePlumb].div_desc_txt));
}

/**
 * check if process description is valid
 * @param processDescription input description
 */
function isValidDescription(processDescription) {
    return processDescription !== null && processDescription.identifier !== 'undefined' && processDescription.title !== 'undefined';
}

/**
 * clear single plumb
 */
function f_clearSinglePlumb() {
    f_setActivePlumb(singlePlumb);
    f_clearPlumb();
}

/**
 * dd literal value
 * @param value literal
 */
function f_addLiteral(value) {
    value = value.replace(',', '.')
    if (value.length === 0)
        return;
    f_addLiteralInput(value);
}

/**
 * add literal value as input process
 * @param value literal
 */
function f_addLiteralInput(value) {
    if ($('#LiteralInputs').length === 0)
        f_addProcess(f_getLiteralInputDescription(), style_input + "bottom:0%;");
    var description = f_getLiteralValueDescription(value, f_getProcessById('LiteralInputs').outputs.length + 1);
    f_getProcessById('LiteralInputs').outputs.push(description);
    f_addProcessIO('LiteralInputs', description, false);
}

/**
 * get literal input process description
 * @returns input process description
 */
function f_getLiteralInputDescription() {
    return {
        title: 'LiteralInputs', identifier: 'LiteralInputs',
        inputs: [],
        outputs: []
    };
}

/**
 * get literal value description
 * @param value literal
 * @param index literal index for identification
 * @returns {{identifier: *, minOccurs: *, maxOccurs: *, title: *, defaultFormat: *, supportedFormats: *}} value description
 */
function f_getLiteralValueDescription(value, index) {
    return f_getProcessIODescription(
        "Literal_" + index, 1, 1, value, "Literal value: " + value,
        f_getIOFormat(null, null, "xs:string"),
        f_getSupportedFormats(value)
    )
}

/**
 * get supported io types for literal value
 * @param value literal
 * @return {[*]} formats
 */
function f_getSupportedFormats(value) {
    var supportedFormats = [f_getIOFormat(null, null, "xs:string")];
    if (/^(true|false|0|1)$/i.test(value))
        supportedFormats.push(f_getIOFormat(null, null, "xs:boolean"));
    if (/^\d+$/.test(value))
        supportedFormats.push(f_getIOFormat(null, null, "xs:integer"));
    if (/^\d+\.?\d*$/.test(value))
        supportedFormats.push(f_getIOFormat(null, null, "xs:double"));
    return supportedFormats;
}

/**
 * get default process output description
 * @returns output description
 */
function f_getOutputDescription() {
    return f_getProcessDescription(
        'Output', 'Output',
        [
            f_getProcessIODescription(
                'Output', 1, 100, 'Output', 'Output of the Workflow',
                f_getIOFormat('*', '*', '*'),
                [
                    f_getIOFormat('*', '*', '*')
                ]
            )
        ],
        [],
        'output'
    );
}

/**
 * update jsPlumbConnections in JSF
 */
function f_updateWorkflow() {
    plumb[activePlumb].workflow = f_getWorkflowDescription();
    plumb[activePlumb].updateConnections(JSON.stringify(plumb[activePlumb].workflow));
}

/**
 * set validation message
 * @param message message string
 */
function f_setValidationMessage(message) {
    plumb[activePlumb].validation.html(message);
}

/**
 * get jsPlumb connections
 * @returns {Array}
 */
function f_getWorkflowDescription() {
    var workflow = {};
    workflow.processes = plumb[activePlumb].activeProcesses;
    workflow.connections = [];
    var plumbConnections = plumb[activePlumb].jsPlumb.getAllConnections();
    for (var i = 0; i < plumbConnections.length; i++) {
        var connection = {};
        //split identifier
        var source = plumbConnections[i].sourceId.split(ioSeparator);
        var target = plumbConnections[i].targetId.split(ioSeparator);
        //set connection
        connection['s_identifier'] = f_decodeIdentifier(source[0]);
        connection['t_identifier'] = f_decodeIdentifier(target[0]);
        connection['s_output'] = f_decodeIdentifier(source[1]);
        connection['t_input'] = f_decodeIdentifier(target[1]);
        workflow.connections.push(connection);
    }
    return workflow;
}

/**
 * get process description by identifier
 * @param identifier process identifier
 * @returns {*} process or null, if process does not exist
 */
function f_getProcessById(identifier) {
    for (var i = 0; i < plumb[activePlumb].activeProcesses.length; i++) {
        if (plumb[activePlumb].activeProcesses[i].identifier === identifier)
            return plumb[activePlumb].activeProcesses[i];
    }
    return null;
}

/**
 * get process description
 * @param identifier process identifier
 * @param title process title
 * @param inputs process inputs
 * @param outputs process outputs
 * @returns {{title: *, identifier: *, inputs: *, outputs: *}} process description
 */
function f_getProcessDescription(identifier, title, inputs, outputs, type) {
    return {
        title: title,
        identifier: identifier,
        inputs: inputs,
        outputs: outputs,
        type: type
    };
}

/**
 * get process IO description
 * @param identifier IO identifier
 * @param minOccurs IO min occurs
 * @param maxOccurs IO max occurs
 * @param title IO title
 * @param description IO description
 * @param defaultFormat IO default format
 * @param supportedFormats IO supported formats
 * @return {{identifier: *, minOccurs: *, maxOccurs: *, title: *, defaultFormat: *, supportedFormats: *}} IO description
 */
function f_getProcessIODescription(identifier, minOccurs, maxOccurs, title, description, defaultFormat, supportedFormats) {
    return {
        identifier: identifier,
        minOccurs: minOccurs, maxOccurs: maxOccurs,
        title: title,
        description: description,
        defaultFormat: defaultFormat,
        supportedFormats: supportedFormats
    }
}

/**
 * get IO format description
 * @param mimetype IO mimetype
 * @param schema IO schema
 * @param type IO type
 * @returns {{mimetype: *, schema: *, type: *}} format description
 */
function f_getIOFormat(mimetype, schema, type) {
    return {
        mimetype: mimetype,
        schema: schema,
        type: type
    }
}

/**
 * set selected features by layer
 * @param selection input selection
 */
function f_setInputDescriptions(descriptions) {
    inputDescriptions = JSON.parse(descriptions);
}



