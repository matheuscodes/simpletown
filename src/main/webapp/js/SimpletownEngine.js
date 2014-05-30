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
			NPCnode.used = true;
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
	
	this.getItem = function(item_id){
		var next = [];
		if(this.items){
			for(var i = 0; i < this.items.length;i++){
				if(this.items[i].id == item_id){
					return this.items[i];
				}
				else{
					if(this.items[i].items){
						for(var j = 0; j < this.items[i].items.length;j++){
							next.push(this.items[i].items[j]);
						}
					}
				}
			}
			for(var k = 0; k < next.length;k++){
				if(next[k].id == item_id){
					return next[k];
				}
				else{
					if(next[k].items){
						for(var l = 0; l < next[k].items.length;l++){
							next.push(next[k].items[l]);
						}
					}
				}
			}
		}
		return null;
	};
}

var URLHandler = {
	item_root: "items/",
	place_root:"places/",
	player_root: "player/",
	scenario: "scenario/",
	script: "script/",
	lead: "lead/",
	getPlace: function(place){
		return this.place_root + place;
	},
	getItem: function(item){
		return this.item_root + item;
	},
	getScript: function(place){
		return this.player_root + this.script + place;
	},
	getPlayerPlace: function(){
		return this.player_root + this.scenario;
	},
	getPlayerLead: function(){
		return this.player_root + this.lead;
	},
	getPlayerMove: function(place){
		return this.player_root + place;
	}
};

var ConditionNode = {
	items: function(game,condition){
		for(var i = 0; i < condition.length; i++){
			if(game.getLead().hasItemID(condition[i])){
				return true;
			}
		}
		return false;
	},
	skills: function(game,condition){
		for(var i = 0; i < condition.length; i++){
			var flag = true;
			for(var skill in condition[i]){
				if(!game.getLead().atSkillLevel(skill,condition[i][skill])){
					flag = false;
				}
			}
			if(flag){
				return true;
			}
		}
		return false;
	},
	attributes: function(game,condition){
		for(var i = 0; i < condition.length; i++){
			var flag = true;
			for(var attribute in condition[i]){
				if(!game.getLead().atAttributeLevel(attribute,condition[i][attribute])){
					flag = false;
				}
			}
			if(flag){
				return true;
			}
		}
		return false;
	} 
};

function Condition(conditions){
	if(conditions.length){
		this.ORs = [];
		for(var i = 0; i < conditions.length; i++){
			this.ORs.push(new Condition(conditions[i]));
		}
	}
	else{
		this.ANDs = {};
		for(var data in conditions){
			this.ANDs[data] = conditions[data];
		}
	}
	
	this.evaluate = function(game){
		if(this.ORs){
			for(var j = 0; j < this.ORs.length; j++){
				if(this.ORs[j].evaluate(game)){
					return true;
				}
			}
			return false;
		}
		else {
			for(var condition in this.ANDs){
				if(!ConditionNode[condition](game,this.ANDs[condition])){
					return false;
				}
			}
			return true;
		}
	};
}

var skillLevel = function(level){
	switch(level.toLowerCase()){
		case "curious": return 1;
		case "student": return 2;
		case "apprentice": return 3;
		case "professional": return 4;
		case "expert": return 5;
		case "master": return 6;
		default: return 0;
	}
};

function Citizen(who){
	for(var data in who){
		this[data] = who[data];
	}
	
	this.hasItemID = function(item){
		if(this.items){
			for(var i = 0; i < this.items.length;i++){
				if(this.items[i].id == item){
					return true;
				}
			}
		}
		return false;
	};
	
	this.atSkillLevel = function(skill,level){
		if(this.skills){
			return skillLevel(this.skills[skill]) >= skillLevel(level);  
		}
	};
	
	this.atAttributeLevel = function(attribute,level){
		console.log(this.attributes[attribute]+","+level);
		return this.attributes[attribute] >= level;
	};
}

var Missing = {
	fetchItems: function(items){
		if(items && items.length){
			for(var i = 0; i < items.length; i++){
				this.fetchItems(items[i].items);
				this.fetchItems(items[i]);
			}
		}
		else if(items){
			var connection = new XMLHttpRequest();
			connection.open("GET",URLHandler.getItem(items.id),false);
			connection.send();
			var item = JSON.parse(connection.responseText);
			for(var data in item){
				items[data] = item[data];
			}
		}
	},
	fetchAll: function(script){
		this.fetchItems(script.items);
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
			connection.setRequestHeader("Content-Type", "application/x-json");
			connection.setRequestHeader("Content-Length", data.length);
			connection.send(data);
		}
		this.current_scenario = this.getCachedScenario(place);
		connection = new XMLHttpRequest();
		connection.open("PUT",URLHandler.getPlayerMove(this.current_scenario.url),false);
		connection.send();
	},
	getLead: function(){
		return this.lead;
	},
	getScenario: function(){
		return this.current_scenario;
	},
	getPreviousScenario: function(){
		return this.previous_scenario;
	},
	initialize: function(){
		var connection = new XMLHttpRequest();
		connection.open("GET",URLHandler.getPlayerLead(),false);
		connection.send();
		var player_lead = JSON.parse(connection.responseText);
		this.lead = new Citizen(player_lead); 
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
			else{
				script = loaded;
				Missing.fetchAll(script);
			}
			
			this.scenarios[stage.url] = new Scenario(stage,script);
			return this.scenarios[stage.url];
		}
		
		return this.scenarios[which];
	}
};

SimpletownEngine.initialize();