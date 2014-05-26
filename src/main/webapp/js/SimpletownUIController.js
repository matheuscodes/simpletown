/**
 * 
 */

var controller_name = 'controller';

var SimpletownUIController = {
	APIRoot: "/",
	game: SimpletownEngine,
	redoLayout: function(){
		var scenario = this.game.getScenario();
		if(!scenario) return;
		
		var places = document.getElementById("place_navigation_id");
		if(places){
			places.innerHTML = "";
			//FIXME for places with no connections
			for(var field in scenario['connections']){
				places.innerHTML += "<p onclick='"+controller_name+".moveTo(\""+scenario['connections'][field]['url']+"\")'>..."+field+".</p>";
			}
		}
		
		var people = document.getElementById("place_citizens_id");
		if(people){
			people.innerHTML = "";
			if(scenario['citizens']){
				for(var i = 0; i < scenario['citizens'].length; i++){
					people.innerHTML += "<p onclick='"+controller_name+".talkTo(\""+i+"\")'>Talk to "+scenario['citizens'][i]['id']+".</p>";
				}
			}
		}
	},
	moveTo: function(place){
		this.game.changeScenario(place);
		this.redoLayout();
	},
	talkTo: function(who){
		var current = this.game.getScenario().getCitizenLine(who);
		var person = this.game.getScenario().getCitizenDialogs(who);
		var dialog = document.getElementById("dialog_id");
		if(dialog){
			dialog.innerHTML = "<p class='line'>"+current.line+"</p>";
			if(current.replies){
				for(var i = 0; i < current.replies.length; i++){
					dialog.innerHTML += "<p class='reply' onclick=\"" +
					controller_name + ".replyTo(" +who+",'"+current.replies[i]+"')\" >"
					+person[current.replies[i]].line+"</p>";
				}
			}
		}
	},
	replyTo: function(who,what){
		this.game.getScenario().replyCitizen(who,what);
		this.talkTo(who);
	}
};

var controller = SimpletownUIController;