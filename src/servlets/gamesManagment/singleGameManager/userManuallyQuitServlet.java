package servlets.gamesManagment.singleGameManager;

import logic.GameEngine;
import logic.data.enums.AttackResult;
import logic.exceptions.NoShipAtPoisitionException;
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
import java.io.PrintWriter;

@WebServlet(name = "userManuallyQuitServlet", urlPatterns = "/userManuallyQuit")
public class userManuallyQuitServlet extends HttpServlet {
    private final String LOBY_PAGE_URL = "/pages/loby/loby.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //--- to decline the game people by 1
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        HttpSession session = request.getSession();
        int userIndex = (int) session.getAttribute("userIndex");
        String gameName = (String) session.getAttribute("gameName");
        GameEngine gameEngine = gamesManager.getGameEngineByGameName(gameName);

        String attackResult = "Quit";
        SquareStatusAfterMove squareStatus = new SquareStatusAfterMove(-1, -1, attackResult, userIndex);
        MoveUpdateVerifyer curMoveMoveUpdateVerifyer = null;
        if (userIndex == 0) {
            curMoveMoveUpdateVerifyer = MoveUpdateVerifyer.Playe0AttackedUpdateVerifyer(squareStatus);
        } else if (userIndex == 1) {
            curMoveMoveUpdateVerifyer = MoveUpdateVerifyer.Playe1AttackedUpdateVerifyer(squareStatus);
        }
        gamesManager.getGameByName(gameName).setMoveUpdateVerifyer(curMoveMoveUpdateVerifyer);


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