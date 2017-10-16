package servlets.gamesManagment.singleGameManager;

import logic.GameEngine;
import servlets.gamesManagment.Game;
import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;
import xmlInputManager.Position;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "insertMineServlet", urlPatterns = "/insertMine")
public class insertMineServlet extends HttpServlet {

    private final String LOBY_PAGE_URL = "/pages/loby/loby.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Get insert position
        int row = Integer.parseInt(request.getParameter("row"));
        int col = Integer.parseInt(request.getParameter("col"));
        Position insertPosition = new Position(row, col);
        //--- to decline the game people by 1
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        HttpSession session = request.getSession();
        int userIndex = (int) session.getAttribute("userIndex");
        String gameName = (String) session.getAttribute("gameName");
        GameEngine gameEngine = gamesManager.getGameEngineByGameName(gameName);
        Game game = gamesManager.getGameByName(gameName);
        //Update game logic

        gameEngine.insertMine(insertPosition );
        String attackResult = "insertMine";
        SquareStatusAfterMove squareStatus = new SquareStatusAfterMove(row, col, attackResult, userIndex);
        MoveUpdateVerifyer curMoveMoveUpdateVerifyer = MoveUpdateVerifyer.noUpdatedValues(squareStatus);
        game.insertMineToShipBoard(row, col, userIndex);
        game.setMoveUpdateVerifyer(curMoveMoveUpdateVerifyer);

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
}
