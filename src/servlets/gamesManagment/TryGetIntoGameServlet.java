package servlets.gamesManagment;

import com.google.gson.JsonObject;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.GAME_NAME;
import static constants.Constants.GAME_PAGE;
import static constants.Constants.LOBY_PAGE_URL;

@WebServlet(name = "TryGetIntoGameServlet", urlPatterns = "/tryGetIntoGame")
public class TryGetIntoGameServlet extends HttpServlet {

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String gameName = request.getParameter(GAME_NAME);
        Game game = ServletUtils.getGamesManager(getServletContext()).getGameByName(gameName);
        JsonObject json = new JsonObject();
        PrintWriter writer = response.getWriter();
        boolean isGameFull = !(game.isGameNotFull());

        /* return to javascript a boolean if the game is already full*/
        json.addProperty("isGameFull", isGameFull);
        json.addProperty(GAME_NAME, gameName);

        if (!isGameFull) {
            json.addProperty("url", GAME_PAGE);
        }

        writer.print(json.toString());
    }
}
