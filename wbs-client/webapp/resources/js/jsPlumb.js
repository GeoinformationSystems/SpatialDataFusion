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
 * instance for single process executions
 */
var singleInstance;
var singleInstance_div = "plumb_single";
var singleInstance_div_desc = "plumb_single_desc";
var singleInstance_identifier;

/**
 * initialize jsPlumb environment
 */
jsPlumb.ready(function () {
    singleInstance = jsPlumb.getInstance({
        DragOptions : { cursor: 'pointer', zIndex:2000 },
        Endpoint: "Blank",
        HoverPaintStyle : {strokeStyle:"#5192b5", lineWidth:2 },
        ConnectionOverlays : [
            [ "Arrow", {
                location:1,
                id:"arrow",
                width:10,
                length:15,
                foldback:1
            } ]
        ],
        Container: "plumb_single"
    });
});

function f_addSingleProcess(processDescription){
    if(processDescription.length == 0)
        return;
    singleInstance_identifier = processDescription.identifier;
    var style = "left:30%;top:10%";
    f_addProcess(singleInstance_div, singleInstance, JSON.parse(processDescription), singleInstance_div_desc, style);
}

function f_removeSingleProcess() {
    f_removeProcess(singleInstance, singleInstance_identifier);
}

function f_addProcess(instance_div, instance, description, description_div, style){
    f_addProcessInstance(instance_div, instance, description, description_div, style);
}

function f_addProcessInstance(instance_div, instance, description, description_div, style){
    //create new div
    var process = $('<div>').attr('id', description.identifier).attr('style', style).addClass('plumb_process');
    process.text(description.identifier);
    $('#' + instance_div).append(process);
    //init draggable jsPlumb process
    var process = instance.getSelector("#" + description.identifier);
    instance.draggable(process);
    //add inputs
    for(var i=0; i<description.inputs.length; i++) {
        f_addProcessIO(instance, description.identifier, description.inputs[i], description_div, true);
    }
    //add outputs
    for(var i=0; i<description.outputs.length; i++) {
        f_addProcessIO(instance, description.identifier, description.outputs[i], description_div, false);
    }
}

/**
 * add process IO to jsPlumb process
 * @param identifier_norm normalized identifier for process
 * @param ioDescription IO description
 * @param input true if IO description is input
 */
function f_addProcessIO(instance, identifier, ioDescription, description_div, isInput){
    var processIOId = identifier + ioSeparator + ioDescription.identifier;
    var processIO = $('<div>').attr('id', processIOId).addClass('plumb_process_io').addClass((isInput ? 'plumb_process_in' : 'plumb_process_out'));
    processIO.text(ioDescription.identifier);
    processIO.mouseover(function() {
        $('#' + description_div).html(f_getIODetails(ioDescription));
    });
    processIO.mouseleave(function() {
        $('#' + description_div).html('');
    });
    $('#' + identifier).append(processIO);

    var io = instance.getSelector("#" + processIOId);
    if(isInput) {
        instance.makeTarget(io, {
            dropOptions:{ hoverClass:"dragHover" },
            anchor:"LeftMiddle",
            maxConnections:1
        });
    }
    else {
        instance.makeSource(io, {
            anchor:"RightMiddle",
            connector:["Flowchart", {stub:30, cornerRadius:5}],
            connectorStyle:{strokeStyle:"#5192b5", lineWidth:2, outlineColor:"transparent", outlineWidth:4 }
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
        if(i >= 5){
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
function f_getFormatString(format){
    var string = '<div class="ioFormat">';
    if(typeof format === 'undefined')
        string += 'no format defined<br />';
    else
        string += (typeof format.mimetype !== 'undefined' && format.mimetype.length > 0 ? 'mimetype: ' + format.mimetype + '<br />' : '') +
            (typeof format.schema !== 'undefined' && format.schema.length > 0 ? 'schema: ' + format.schema + '<br />' : '') +
            (typeof format.type !== 'undefined' && format.type.length > 0 ? 'type: ' + format.type + '<br />' : '');
    if(string.lastIndexOf('<br />') > -1)
        string = string.substring(0, string.lastIndexOf('<br />'));
    return string + '</div>';
}

function f_removeProcess(instance, identifier){
    var process = instance.getSelector("#" + identifier);
    instance.remove(process);
}