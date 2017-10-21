package servlets.gamesManagment.singleGameManager;

import logic.GameEngine;
import logic.data.ShipBoard;
import logic.data.enums.ShipBoardSquareValue;
import servlets.gamesManagment.Game;
import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import xmlInputManager.Position;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;


@WebServlet(name = "EndGameServlet", urlPatterns = {"/EndGame"})
public class EndGameServlet extends HttpServlet {

    private static final String GAME_NAME = "gameName";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String gameName = (String) session.getAttribute(GAME_NAME);
        GameEngine gameEngine = getGameEngineByGameName(gameName);
        Game game = ServletUtils.getGamesManager(getServletContext()).getGameByName(gameName);
        int amountOfPlayersInGame = game.getAmountOfPlayers();

        if (amountOfPlayersInGame < 2) {
            response.sendRedirect(request.getContextPath() + LOBY_PAGE_URL);
            game.removeUserFromGame(SessionUtils.getUsername(request));
        } else {
            game.setGameAsInEndingProccess();
            generateStatisticsPage(request, response, gameName, gameEngine, game);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void generateStatisticsPage(HttpServletRequest request, HttpServletResponse response, String gameName, GameEngine gameEngine, Game game) throws IOException {
        try {
            writePageToClient(request, response, gameName, gameEngine.getGameInfo().getBoardSize(), gameEngine, game);
        } catch (Exception e) { // Game is full
            response.setStatus(500);
            PrintWriter writer = response.getWriter();
            writer.print(e.getMessage() + "there was a Problem");
        }
    }

    private void writePageToClient(HttpServletRequest request, HttpServletResponse response, String gameName, int boardSize,
                                   GameEngine gameEngine, Game game) throws IOException {
        int numOfMines = gameEngine.getGameInfo().getMineAmount();

        response.setContentType("text/html;charset=UTF-8"); //tamir added request param

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>End Game Statistics</title>");
            out.println("<link rel='stylesheet' type='text/css' href='pages/gamePage/gamePage.css' />");
            out.println("<link rel='stylesheet' type='text/css' href='pages/Statistics/Statistics.css' />");
            out.println("<script src='lib/jquery-3.2.1.min.js' type='text/javascript'></script>");
            out.println("<script src='lib/context-path-helper.js' type='text/javascript'></script>");

            out.println("<script src='pages/Statistics/Statistics.js' type='text/javascript'></script>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div id='upperContainer'>");
            //Generate Quit Button
            generateQuitButton(out);
            //Generate Table
            generateStatisticsTable(request, gameName, out, gameEngine, game);
            out.println("</div>");
            out.println("<div id='boardsContainer'>");
            generateShipBoards(gameName, boardSize, gameEngine, out, game);
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void generateShipBoards(String gameName, int boardSize, GameEngine gameEngine, PrintWriter out, Game game) {
        String playerName0 = getPlayerNameByIndex(0, gameName);
        String playerName1 = getPlayerNameByIndex(1, gameName);
        String[][] player0ShipBoard = game.getUser0ShipBoard();
        String[][] player1ShipBoard = game.getUser1ShipBoard();


        generateShipBoard(boardSize, out, playerName0, player0ShipBoard);
        generateShipBoard(boardSize, out, playerName1, player1ShipBoard);
    }

    private void generateQuitButton(PrintWriter out) {
        out.println("<div id='quiteButtonDiv'>");
        out.println("<button id='quitButtonFromStatistics' type=\"button\"> QuitGame</button>");
        out.println("</div> ");
    }

    private void generateStatisticsTable(HttpServletRequest request, String gameName, PrintWriter out, GameEngine gameEngine, Game game) {
        out.println("<div id='scoreTableDiv'>");
        out.println("<table class='tg'>");
        out.println("<tr>");
        out.println("<th class='tg-yw4l'>Player</th>");
        out.println("<th class='tg-yw4l'>Num of turns</th>");
        out.println("<th class='tg-yw4l'>Total Time From Game Begining</th>");
        out.println("<th class='tg-yw4l'>Score</th>");
        out.println("<th class='tg-yw4l'>Hits</th>");
        out.println("<th class='tg-yw4l'>Misses</th>");
        out.println("<th class='tg-yw4l'>Average Move time</th>");
        out.println("</tr>");
        playerDetailsInTr(out, 0, gameEngine, gameName, game);
        playerDetailsInTr(out, 1, gameEngine, gameName, game);
        out.println("</table>");
        out.println("</div>");
    }

    private void playerDetailsInTr(PrintWriter out, int playerIndex, GameEngine gameEngine, String gameName, Game game) {
        String Time = getTotalTimeFromGameBegin(game);

        out.println("<tr>");
        out.println("<td id='player" + playerIndex + "'class='tg-yzt1'>" + getPlayerNameByIndex(playerIndex, gameName) + "</td>");
        out.println("<td id='numOfTurns" + playerIndex + "'class='tg-yzt1'>" + gameEngine.getPlayerData(0).getTurnsCounter() + "</td>");
        out.println("<td id='TimeFromBegin" + playerIndex + "' class='tg-yzt1'>" + Time + "</td>");
        out.println("<td id='score" + playerIndex + "' class='tg-yzt1'>" + gameEngine.getPlayerData(playerIndex).getScore() + "</td>");
        out.println("<td id='hits" + playerIndex + "' class='tg-yzt1'>" + gameEngine.getPlayerData(playerIndex).getNumOfHits() + "</td>");
        out.println("<td id='Misses" + playerIndex + "' class='tg-yzt1'>" + gameEngine.getPlayerData(playerIndex).getNumOfMisses() + "</td>");
        out.println("<td id='AveragemoveTime" + playerIndex + "' class='tg-yzt1'>" + gameEngine.getPlayerData(playerIndex).getAvgAttackTime() + "</td>");
        out.println("</tr>");
    }

    private String getTotalTimeFromGameBegin(Game game) {
        String totalTimeFromGameBegin = game.getEndTime();

        return totalTimeFromGameBegin;
    }

    private GameEngine getGameEngineByGameName(String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        GameEngine gameEngine = gamesManager.getGameEngineByGameName(gameName);

        return gameEngine;
    }

    private String getPlayerNameByIndex(int index, String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        Game game = gamesManager.getGameByName(gameName);
        return game.GetPlayerNameByIndex(index);
    }

    private void generateTrackBoard(int boardSize, PrintWriter out, GameEngine gameEngine) {
        out.println("<div class='divBoard'>");
        out.println("<h2 class='boardHeadline'>TrackBoard<h2>");
        //oncontextmenu=\"return false\" - causes the right click menu to not open
        out.println("<table class='board' id='trackBoard' oncontextmenu=\"return false\">");
        String className = "trackBoardSquare";
        for (int row = 0; row < boardSize; row++) {
            out.println("<tr>");
            for (int col = 0; col < boardSize; col++) {
                out.println("<td class= '" + className + "'" + "' row='" + row + "' col='" + col + "'>");
                out.println("</td>");
            }
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("</div>");
    }

    public int getUserIndex(String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        int amountOfPlayerInGame = gamesManager.getNumberOfPlayersInSpecificGame(gameName);
        int userIndex = amountOfPlayerInGame - 1;

        return userIndex;
    }

    private void generateShipBoard(int boardSize, PrintWriter out, String playerName, String[][] shipBoard) {
        out.println("<div class='divBoard'>");
        out.println("<h2 class='boardHeadline'>" + playerName + " ShipBoard</h2>");
        //oncontextmenu=\"return false\" - causes the right click menu to not open
        out.println("<table class='board' id='shipBoard' oncontextmenu=\"return false\">");
        for (int row = 0; row < boardSize; row++) {
            out.println("<tr>");
            for (int col = 0; col < boardSize; col++) {
                String className = shipBoard[row][col];
                if (className == "ship") {
                    out.println("<td class= '" + className + "' row='" + row + "' col='" + col + "'>");
                } else {
                    out.println("<td ondragover=\"allowDrop(event)\" ondrop=\"drop(event)\" ondragenter=\"drawElementOnDragEnter(event)\" ondragleave=\"undrawElementOnDragEnd(event)\"" +
                            " class= '" + className + "' row='" + row + "' col='" + col + "'>");
                }
                out.println("</td>");
            }
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("</div>");
    }

    private String generateShipBoardClassNameFromShipBoard(ShipBoard shipBoard, int row, int col) {
        Position position = new Position(row, col);
        ShipBoardSquareValue shipBoardSquareValue = shipBoard.getShipBoardSquareValue(position);
        String className;

        switch (shipBoardSquareValue) {
            case WATER:
                className = "water";
                break;
            case SHIP:
                className = "ship";
                break;
            case MINE:
                className = "mine";
                break;
            default:
                className = "water";
                break;
        }

        return className;
    }

}