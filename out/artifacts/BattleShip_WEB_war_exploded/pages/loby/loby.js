var refreshRate = 200; //mili seconds
var USERS_LIST_URL = "/userslist";
var GAMES_LIST_URL = "/gameslist";

//users = a list of usernames, essentially an array of javascript strings:
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function (index, username) {
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}

function refreshGamesList(games) {
    //clear all current users
    // gameSelectList.empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(games || [], function (index, gameName) {
        console.log("Adding game #" + index + ": " + gameName);
        //Check if there the item is not in the list (New Item)
        //If item is not found int the list, append it !
        if ($('#gameSelectList').find("option:contains('" + gameName + "')").length == 0) {
            $('<option value="' + gameName + '">' + gameName + '</option>').appendTo($("#gameSelectList"));
        }
    });

    $("#gameSelectList").find("option").each(function (index, data) {
        var val = data.value;
        if (games.includes(val) === false) {
            $(this).remove();
        }
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USERS_LIST_URL,
        success: function (users) {
            refreshUsersList(users);
        }
    });
}

function ajaxGamesList() {
    $.ajax({
        url: GAMES_LIST_URL,
        success: function (games) {
            refreshGamesList(games);
        }
    });
}

//activate the timer calls after the page is loaded
$(function () {

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

    //The games list is refreshed automatically every second
    setInterval(ajaxGamesList, refreshRate);

    //Check if the user is still connected to the server
    setInterval(isUserConnectedRoutine, refreshRate);
});

// Add onSubmit Event Handler for sending file to the server (using ajax)
$(function () {
    $("#uploadFileForm").on('submit', function (e) {
        e.preventDefault(); // prevent form for submiting for using ajax instead
        var file = $("#fileInput").prop('files')[0];
        if (file.type === 'text/xml') {
            sendFile(file);
        } else { // file type != 'text/xml'
            alert('Illegal file type, please choose an xml file');
        }
    });
});
// Delete game
$(function () {
    $("#deleteGameForm").on('submit', function (e) {
        e.preventDefault(); // prevent form for submiting for using ajax instead
        var curGameName = ($("#gameSelectList :selected").text());
        if (curGameName !== "") {
            var formDataGameName = new FormData();
            formDataGameName.append('gameName', curGameName);

            $.ajax({
                url: '/deleteGame',
                type: 'POST',
                data: {'gameName': curGameName},
                cache: false,
                success: function (data) {
                    //Delete the game from the list only if deleted from the server
                    if (data === "true") {
                        $("#gameSelectList :selected").remove();
                    }
                }
            });
        }
    });
});

function sendFile(file) {
    var data = new FormData();
    var gameName = $("#gameName").val();
    data.append('xmlFile', file);
    data.append('gameName', gameName);
    $.ajax({
        url: '/upload',
        type: 'POST',
        data: data,
        cache: false,
        enctype: "multipart/form-data",
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function (data, textStatus, jqXHR) {
            swal("File Uploaded Successfully!");
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
        var curGameName = ($("#gameSelectList :selected").text());

        if (curGameName !== "") {
            var formDataGameName = new FormData();
            formDataGameName.append('gameName', curGameName);
            $.ajax({
                url: '/gamesInfo',
                type: 'POST',
                data: {'gameName': curGameName},
                cache: false,
                success: function (data) {
                    fillInfo(data);
                    //alert("succsses");
                }
            });
        }
    });
});

function fillInfo(data) {
    $("#gameInfo p").remove();
    $("#gameInfo").append(data);
}

function isUserConnectedRoutine() {
    $.ajax({
        url: '/isUserConnected',
        type: 'GET',
        success: function (isConnected) {
            if (isConnected === "false") {
                $.get('/goToFirstPage', function (data) {
                    window.location.replace(data);
                });
            }
        }
    });
}

// Go to specific game
$(function () {
    // this is the id of the form
    $("#chooseGameForm").submit(function (e) {
        e.preventDefault(); // avoid to execute the actual submit of the form.
        var form = $(this);
        var curGameName = ($("#gameSelectList :selected").text());
        var formDataGameName = new FormData();
        formDataGameName.append('gameName', curGameName);

        $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: {'gameName': curGameName},
            datatype: 'json',
            success: function (data) {
                if (data.isGameFull === true) {
                    swal("Game is full, please choose another game")
                } else {
                    // Submit and redirect to the game apge
                    var hiddenGameForm = $("#hiddenGameForm");
                    $("#hiddenGameNameInput").val(curGameName);
                    hiddenGameForm.submit();
                }
            }
        });
    });
});