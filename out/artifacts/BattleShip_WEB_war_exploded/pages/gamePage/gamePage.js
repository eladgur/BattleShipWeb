//var lookForUpdate = true;
var myIndex = -1;
var isUpdated = false;
var currentIndex = -1;
var refreshRate = 100;
var duringRoutine = false;

window.onload = function () {
    askServerMyIndex();
    DisableTrackBoard();
    DisableShipBoard();
};


//----index actions-----------
function askServerMyIndex() {
    $.ajax({
        url: '/getIndex',
        type: 'GET',
        success: function (res) {
            // alert("your index is " + res);
            myIndex = parseInt(res);
        },
    });
}

function isCurrentIndexEqualToMyIndex() {
    var res;
    if (myIndex == currentIndex) {
        res = true;
        $('#whosTurn').text("your turn");
    }

    else {
        res = false;
        $('#whosTurn').text("Not your turn")
    }
    return res;
}

//-------------------------------------


function askServerCurrentIndex() {
    $.ajax({
        url: '/currentPlayerIndex',
        type: 'GET',
        success: function (res) {
            //alert("Current plyer index is "+ res);
            updateCurrentIndexVar(parseInt(res));
        },
    });
}

function updateCurrentIndexVar(number) {
    currentIndex = number;
}


function getUpdateFromServer() {
    $.ajax({
        url: '/getUpdateFromServer',
        type: 'GET',
        success: function (res) {
            if (res) {
                moveObj = $.parseJSON(res);
                updateBoards(moveObj);
                afterMoveActions();
            }
        }
    });
}

function isUpdateCompleted() {
    $.ajax({
        url: '/isUpdateCompleted',
        type: 'GET',
        success: function (isCompleted) {
            if (isCompleted == "true") {
                updateIsUpdatedVar(true);

            }
            else if (isCompleted == "false") {
                updateIsUpdatedVar(false);
            }
        },
    });
}

function updateIsUpdatedVar(bool) {
    isUpdated = bool;
}


//Declaration of a function that will be called on mouse clicks
function onTrackBoardSquareClickEventHandler(event) {

    // alert("Im clickable");
    afterMoveActions();
    document.getElementById('button').value = event.button;
    var clickedSquare = event.target;
    // clickedSquare.classList.add('clickedSquare');
    clickedSquare.classList.remove("trackBoardSquare"); // For not Re-binding On-Click Event !
    // clickedSquare.onclick = null;
    //Get row and cul from cell
    var row = event.currentTarget.attributes['row'].value;
    var col = event.currentTarget.attributes['col'].value;
    //Put row and col on form
    document.getElementById('form_row').value = row;
    document.getElementById('form_col').value = col;

    $.ajax({
        url: '/playerMove',
        type: 'POST',
        data: {"col": document.getElementById('form_col').value, "row": document.getElementById('form_row').value},
        dataType: "text",
        success: function (res) {
            moveObj = $.parseJSON(res);
            // alertMoveJsonDetials(moveObj);
            //updateBoards(moveObj);
            //afterMoveActions();
        }
    });
}

function alertMoveJsonDetials(moveObj) {
    alert("row: " + moveObj.row + "\n col: " + moveObj.column + "\n attack result: " + moveObj.attackResult + "\n attackers index: " + moveObj.attackersIndex);
}


function updateBoards(moveObj) // todo: to complete all of the 16 cases with elad
{
    var row = moveObj.row;
    var column = moveObj.column;
    var attackersIndex = moveObj.attackersIndex;
    var attackResult = moveObj.attackResult;

    if (myIndex === attackersIndex) {
        updateAttackerBoards(row, column, attackersIndex, attackResult);
    } else {
        updateDefenderBoards(row, column, attackersIndex, attackResult);
    }
}

function updateAttackerBoards(row, column, attackersIndex, attackResult) {

    var shipBoardSquare =  $("#shipBoard td[row= '" + row + "'][col= '" + column + "']" );
    var trackBoardSquare =  $("#trackBoard td[row= '" + row + "'][col= '" + column + "']" );

    if (attackResult === "SHIPHIT") {
        updateCssClass(trackBoardSquare, "hit");
    }
    else if (attackResult == "SHIPDROWNHIT") {
        //TODO: X
        updateCssClass(trackBoardSquare, "drownShip");
    }
    else if (attackResult == "REPEATEDHIT") {

    }
    else if (attackResult == "MISSHIT") {
        updateCssClass(trackBoardSquare, "miss");
    }
    else if (attackResult == "MINESHIP") {
        updateCssClass(trackBoardSquare, "mineExplosion");
        updateCssClass(shipBoardSquare, "hit");
    }
    else if (attackResult == "MINEDROWNSHIP") {
        updateCssClass(trackBoardSquare, "mineExplosion");
        updateCssClass(shipBoardSquare, "drownShip");
    }
    else if (attackResult == "MINEWATER") {
        updateCssClass(trackBoardSquare, "mineExplosion");
        updateCssClass(shipBoardSquare, "miss");
    }
    else if (attackResult == "MINEMINE") {
        updateCssClass(trackBoardSquare, "mineExplosion");
        updateCssClass(shipBoardSquare, "mineExplosion");
    }
    else if (attackResult == "MINEREAPETEDHIT") {
        updateCssClass(trackBoardSquare, "mineExplosion");
    }
    else if (attackResult == "INSERTMINE") {
        updateCssClass(shipBoardSquare, "mine");
    }
    else {
        alert("attack result value is " + attackResult + "and it Doesnt much any option")
    }
}

