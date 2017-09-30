var waitingForSync = false;

window.onload = function () {
    var cells = document.getElementsByTagName('td');
    for (var i = 0; i < cells.length; i++) {
        var cell = cells[i];
        cell.onclick = onTrackBoardSquareClickEventHandler;
    }
};

//Declaration of a function that will be called on mouse clicks
function onTrackBoardSquareClickEventHandler(event) {

    if (!waitingForSync) {
        // document.getElementById('button').value = event.button;
        var clickedSquare = event.target;
        clickedSquare.classList.add('clickedSquare');
        clickedSquare.onclick = null;
        //Get row and cul from cell
        var row = event.currentTarget.attributes['row'].value;
        var col = event.currentTarget.attributes['col'].value;
        //Put row and col on form
        document.getElementById('form_row').value = row;
        document.getElementById('form_col').value = col;
        // document.forms['clickform'].submit(); //TODO: To use ajax intead
        var formData = new FormData(document.forms['clickform']);

        $.ajax({
            url: '/playerMove',
            type: 'POST',
            data: formData,
        });
    }
}

function sync() {
    $.ajax({
        url: '/sync',
        type: 'GET',
        success: function (squareStatusAfterMove) {

        }
    });
}