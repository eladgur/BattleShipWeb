var myIndex = -1;
var isUpdated = false;
var currentIndex = -1;
var refreshRate = 100;
var duringRoutine = false;
var Quit = false;
var isGameStarted = false;
var gameName;
var isGameStartedRoutineTimer;
var USER_MANUALLY_QUIT_URL = buildUrlWithContextPath("userManuallyQuit");
var GET_INDEX_URL = buildUrlWithContextPath("getIndex");
var GET_AMOUNT_OF_PLAYERS_IN_GAME_URL = buildUrlWithContextPath("getAmountOfPlayersInGame");
var CURRENT_PLAYER_INDEX_URL = buildUrlWithContextPath("currentPlayerIndex");
var GET_UPDATE_FROM_SERVER_URL = buildUrlWithContextPath("getUpdateFromServer");
var IS_UPDATE_COMPLETED_URL = buildUrlWithContextPath("isUpdateCompleted");
var PLAYER_MOVE_URL= buildUrlWithContextPath("playerMove");
var INSERT_MINE_URL= buildUrlWithContextPath("insertMine");
var IS_USER_CONNECTED_URL = buildUrlWithContextPath("isUserConnected");
var END_GAME_URL = buildUrlWithContextPath("EndGame");


window.onload = function () {
    askServerMyIndex();
    DisableTrackBoard();
    $("#quitButton").on("click", onQuitCLick);
};

//activate the timer calls after the page is loaded
$(function () {

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(serverRoutine, refreshRate);

    //Check if the user is still connected to the server
    setInterval(isUserConnectedRoutine, refreshRate);

    //Check if the user is still connected to the server
    isGameStartedRoutineTimer =  setInterval(isGameStartedRoutine, refreshRate);
});

function isGameStartedRoutine() {
    $.ajax({
        url: GET_AMOUNT_OF_PLAYERS_IN_GAME_URL,
        type: 'GET',
        success: function (amountOfPlayersInGame) {
            if (amountOfPlayersInGame === "2") {
                isGameStarted = true;
                clearInterval(isGameStartedRoutineTimer);
            }
        }
    });
}

function userQuitManuallyActions() {
    $.ajax({
        url: USER_MANUALLY_QUIT_URL,
        type: 'GET',
        success: function () {
        },
    });
}

function onQuitCLick(event) {
    userQuitManuallyActions();
    swal("We are sorry", "You lose the game technically because you left game =[", "error").then((value) => {QuitGame();});
}

//----index actions-----------
function askServerMyIndex() {
    $.ajax({
        url: GET_INDEX_URL,
        type: 'GET',
        success: function (res) {
            // alert("your index is " + res);
            myIndex = parseInt(res);
        },
    });
}

function isCurrentIndexEqualToMyIndex() {
    var res = ( myIndex === currentIndex );

    return res;
}

//-------------------------------------

