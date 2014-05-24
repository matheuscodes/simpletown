/**
 * 
 */

var SimpletownUIController = {
	APIRoot: "/",
	game: SimpletownEngine,
	redoLayout: function(){
		var places = document.getElementById("places_to_go_id");
		places.innerHTML = "";
		for(var field in SimpletownEngine.getScenario()['connections']){
			places.innerHTML += "<p onclick='SimpletownUIController.moveTo(\""+SimpletownEngine.getScenario()['connections'][field]['url']+"\")'>"+field+"</p>";
		}
	},
	moveTo: function(place){
		this.game.changeScenario(place);
		this.redoLayout();
	}
};

