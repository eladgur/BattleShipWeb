var REDIRECT_TO_LOBY_URL = buildUrlWithContextPath("redirectToLoby")

window.onload = function () {
    $("#quitButtonFromStatistics").on("click",onStatisticsQuitButtonPresses);
};

function onStatisticsQuitButtonPresses(event)
{
    $.get(REDIRECT_TO_LOBY_URL,function(data){
        window.location.replace(data);
    });

}