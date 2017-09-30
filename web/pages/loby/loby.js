var refreshRate = 2000; //mili seconds
var USERS_LIST_URL = "/userslist";
var GAMES_LIST_URL = "/gameslist";

//users = a list of usernames, essentially an array of javascript strings:
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}

function refreshGamesList(games) {
    //clear all current users
    $("#gamesList").empty();
    $("#gameSelectList").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(games || [], function(index, gameName) {
        console.log("Adding game #" + index + ": " + gameName);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + gameName + '</li>').appendTo($("#gamesList"));
        $('<option value="' + gameName + '">' + gameName + '</option>').appendTo($("#gameSelectList"));
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USERS_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function ajaxGamesList() {
    $.ajax({
        url: GAMES_LIST_URL,
        success: function(games) {
            refreshGamesList(games);
        }
    });
}

//activate the timer calls after the page is loaded
$(function() {

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

    //The games list is refreshed automatically every second
    setInterval(ajaxGamesList, refreshRate);
});

// Add onSubmit Event Handler for sending file to the server (using ajax)
$(function () {
    $("#uploadFileForm").on('submit', function (e) {
        e.preventDefault(); // prevent form for submiting for using ajax instead
        var file = $("#fileInput").prop('files')[0];
        if(file.type === 'text/xml'){
          sendFile(file);
        } else { // file type != 'text/xml'
          alert('Illegal file type, please choose an xml file');
        }
    });
});

function sendFile(file) {
  var data = new FormData();
  var gameName = $("#gameName").val();
  data.append('xmlFile',file);
  data.append('gameName',gameName);
  $.ajax({
      url: '/upload',
      type: 'POST',
      data: data,
      cache: false,
      enctype: "multipart/form-data",
      processData: false, // Don't process the files
      contentType: false, // Set content type to false as jQuery will tell the server its a query string request
      success: function (data, textStatus, jqXHR) {
          alert("File Uploaded Successfully!")
      },
      error: function (jqXHR) {
          alert("Unscussfull file upload: " + jqXHR.responseText);
      }
  });

}

//function getGameInfo

$(function () {
    $("#gameSelectList").on('change', function (e) {
        e.preventDefault(); // prevent form for submiting for using ajax instead
        var gameName = ($("#gameSelectList :selected").text());
        var formDataGameName = new FormData();
        formDataGameName.append('gameName',gameName);
        $.ajax({
            url: '/gamesInfo',
            type: 'POST',
            data: formDataGameName,
            success: function (data) {
                fillInfo(data);
            }

        });
    });
});

function fillInfo(data){
    data.appendTo($("#gameInfo"));
}

function onStartGameButtonEventHandler(e) {
    $.ajax({
        url: '/gamePage',
        type: 'GET',
    });
}