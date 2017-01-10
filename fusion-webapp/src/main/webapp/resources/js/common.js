//change visibility of all elements identified by class name
function f_changeVisibility(className){
	elements = document.getElementsByClassName(className);
	if(elements && elements.length > 0){
		for(var i in elements){
			var element = elements[i];
			if(element.style){
				if(element.style.display == "inline")	element.style.display = "none"; 
				else element.style.display = "inline";
			}
		}
	}
}

//get element text by id
function f_getText(elementId) {
	return document.getElementById(elementId).innerHTML;
}

//set element text by id
function f_setText(elementId, text, resize) {
	element = document.getElementById(elementId);
	element.innerHTML = text;
	if(resize)
		element.style = 'width:' + (parseInt(element.offsetWidth) + 20) + 'px;';
}

//clear element text by id
function f_clearText(elementId, resize) {
	element = document.getElementById(elementId);
	element.innerHTML = '&nbsp;';
	if(resize)
		element.style = '';
}

function f_getSimpleId(id, splitNumberSign){
	if(splitNumberSign !== 'undefined' && splitNumberSign === true && id.indexOf('#') !== -1)
		return id.split('#')[1];
	else if(id.indexOf('/') !== -1)
		return id.split('/')[id.split('/').length - 1];
	else
		return id;
}

function f_getSimpleMeasurementValue(value){
	if(value.indexOf('.') > -1 && !isNaN(parseFloat(value)))
		return parseFloat(value).toFixed(6);
	else
		return value;
}

//remove all listeners of an element by cloning
function f_removeListeners(id){
	var elemOld = document.getElementById(id);
	var elemNew = elemOld.cloneNode(true);
	elemOld.parentNode.replaceChild(elemNew, elemOld);
}

//toggle visibility for objective selection
function f_toggleVisibility(className){
	var nodes = document.getElementsByClassName(className);
	var i;
	for (i=0; i<nodes.length; i++) {
		if(nodes[i].style.display == "none")
			nodes[i].style.display = "block";
		else
			nodes[i].style.display = "none";
	}
}