function updateDefenderBoards(row, column, attackersIndex, attackResult) {

    var shipBoardSquare =  $("#shipBoard td[row= '" + row + "'][col= '" + column + "']" );
    var trackBoardSquare =  $("#trackBoard td[row= '" + row + "'][col= '" + column + "']" );

    if (attackResult === "SHIPHIT") {
        updateCssClass(shipBoardSquare, "hit");
    }
    else if (attackResult == "SHIPDROWNHIT") {
        //TODO: X
        updateCssClass(shipBoardSquare, "drownShip");
    }
    else if (attackResult == "REPEATEDHIT") {
    }
    else if (attackResult == "MISSHIT") {
        updateCssClass(shipBoardSquare, "miss");
    }
    else if (attackResult == "MINESHIP") {
        updateCssClass(shipBoardSquare, "mineExplosion");
        updateCssClass(trackBoardSquare, "hit");
    }
    else if (attackResult == "MINEDROWNSHIP") {
        updateCssClass(shipBoardSquare, "mineExplosion");
        updateCssClass(trackBoardSquare, "drownShip");
    }
    else if (attackResult == "MINEWATER") {
        updateCssClass(shipBoardSquare, "mineExplosion");
        updateCssClass(trackBoardSquare, "miss");
    }
    else if (attackResult == "MINEMINE") {
        updateCssClass(trackBoardSquare, "mineExplosion");
        updateCssClass(shipBoardSquare, "mineExplosion");
    }
    else if (attackResult == "MINEREAPETEDHIT") {
        updateCssClass(shipBoardSquare, "mineExplosion");
    }
    else if (attackResult == "INSERTMINE") {
    }
    else {
        alert("attack result value is " + attackResult + "and it Doesnt much any option")
    }
}

function updateCssClass(squareObject,className) {
    squareObject.attr('class',className);
}


//------ before move and after move actions sector
function BeforeMoveActions() {
    // isUpdateCompleted();
    // if( isUpdated == false)
    // {
    //     getUpdateFromServer();
    // }
    EnableTrackBoard();
    // EnableShipBoard(); // ***for mines
}

function afterMoveActions() {
    //lookForUpdate = true;
    DisableTrackBoard();
    // DisableShipBoard();
}

//-------------------------------------------------


//-- diable and enable button sector

function disableSomethingClickableEventHandler(event) {

    // alert("Im Not clickable");

}


function DisableShipBoard() {
   // $("#shipBoard td").css("background-color", "yellow");
    $("#shipBoard td").unbind("click");
    $("#shipBoard td").on("click", disableSomethingClickableEventHandler);


}

function EnableShipBoard() {
   // $("#shipBoard td").css("background-color", "red");
    $("#shipBoard td").unbind("click");
    $("#shipBoard td").on("click", onTrackBoardSquareClickEventHandler); // change to ship board event handler(for mines)
}

function DisableTrackBoard() {
   // $("#trackBoard td").css("background-color", "yellow");
    $("#trackBoard td").unbind("click");
    $("#trackBoard td").on("click", disableSomethingClickableEventHandler);
}

function EnableTrackBoard() {
   // $("#trackBoard td").css("background-color", "red");
   //  $("#trackBoard td").unbind("click");
   //  $("#trackBoard td").on("click", onTrackBoardSquareClickEventHandler);
    $(".trackBoardSquare").unbind("click");
    $(".trackBoardSquare").on("click", onTrackBoardSquareClickEventHandler);
}

//-----------------------------------------------------


//activate the timer calls after the page is loaded
$(function () {

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(serverRoutine, refreshRate);

});

function serverRoutine() {
    if(duringRoutine === false) {
        duringRoutine = true;
        isUpdateCompleted();
        if (isUpdated === true) {
            askServerCurrentIndex();
            if (isCurrentIndexEqualToMyIndex()) {
                BeforeMoveActions();
            }
            else {
                afterMoveActions();
            }
        }
        else {
            getUpdateFromServer();
            afterMoveActions();
        }

        duringRoutine = false;
    }

}