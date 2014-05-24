/**
 * 
 */

var PlaceRoot = "places/";

var SimpletownEngine = {
	scenario: {},
	changeScenario: function(place){
		var connection = new XMLHttpRequest();
		connection.open("GET",PlaceRoot+place,false);
		connection.send();
		//TODO handle errors
		this.scenario = JSON.parse(connection.responseText);
	},
	getScenario: function(){
		return this.scenario;
	}
};