function askServerCurrentIndex() {
    $.ajax({
        url: CURRENT_PLAYER_INDEX_URL,
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
        url: GET_UPDATE_FROM_SERVER_URL,
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
        url: IS_UPDATE_COMPLETED_URL,
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
        url: PLAYER_MOVE_URL,
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
    if (attackersIndex === myIndex) {
        swal("We are sorry", "You lose the game technically because you left game =[", "error").then((value) => {QuitGame();});
    }
    else {
        swal("Your Opoonent ran away!", "You win the game =]", "success").then((value) => {QuitGame();});
    }
}

function updateScore(index0Score, index1Score) {
    if (myIndex == 0) {
        $("#scoreHolder").text(index0Score);
    }
    else if (myIndex == 1) {
        $("#scoreHolder").text(index1Score);
    }
}

function updateBoards(moveObj) {
    var row = moveObj.row;
    var column = moveObj.column;
    var attackersIndex = moveObj.attackersIndex;
    var attackResult = moveObj.attackResult;

    if (attackResult === "Quit" && Quit === false) {
        Quit = true;
        updatePageOnTechnicalGameEnd(attackersIndex);
    }
    else if (attackResult === "Quit" && Quit === true) {
        //do nothing
    }
    else {
        updateScore(moveObj.index0Score, moveObj.index1Score);
        var winGame = moveObj.isGameEnd;

        if (myIndex === attackersIndex) {
            updateAttackerBoards(row, column, attackersIndex, attackResult, moveObj);
        } else {
            updateDefenderBoards(row, column, attackersIndex, attackResult, moveObj);
        }

        if (winGame === true) {
            updatePageOnGameEnd(moveObj.winningPlayerIndex);
        }
    }

}

function updatePageOnGameEnd(winningPlayerIndex) {
    var currentUserIsTheWinner = (winningPlayerIndex === myIndex);

    if (currentUserIsTheWinner) {
        // alert("You win the game!");
        swal("Good job!", "You win the game =]", "success").then((value) => {QuitGame();});
    } else {
        // alert("You lose the game!");
        swal("We are sorry", "You lose the game =[", "error").then((value) => {QuitGame();});
    }


}

function updateAttackerBoards(row, column, attackersIndex, attackResult, moveObj) {

    var shipBoardSquare = $("#shipBoard td[row= '" + row + "'][col= '" + column + "']");
    var trackBoardSquare = $("#trackBoard td[row= '" + row + "'][col= '" + column + "']");

    if (attackResult === "SHIPHIT") {
        updateCssClass(trackBoardSquare, "hit");
    }
    else if (attackResult == "SHIPDROWNHIT") {
        markDrownShip(moveObj, $("#trackBoard"));
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
        markDrownShip(moveObj, $("#shipBoard"));
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

function updateDefenderBoards(row, column, attackersIndex, attackResult, moveObj) {

    var shipBoardSquare = $("#shipBoard td[row= '" + row + "'][col= '" + column + "']");
    var trackBoardSquare = $("#trackBoard td[row= '" + row + "'][col= '" + column + "']");

    if (attackResult === "SHIPHIT") {
        updateCssClass(shipBoardSquare, "hit");
    }
    else if (attackResult == "SHIPDROWNHIT") {
        markDrownShip(moveObj, $("#shipBoard"));
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
    EnableMineDrag();
}

function EnableTrackBoard() {
    $(".trackBoardSquare").unbind("click");
    $(".trackBoardSquare").on("click", onTrackBoardSquareClickEventHandler);
}

function EnableMineDrag() {
    $(".minesDiv img").attr('draggable', 'True');
}

function afterMoveActions() {
    DisableTrackBoard();
    disableMineDrag();
}

function DisableTrackBoard() {
    $("#trackBoard td").unbind("click");
}

function disableMineDrag() {
    $(".minesDiv img").attr('draggable', 'False');
}

// Disable and enable button sector

function serverRoutine() {
    if (isGameStarted) {
        if (duringRoutine === false) {
            duringRoutine = true;
            isUpdateCompleted();
            if (isUpdated === true) {
                askServerCurrentIndex();
                if (isCurrentIndexEqualToMyIndex()) {
                    BeforeMoveActions();
                    updateWhoseTurn(true);
                }
                else {
                    afterMoveActions();
                    updateWhoseTurn(false);
                }
            }
            else {
                getUpdateFromServer();
                afterMoveActions();
            }

            duringRoutine = false;
        }
    }
}

function updateWhoseTurn(hisTurn) {
    var element = $('#whosTurn');

    if (hisTurn) {
        element.text("your turn");
        element.removeClass("notCorrentTurn");
        element.addClass("correntTurn");
    }
    else {
        element.text("Not your turn");
        element.removeClass("correntTurn");
        element.addClass("notCorrentTurn");
    }
}

function isUserConnectedRoutine() {
    $.ajax({
        url: IS_USER_CONNECTED_URL,
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

/*Mines*/

function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
}

function allowDrop(event) {
    event.preventDefault();
}

function drop(event) {
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
        url: INSERT_MINE_URL,
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

function QuitGame() {
    $("#gameEndForm").attr('action', END_GAME_URL);
    document.forms['gameEndForm'].submit();

    // $.get('/redirectToLoby',function(data){
    //     window.location.replace(data);
    // });
}

//TODO: Drown Ship ( Taken from java )

function markDrownShip(shipInfoObject, board) {
    var drownShip = shipInfoObject.drownShip;
    var row = drownShip.position.x,
        column = drownShip.position.y,
        shipLength = drownShip.length,
        shipDirection = drownShip.direction;

    switch (shipDirection) {
        case "ROW":
            setShipButtonOnShipDrownInRow(row, column, shipLength, board);
            break;
        case "COLUMN":
            setShipButtonOnShipDrownInColumn(row, column, shipLength, board);
            break;
        case "DOWN_RIGHT":
            setShipButtonOnShipDrownInColumn(row - shipLength + 1, column, shipLength, board);
            setShipButtonOnShipDrownInRow(row, column, shipLength, board);
            break;
        case "UP_RIGHT":
            setShipButtonOnShipDrownInColumn(row, column, shipLength, board);
            setShipButtonOnShipDrownInRow(row, column, shipLength, board);
            break;
        case "RIGHT_UP":
            setShipButtonOnShipDrownInRow(row, column - shipLength + 1, shipLength, board);
            setShipButtonOnShipDrownInColumn(row - shipLength + 1, column, shipLength, board);
            break;
        case "RIGHT_DOWN":
            setShipButtonOnShipDrownInRow(row, column - shipLength + 1, shipLength, board);
            setShipButtonOnShipDrownInColumn(row, column, shipLength, board);
            break;
    }
}

function setShipButtonOnShipDrownInRow(row, firstColumn, shipLength, board) {
    for (var column = firstColumn; column < firstColumn + shipLength; column++) {
        var boardSquare = board.find("td[row= '" + row + "'][col= '" + column + "']");
        updateCssClass(boardSquare, "drownShip");
    }
}

function setShipButtonOnShipDrownInColumn(firstRow, column, shipLength, board) {
    for (var row = firstRow; row < firstRow + shipLength; row++) {
        var boardSquare = board.find("td[row= '" + row + "'][col= '" + column + "']");
        updateCssClass(boardSquare, "drownShip");
    }
}