/**
 * 
 */

var controller_name = 'controller';

var StyleHandler = {
		place_window: "place_navigation_block",
		place_content: "place_navigation_id",
		place_back: "back",
		people_window: "place_citizens_block",
		people_content: "place_citizens_id",
		dialog_window: "dialog_block",
		dialog_content: "dialog_id",
		dialog_close: "dialog_close",
		reply: "reply",
		line: "line"		
};

var SimpletownUIController = {
	APIRoot: "/",
	game: SimpletownEngine,
	redoLayout: function(){
		var scenario = this.game.getScenario();
		if(!scenario) return;
				
		var places = document.getElementById(StyleHandler.place_content);
		if(places){
			var html = "";
			if(scenario.connections){
				for(var field in scenario.connections){
					var previous = this.game.getPreviousScenario();
					if(previous && previous.isSameAs(scenario.connections[field])){
						html += "<p class='"+StyleHandler.place_back+"' ";
					}
					else{
						html += "<p ";
						
					}
					html += "onclick='"+controller_name+".moveTo(\""+scenario.connections[field].url+"\")'>..."+field+".</p>";
				}
				this.show(StyleHandler.place_window);
				places.innerHTML = html;
			}
			else{
				this.hide(StyleHandler.place_window);
			}
		}
		
		var people = document.getElementById(StyleHandler.people_content);
		if(people){
			people.innerHTML = "";
			if(scenario.citizens){
				for(var i = 0; i < scenario.citizens.length; i++){
					people.innerHTML += "<p onclick='"+controller_name+".talkTo(\""+i+"\")'>Talk to "+scenario['citizens'][i]['id']+".</p>";
				}
				this.show(StyleHandler.people_window);
			}
			else{
				this.hide(StyleHandler.people_window);
			}
		}
		
		/* Making sure invisible are not visible */
		this.hide(StyleHandler.dialog_window);
	},
	moveTo: function(place){
		this.game.changeScenario(place);
		this.redoLayout();
	},
	talkTo: function(who){
		var current = this.game.getScenario().getCitizenLine(who);
		var person = this.game.getScenario().getCitizenDialogs(who);
		
		var dialog = document.getElementById(StyleHandler.dialog_content);		
		
		if(dialog){
			dialog.innerHTML = "<p class='"+StyleHandler.line+"'>"+current.line+"</p>";
			if(current.replies){
				for(var i = 0; i < current.replies.length; i++){
					dialog.innerHTML += "<p class='"+StyleHandler.reply+"' onclick=\"" +
					controller_name + ".replyTo(" +who+",'"+current.replies[i]+"')\" >"
					+person[current.replies[i]].line+"</p>";
				}
			}
			else{
				dialog.innerHTML += "<p class='"+StyleHandler.dialog_close+"' onclick=\"" +
				controller_name + ".exitTalk()\" > Close </p>";
			}
		}
		this.hideAll();
		this.show(StyleHandler.dialog_window);
	},
	exitTalk: function(){
		this.redoLayout();
	},
	hideAll: function(){
		this.hide(StyleHandler.place_window);
		this.hide(StyleHandler.people_window);
		this.hide(StyleHandler.dialog_window);
	},
	hide: function(what){
		document.getElementById(what).style.visibility = 'hidden';
		document.getElementById(what).style.height = 0;
		document.getElementById(what).style.width = 0;
	},
	show: function(what){
		document.getElementById(what).style.visibility = 'visible';
		document.getElementById(what).style.height = '';
		document.getElementById(what).style.width = '';
	},
	replyTo: function(who,what){
		this.game.getScenario().replyCitizen(who,what);
		this.talkTo(who);
	}
};

var controller = SimpletownUIController;