package servlets.gamesManagment.singleGameManager;

import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;




@WebServlet(name = "redirectToLobyServlet", urlPatterns = "/redirectToLoby")
public class redirectToLobyServlet extends HttpServlet {
    private final String LOBY_PAGE_URL = "/pages/loby/loby.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //--- to decline the game people by 1
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        int userIndex = (int) session.getAttribute("userIndex");
        String gameName = (String) session.getAttribute("gameName");
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String playerName = SessionUtils.getUsername(request);
        gamesManager.getGameByName(gameName).removeUserFromGame(playerName);
        try (PrintWriter out = response.getWriter()) {
            out.println(LOBY_PAGE_URL);
            out.flush();
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

}