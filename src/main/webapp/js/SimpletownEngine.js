/**
 * 
 */

function Scenario(stage, script){
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
	}
};

var SimpletownEngine = {
	scenarios: {},
	changeScenario: function(place){
		this.current_scenario = this.getCachedScenario(place);
	},
	getScenario: function(){
		return this.current_scenario;
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
			var script = JSON.parse(connection.responseText);
			
			this.scenarios[stage.url] = new Scenario(stage,script);
			return this.scenarios[stage.url];
		}
		
		return this.scenarios[which];
	}
};

SimpletownEngine.initialize();