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
		item_window: "place_items_block",
		item_content: "place_items_id",
		item_action: "item_action",
		item_name: "item_name",
		dialog_window: "dialog_block",
		dialog_content: "dialog_id",
		close: "close",
		reply: "reply",
		used: "used",
		line: "line"
};

function SpecialActions(){
	this.open = false;
	this.canExecute = function(item,action){
		switch(action){
			case "open": return item.items;
		}
		return false;
	};
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
					people.innerHTML += "<p onclick='"+controller_name+".talkTo(\""+i+"\")'>Talk to "+scenario.citizens[i].id+".</p>";
				}
				this.show(StyleHandler.people_window);
			}
			else{
				this.hide(StyleHandler.people_window);
			}
		}
		
		var items = document.getElementById(StyleHandler.item_content);
		if(scenario.items){
			this.doItem(items,null,scenario.items);
			this.show(StyleHandler.item_window);
		}
		else{
			this.hide(StyleHandler.item_window);
		}
		
		/* Making sure invisible are not visible */
		this.hide(StyleHandler.dialog_window);
	},
	openItem: function(item_id){
		var item = this.game.getScenario().getItem(item_id);
		var content = document.getElementById("item"+item_id);
		this.doItem(content,item, item.items);
	},
	doItem: function(content,item,items){
		if(content){
			content.innerHTML = "";
			var text = "<ul>";
			if(item){
				text += "<p class=\""+StyleHandler.item_name+"\">"+item.name+"</p>";
			}
			if(items){
				var sa = new SpecialActions();
				for(var i = 0; i < items.length; i++){
					var visible = true;
					if(items[i].conditions){
						var condition = new Condition(items[i].conditions);
						visible = condition.evaluate(this.game);
					}
					if(visible){
						text += "<li id=\"item"+items[i].id+"\">";
						for(var action in sa){
							if(items[i].actions && items[i].actions[action]){
								var c = new Condition(items[i].actions[action].conditions);
								if(c.evaluate(this.game)){
									text += "<p class=\""+StyleHandler.item_action+"\" ";
									text += "onclick='"+controller_name+".openItem("+items[i].id+")'";
									text += ">"+action+"</p>";
								}
								else{
									if(items[i].actions[action].placeholder){
										text += "<p class=\""+StyleHandler.item_action+"\">"+action;
										text += "<br/>("+items[i].actions[action].placeholder+")";
										text += "</p>";
									}
								}
								sa[action] = true;
							}
							else{
								if(sa.canExecute(items[i], action)){
									text += "<p class=\""+StyleHandler.item_action+"\" ";
									text += "onclick='"+controller_name+".openItem("+items[i].id+")'";
									text += ">"+action+"</p>";
									sa[action] = true;
								}
							}
							
						}
						for(var action in items[i].actions){
							if(!sa[action]){
								var c = new Condition(items[i].actions[action].conditions);
								if(c.evaluate(this.game)){
									text += "<p class=\""+StyleHandler.item_action+"\">"+action+"</p>";
								}
								else{
									if(items[i].actions[action].placeholder){
										text += "<p class=\""+StyleHandler.item_action+"\">"+action;
										text += "<br/>("+items[i].actions[action].placeholder+")";
										text += "</p>";
									}
								}
							}
						}
						text += "<p class=\""+StyleHandler.item_name+"\">"+items[i].name+"</p>";
						text += "</li>";
					}
				}
				text += "</ul>";
				content.innerHTML = text;
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
		
		var dialog = document.getElementById(StyleHandler.dialog_content);		
		
		if(dialog){
			dialog.innerHTML = "<p class='"+StyleHandler.line+"'>"+current.line+"</p>";
			var text = "";
			if(current.replies){
				for(var i = 0; i < current.replies.length; i++){
					if(person[current.replies[i]].used){
						text += "<p class='"+StyleHandler.reply+" "+StyleHandler.used+"'>";
					}
					else{
						text += "<p class='"+StyleHandler.reply+"' onclick=\"";
						text += controller_name + ".replyTo(" +who+",'"+current.replies[i]+"')\" >";
					}
					text += person[current.replies[i]].line+"</p>";
				}
				dialog.innerHTML += text;
			}
			else{
				dialog.innerHTML += "<p class='"+StyleHandler.close+"' onclick=\"" +
				controller_name + ".exitTalk()\" > Close </p>";
			}
		}
		this.hideAll();
		this.show(StyleHandler.dialog_window);
	},
	replyTo: function(who,what){
		this.game.getScenario().replyCitizen(who,what);
		this.talkTo(who);
	},
	exitTalk: function(){
		this.redoLayout();
	},
	hideAll: function(){
		this.hide(StyleHandler.place_window);
		this.hide(StyleHandler.people_window);
		this.hide(StyleHandler.item_window);
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
	}
};

var controller = SimpletownUIController;