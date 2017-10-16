package servlets.gameServlet;

import logic.GameEngine;
import logic.data.ShipBoard;
import logic.data.enums.ShipBoardSquareValue;
import servlets.gamesManagment.Game;
import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import xmlInputManager.Position;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static constants.Constants.GAME_NAME;

@WebServlet(name = "GameServlet", urlPatterns = {"/gamePage"})
public class GameServlet extends HttpServlet {

    private static final int BOARD_SIZE = 10;
    private static final String ROW_PARAMETER = "row";
    private static final String COL_PARAMETER = "col";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameName = request.getParameter(GAME_NAME);
        GameEngine gameEngine = getGameEngineByGameName(gameName);
        int boardSize = gameEngine.getPlayerData().getBoardSize();
        String userName = SessionUtils.getUsername(request);
        storeGameNameOnSession(gameName, request);
        Game game = ServletUtils.getGamesManager(getServletContext()).getGameByName(gameName);
        PrintWriter writer = response.getWriter();

        try {
            addUserToGame(gameName, userName);
            if (game.getAmountOfPlayers() == 2) {
                gameEngine.startGame();
            }
            int userIndexInGame = getUserIndex(gameName);
            storeUserIndexInSession(userIndexInGame, request.getSession());
            writePageToClient(request, response, boardSize, gameEngine, userIndexInGame);
        } catch (Game.GameFullException e) { // Game is full
            response.setStatus(500);
            writer.print(e.getMessage() + " Please try other game");
        }
    }

    private void storeGameNameOnSession(String gameName, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("gameName", gameName);
    }

    private void addUserToGame(String gameName, String userName) throws Game.GameFullException {

        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        gamesManager.addUserToGame(gameName, userName);
    }

    private void storeUserIndexInSession(int userIndex, HttpSession session) {
        session.setAttribute("userIndex", userIndex);
    }

    private void writePageToClient(HttpServletRequest request, HttpServletResponse response, int boardSize,
                                   GameEngine gameEngine, int userIndexInGame) throws IOException {
        int numOfMines = gameEngine.getGameInfo().getMineAmount();
        response.setContentType("text/html;charset=UTF-8"); //tamir added request param


        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Game Page</title>");
            out.println("<link rel='stylesheet' type='text/css' href='/pages/gamePage/gamePage.css' />");
            out.println("<script src='/lib/jquery-3.2.1.min.js' type='text/javascript'></script>");
            out.println("<script src='/pages/gamePage/gamePage.js' type='text/javascript'></script>");
//            out.println("<script src='https://unpkg.com/sweetalert/dist/sweetalert.min.js' type='text/javascript'></script>");
            out.println("<script src=\"/lib/SweetAlert/sweetalert2.all.min.js\" type='text/javascript'></script>");
            out.println("<link rel=\"stylesheet\" href=\"/lib/SweetAlert/sweetalert2.min.css\">");
            out.println("</head>");
            out.println("<body>");

            out.println("<div id='upperContainer'>");
            //Generate Quit Button
            generateQuitButton(out);
            //Generate Table
            generateTable(request, out);
            out.println("</div>");

            out.println("<div id='boardsContainer'>");
            //Generate Boards
            generateShipBoard(boardSize, out, gameEngine, userIndexInGame);
            generateTrackBoard(boardSize, out, gameEngine);
            generateMines(out, numOfMines);
            out.println("</div>");
            generateHiddenClickForm(out);
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void generateTable(HttpServletRequest request, PrintWriter out) {
        out.println("<div id='scoreTableDiv'>");
        out.println("<table class='tg'>");
        out.println("<tr>");
        out.println("<th class='tg-yw4l'>SCORE</th>");
        out.println("<th class='tg-yw4l'>NAME</th>");
        out.println("<th class='tg-yw4l'>IS IT YOUR TURN</th>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td class='tg-yzt1' id='scoreHolder'>0</td>");
        out.println("<td class='tg-yzt1' id='nameHolder'>" + SessionUtils.getUsername(request) + "</td>");
        out.println("<td class='tg-yzt1' id='turnHolder'><p id='whosTurn'>Wait For Other Player</p></td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</div>");
    }

    private void generateMines(PrintWriter out, int numOfMines) {
        out.println("<div class=\"minesDiv\">\n");
        for (int i = 0; i < numOfMines; i++) {
            out.println("<img src='/resources/images/mine.png' id=\"dragSource" + i + "\" class=\"mine\" draggable=\"true\" ondragstart=\"drag(event)\" alt=\"mine" + i + "\" height=\"22\" width=\"22\">");
        }
        out.println("</div>");
    }

    private void generateHiddenClickForm(PrintWriter out) {
        out.println("<form id='clickform' method='post' action='click'>");
        out.println("<input id='form_col' type='hidden' name='" + COL_PARAMETER + "'/>");
        out.println("<input id='form_row' type='hidden' name='" + ROW_PARAMETER + "'/>");
        out.println("<input id='button'   type='hidden' name='button'/>");
        out.println("</form>");

        out.println("<form id='gameEndForm' method='get' action='/EndGame'>");
        out.println("<input id='button'   type='hidden' name='button'/>");
        out.println("</form>");
    }

    private void generateQuitButton(PrintWriter out) {
        out.println("<div id='quiteButtonDiv'>");
        out.println("<button id='quitButton' type=\"button\"> QuitGame</button>");
        out.println("</div> ");
    }

    private GameEngine getGameEngineByGameName(String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        GameEngine gameEngine = gamesManager.getGameEngineByGameName(gameName);

        return gameEngine;
    }

    private void generateShipBoard(int boardSize, PrintWriter out, GameEngine gameEngine, int userIndexInGame) {
        ShipBoard shipBoard = gameEngine.getPlayerData(userIndexInGame).getShipBoard();

        out.println("<div class='divBoard'>");
        out.println("<h2 class='boardHeadline'>ShipBoard</h2>");
        //oncontextmenu=\"return false\" - causes the right click menu to not open
        out.println("<table class='board' id='shipBoard' oncontextmenu=\"return false\">");
        for (int row = 0; row < boardSize; row++) {
            out.println("<tr>");
            for (int col = 0; col < boardSize; col++) {
                String className = generateShipBoardClassNameFromShipBoard(shipBoard, row, col);
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

    public static String generateShipBoardClassNameFromShipBoard(ShipBoard shipBoard, int row, int col) {
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

    public int getUserIndex(String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        int amountOfPlayerInGame = gamesManager.getNumberOfPlayersInSpecificGame(gameName);
        int userIndex = amountOfPlayerInGame - 1;

        return userIndex;
    }
}