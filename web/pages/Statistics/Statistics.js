window.onload = function () {
    $("#quitButtonFromStatistics").on("click",onStatisticsQuitButtonPresses);
};

function onStatisticsQuitButtonPresses(event)
{
    $.get('/redirectToLoby',function(data){
        window.location.replace(data);
    });

}