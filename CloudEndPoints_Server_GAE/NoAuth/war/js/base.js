var google = google || {};

google.endpoints = google.endpoints || {};	

google.endpoints.notesApi = google.endpoints.notesApi || {};


google.endpoints.notesApi.init = function(apiRoot) {
	var callback = function() {
//		alert("noteendpoint API uploaded");
	}
	
	gapi.client.load('noteendpoint', 'v1', callback, apiRoot);
};

google.endpoints.notesApi.showHome = function() {
	$("#mainMenu").show();
	$("#listNotes").hide();
	$("#newNote").hide();
	$("#note").hide();
};

google.endpoints.notesApi.showNewNote = function() {
	var idInput 			= document.querySelector(".newId");
	var emailAddressInput 	= document.querySelector(".newEmailAddress");
	var descriptionInput 	= document.querySelector(".newDescription");
		
	idInput.value = "";
	emailAddressInput.value = "";
	descriptionInput.value = "";
	
	$("#mainMenu").hide();
	$("#listNotes").hide();
	$("#newNote").show();
	$("#note").hide();
};

google.endpoints.notesApi.showListNotes = function() {
	$("#mainMenu").hide();
	$("#listNotes").show();
	$("#newNote").hide();
	$("#note").hide();
};

google.endpoints.notesApi.showNoteDiv = function() {
	$("#mainMenu").hide();
	$("#listNotes").hide();
	$("#newNote").hide();
	$("#note").show();
};

google.endpoints.notesApi.add = function() {
	var idInput 			= document.querySelector(".newId");
	var emailAddressInput 	= document.querySelector(".newEmailAddress");
	var descriptionInput 	= document.querySelector(".newDescription");
		
	var id 				= idInput.value;
	var emailAddress 	= emailAddressInput.value;
	var description 	= descriptionInput.value;
	
	gapi.client.noteendpoint.note.insert({'id': id, 'emailAddress': emailAddress, 'description': description}).execute(function(resp) {
		idInput.value = "";
		emailAddressInput.value = "";
		descriptionInput.value = "";
		
		google.endpoints.notesApi.showHome();
	});
};


google.endpoints.notesApi.list = function() {
	google.endpoints.notesApi.showListNotes();
	
	gapi.client.noteendpoint.note.list().execute(function(resp) {
		var listTable = document.getElementById('listTable');
		listTable.innerHTML = '';
		
		var header = document.createElement('tr');
		header.innerHTML =  "<th>Id</th><th>Mail</th><th>Note</th>";
		listTable.appendChild(header);
		
	    if (resp.items) {
	      for (var i = 0; i < resp.items.length; i++) {
	        var note = document.createElement('tr');
	        note.innerHTML = "<td><a href=\"javascript:google.endpoints.notesApi.showNote('"+ resp.items[i].id +"', '"+ resp.items[i].emailAddress +"', '"+ resp.items[i].description +"')\">"+ resp.items[i].id +"</a></td><td>"+ resp.items[i].emailAddress +"</td><td>"+ resp.items[i].description +"</td>";
	        listTable.appendChild(note);
	      }
	    }
	});
};


google.endpoints.notesApi.update = function() {
	var idInput 			= document.querySelector(".id");
	var emailAddressInput 	= document.querySelector(".emailAddress");
	var descriptionInput 	= document.querySelector(".description");
	
	var id 				= idInput.value;
	var emailAddress 	= emailAddressInput.value;
	var description 	= descriptionInput.value;

	gapi.client.noteendpoint.note.update({'id': id, 'emailAddress': emailAddress, 'description': description}).execute(function(resp) {
		idInput.value = "";
		emailAddressInput.value = "";
		descriptionInput.value = "";
		
		google.endpoints.notesApi.list();
	});
};


google.endpoints.notesApi.remove = function() {
	var idInput 			= document.querySelector(".id");
	var emailAddressInput 	= document.querySelector(".emailAddress");
	var descriptionInput 	= document.querySelector(".description");
	
	var id = idInput.value;
	
	gapi.client.noteendpoint.note.remove({'id': id}).execute(function(resp) {
		idInput.value = "";
		emailAddressInput.value = "";
		descriptionInput.value = "";
		
		google.endpoints.notesApi.showHome();
	});
};

google.endpoints.notesApi.showNote = function(id, emailAddress, description) {	
	google.endpoints.notesApi.showNoteDiv();
	
	var idInput 			= document.querySelector(".id");
	var emailAddressInput 	= document.querySelector(".emailAddress");
	var descriptionInput 	= document.querySelector(".description");
	
	idInput.value = id;
	emailAddressInput.value = emailAddress;
	descriptionInput.value = description;
};