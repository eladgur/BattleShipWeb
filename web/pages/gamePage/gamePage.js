var myIndex = -1;
var isUpdated = false;
var currentIndex = -1;
var refreshRate = 100;
var duringRoutine = false;
var Quit = false;

window.onload = function () {
    askServerMyIndex();
    DisableTrackBoard();
    $("#quitButton").on("click",onQuitCLick);
};

function userQuitManuallyActions() {
    $.ajax({
        url: '/userManuallyQuit',
        type: 'GET',
        success: function () {
        },
    });
}

function onQuitCLick(event)
{
    userQuitManuallyActions();
    swal("We are sorry", "You lose the game technically because you left game =[", "error").then((value) => { QuitGame(); });
}

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
    if (myIndex === currentIndex) {
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
            if (isCompleted === "true") {
                updateIsUpdatedVar(true);
            }
            else if (isCompleted === "false") {
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
    clickedSquare.classList.remove("trackBoardSquare"); // For not Re-binding On-Click Event !
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
        // success: function () {
        // For Debugging
        // }
    });
}

function alertMoveJsonDetials(moveObj) {
    alert("row: " + moveObj.row + "\n col: " + moveObj.column + "\n attack result: " + moveObj.attackResult + "\n attackers index: " + moveObj.attackersIndex);
}

function updatePageOnTechnicalGameEnd(attackersIndex) {
    if(attackersIndex===myIndex)
    {
        swal("We are sorry", "You lose the game technically because you left game =[", "error").then((value) => { QuitGame(); });
    }
    else
    {
        swal("Your Opoonent ran away!", "You win the game =]", "success").then((value) => { QuitGame(); });
    }

}

function updateScore(index0Score, index1Score) {
    if (myIndex==0){
        $("#scoreHolder").text(index0Score);
    }
    else if(myIndex == 1) {
        $("#scoreHolder").text(index1Score);
    }

}

function updateBoards(moveObj)
{
    var row = moveObj.row;
    var column = moveObj.column;
    var attackersIndex = moveObj.attackersIndex;
    var attackResult = moveObj.attackResult;
    if(attackResult === "Quit" && Quit === false)
    {
        Quit = true;
        updatePageOnTechnicalGameEnd(attackersIndex);
    }
    else if(attackResult === "Quit" && Quit === true)
    {
        //do nothing
    }
    else
    {
        if(attackResult === "insertMine")
        {
            //do nothing
        }
        else
        {
            updateScore(moveObj.index0Score,moveObj.index1Score);
            var winGame = moveObj.isGameEnd;

            if (myIndex === attackersIndex) {
                updateAttackerBoards(row, column, attackersIndex, attackResult);
            } else {
                updateDefenderBoards(row, column, attackersIndex, attackResult);
            }

            if (winGame === true) {
                updatePageOnGameEnd(moveObj.winningPlayerIndex);
            }
        }

    }

}

function updatePageOnGameEnd(winningPlayerIndex) {
    var currentUserIsTheWinner = (winningPlayerIndex === myIndex);

    if (currentUserIsTheWinner) {
        // alert("You win the game!");

        swal("Good job!", "You win the game =]", "success").then((value) => { QuitGame(); });

    } else {
        // alert("You lose the game!");
        swal("We are sorry", "You lose the game =[", "error").then((value) => { QuitGame(); });

    }


}

function updateAttackerBoards(row, column, attackersIndex, attackResult) {

    var shipBoardSquare = $("#shipBoard td[row= '" + row + "'][col= '" + column + "']");
    var trackBoardSquare = $("#trackBoard td[row= '" + row + "'][col= '" + column + "']");

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

    var shipBoardSquare = $("#shipBoard td[row= '" + row + "'][col= '" + column + "']");
    var trackBoardSquare = $("#trackBoard td[row= '" + row + "'][col= '" + column + "']");

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

function updateCssClass(squareObject, className) {
    squareObject.attr('class', className);
}

//------ before move and after move actions sector
function BeforeMoveActions() {
    EnableTrackBoard();
}

function afterMoveActions() {
    DisableTrackBoard();
}

// Disable and enable button sector

function DisableTrackBoard() {
    $("#trackBoard td").unbind("click");
}

function EnableTrackBoard() {
    $(".trackBoardSquare").unbind("click");
    $(".trackBoardSquare").on("click", onTrackBoardSquareClickEventHandler);
}

//activate the timer calls after the page is loaded
$(function () {

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(serverRoutine, refreshRate);

    //Check if the user is still connected to the server
    setInterval(isUserConnectedRoutine, refreshRate);

});

function serverRoutine() {
    if (duringRoutine === false) {
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

function isUserConnectedRoutine() {
    $.ajax({
        url: '/isUserConnected',
        type: 'GET',
        success: function (isConnected) {
            if (isConnected === "false") {
                $.get('/goToFirstPage',function(data){
                    window.location.replace(data);
                });
            }
        }
    });
}

/*Mines*/

function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
}

function allowDrop(event){
    event.preventDefault();
}

function drop(event){
    event.preventDefault();

    var id = event.dataTransfer.getData("text");
    var sourceElement = document.getElementById(id);
    var className = sourceElement.className;
    //Change Target class to the source class
    event.target.className = className;
    //Remove source
    sourceElement.remove();

    var row = event.target.attributes['row'].value;
    var col = event.target.attributes['col'].value;
    ajaxInsertMine(row, col);
}

function ajaxInsertMine(row, col) {
    $.ajax({
        url: '/insertMine',
        type: 'POST',
        data: {"col": col, "row": row},
        dataType: "text",
        // success: function () {
        // }
    });
}

function drawElementOnDragEnter(event) {
    event.target.classList.add("onDragEnter");
}

function undrawElementOnDragEnd(event) {
    event.target.classList.remove("onDragEnter");
}

function QuitGame()
{
    document.forms['gameEndForm'].submit();

    // $.get('/redirectToLoby',function(data){
    //     window.location.replace(data);
    // });
}
