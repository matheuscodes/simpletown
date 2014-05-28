/**
 * 
 */

function Scenario(stage, script){
	this.simpletownClass = 'Scenario';
	for(var data in stage){
		this[data] = stage[data];
	}
	for(var data in script){
		this[data] = script[data];
	}
	this.getCitizenLine = function(who){
		if(this.citizens && this.citizens[who]){
			var dialogs = this.citizens[who].dialogs;
			if(dialogs && !dialogs.current){
				this.citizens[who].dialogs.current = dialogs[dialogs.start];
			}
			return this.citizens[who].dialogs.current;
		}
	};
	this.getCitizenDialogs = function(who){
		if(this.citizens && this.citizens[who] && this.citizens[who].dialogs){
			return this.citizens[who].dialogs;
		}
	};
	this.replyCitizen = function(who,what){
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
	
	this.isSameAs = function(scenario){
		if(scenario && scenario.url){
			if(scenario.simpletownClass && scenario.simpletownClass != this.simpletownClass){
				return false;
			}
			return this.url === scenario.url;
		}
		return false;
	};
}

var URLHandler = {
	place_root:"places/",
	player_root: "player/",
	scenario: "scenario/",
	script: "script/",
	getScript: function(place){
		return this.player_root + this.script + place;
	},
	getPlace: function(place){
		return this.place_root + place;
	},
	getPlayerPlace: function(){
		return this.player_root + this.scenario;
	},
	getPlayerMove: function(place){
		return this.player_root + place;
	}
};

var SimpletownEngine = {
	scenarios: {},
	changeScenario: function(place){
		if(this.current_scenario){
			this.previous_scenario = this.current_scenario;

			var connection = new XMLHttpRequest();
			connection.open("PUT",URLHandler.getScript(this.current_scenario.url),false);
			data = JSON.stringify(this.current_scenario);
			//TODO send data with proper thing... without size limits
			connection.setRequestHeader("Content-Type", "application/x-json");
			connection.setRequestHeader("Content-Length", data.length);
			connection.send(data);
		}
		this.current_scenario = this.getCachedScenario(place);
		connection = new XMLHttpRequest();
		connection.open("PUT",URLHandler.getPlayerMove(this.current_scenario.url),false);
		connection.send();
	},
	getScenario: function(){
		return this.current_scenario;
	},
	getPreviousScenario: function(){
		return this.previous_scenario;
	},
	initialize: function(){
		this.current_scenario = this.getCachedScenario();
	},
	getCachedScenario: function(which){
		var stage;
		var connection = new XMLHttpRequest();
		if(!which){
			connection.open("GET",URLHandler.getPlayerPlace(),false);
			connection.send();
			stage = JSON.parse(connection.responseText);
		}
		else if(!this.scenarios[which]){
			connection.open("GET",URLHandler.getPlace(which),false);
			connection.send();
			stage = JSON.parse(connection.responseText);
		}
		
		if(stage && !this.scenarios[stage.url]){
			connection.open("GET",URLHandler.getScript(stage.url),false);
			connection.send();
			var loaded = JSON.parse(connection.responseText);
			var script;
			if(loaded.saved_on){
				script = loaded.state;
				script.saved_on = loaded.saved_on;
			}
			
			this.scenarios[stage.url] = new Scenario(stage,script);
			return this.scenarios[stage.url];
		}
		
		return this.scenarios[which];
	}
};

SimpletownEngine.initialize();