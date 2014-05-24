/**
 * 
 */

var controllerName = 'controller';

var SimpletownUIController = {
	APIRoot: "/",
	game: SimpletownEngine,
	redoLayout: function(){
		var places = document.getElementById("place_navigation_id");
		if(places){
			console.log("aaaa");
			places.innerHTML = "";
			for(var field in this.game.getScenario()['connections']){
				places.innerHTML += "<p onclick='"+controllerName+".moveTo(\""+SimpletownEngine.getScenario()['connections'][field]['url']+"\")'>..."+field+".</p>";
			}
		}
	},
	moveTo: function(place){
		this.game.changeScenario(place);
		this.redoLayout();
	}
};

var controller = SimpletownUIController;