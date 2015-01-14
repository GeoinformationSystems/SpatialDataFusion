var jsPlumbProcesses;

var process = function(name){
	this.name = name;
	this.asDiv = function() {
		return "<div " +
				"class='plumb_process jsplumb-draggable' " +
				"id='" + this.name + "'>" + this.name + "</div>";
	};
};

//init plumb environment
jsPlumb.ready(function() {
	
	jsPlumbProcesses = jsPlumb.getInstance({
		DragOptions : { cursor: 'pointer', zIndex:2000 },
		Container:"plumb_processes"
	});
	
	jsPlumbProcesses.doWhileSuspended(function() {
	
		jsPlumbProcesses.draggable(
			jsPlumb.getSelector(".plumb_processes .plumb_process"), 
			{ grid: [20, 20] }
		);
		
//		jsPlumbProcesses.connect({source:"test1", target:"test2"});
		
	});

});

function f_addProcess(process){
	$("#plumb_processes").append(process.asDiv());
	jsPlumbProcesses.draggable(
		jsPlumb.getSelector("#plumb_processes .plumb_process"), { 	
			containment: "#plumb_processes",
			grid: [20, 20] }
	);
}