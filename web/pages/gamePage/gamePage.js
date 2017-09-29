window.onload = function(){
    var cells = document.getElementsByTagName('td');
    for (var i = 0 ; i < cells.length ; i++){
        var cell = cells[i];
        cell.onclick = myclick;
    }
};

//declaration of a function that will be called on mouse clicks
function myclick (event) {
    // var row = event.currentTarget.attributes['row'].value;
    // var col = event.currentTarget.attributes['col'].value;
    // document.getElementById('form_row').value = row;
    // document.getElementById('form_col').value = col;
    // document.getElementById('button').value = event.button;
    // document.forms['clickform'].submit(); //TODO: To use ajax intead
    var clickedSquare = event.target;

    clickedSquare.classList.add('clickedSquare');
    clickedSquare.onclick = null;
}
