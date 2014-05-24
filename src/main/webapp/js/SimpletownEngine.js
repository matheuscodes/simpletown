/**
 * 
 */

var PlaceRoot = "places/";
var PlayerRoot = "player/";
var Scenario = "scenario/";
var Script = "script/";

var SimpletownEngine = {
	scenario: {},
	changeScenario: function(place){
		var connection = new XMLHttpRequest();
		connection.open("GET",PlaceRoot+place,false);
		connection.send();
		//TODO handle errors
		this.scenario = JSON.parse(connection.responseText);
		
		connection.open("GET",PlayerRoot+Script+this.scenario['url'],false);
		connection.send();
		var script = JSON.parse(connection.responseText);
		for(var data in script){
			this.scenario[data] = script[data];
		}
		this.makeScenario();
	},
	getScenario: function(){
		return this.scenario;
	},
	initialize: function(){
		var connection = new XMLHttpRequest();
		//TODO temp hack, remove
		
		connection.open("GET",PlayerRoot+Scenario,false);
		connection.send();
		//TODO handle errors
		this.scenario = JSON.parse(connection.responseText);
		
		connection.open("GET",PlayerRoot+Script+this.scenario['url'],false);
		connection.send();
		var script = JSON.parse(connection.responseText);
		for(var data in script){
			this.scenario[data] = script[data];
		}
		this.makeScenario();
	},
	makeScenario: function(){
		//TODO make a proper JS class thing.
		this.scenario['getCitizenLine'] = function(who){
			if(this.citizens && this.citizens[who]){
				var dialogs = this.citizens[who].dialogs;
				if(dialogs && !dialogs.current){
					this.citizens[who].dialogs.current = dialogs[dialogs.start];
				}
				return this.citizens[who].dialogs.current;
			}
		};
		this.scenario['getCitizenDialogs'] = function(who){
			if(this.citizens && this.citizens[who] && this.citizens[who].dialogs){
				return this.citizens[who].dialogs;
			}
		};
		this.scenario['replyCitizen'] = function(who,what){
			if(this.citizens && this.citizens[who] && this.citizens[who].dialogs){
				//TODO really get always the first match?
				//Here NPC can decide what to reply.
				var NPCnode = this.citizens[who].dialogs[what];

				console.log(NPCnode.line);
				//Always take the first...
				if(NPCnode.replies[0]){
					this.citizens[who].dialogs.current = this.citizens[who].dialogs[NPCnode.replies[0]];
				}
				else {
					this.citizens[who].dialogs.current = {};
				}
			}
		};
	}
};

SimpletownEngine.initialize();