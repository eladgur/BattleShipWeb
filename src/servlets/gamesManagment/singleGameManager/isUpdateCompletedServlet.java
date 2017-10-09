package servlets.gamesManagment.singleGameManager;

import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "isUpdateCompletedServlet", urlPatterns = "/isUpdateCompleted")
public class isUpdateCompletedServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            HttpSession session = request.getSession();
            String gameName = (String) session.getAttribute("gameName");
            MoveUpdateVerifyer curMoveUpdateVerifyer = gamesManager.getGameByName(gameName).getMoveUpdateVerifyer();
            String res;
            if(curMoveUpdateVerifyer == null || curMoveUpdateVerifyer.isBothPlayerRecivedUpdate())
            {
                res =   "true" ;
            }
            else
            {
                res = "false";
            }
            //res = gamesManager.getGameByName(request.getParameter(Constants.GAME_NAME)).getMoveUpdateVerifyer() == null ? "true" : "false";
            out.print(res);
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
