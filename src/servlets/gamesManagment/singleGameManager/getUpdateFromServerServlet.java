package servlets.gamesManagment.singleGameManager;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import logic.GameEngine;
import logic.GameStatus;
import logic.data.Ship;
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
import java.io.PrintWriter;

@WebServlet(name = "getUpdateFromServerServlt", urlPatterns = "/getUpdateFromServer")
public class getUpdateFromServerServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String check = request.getParameter("row");

        HttpSession session = request.getSession();
        int userIndex = (int) session.getAttribute("userIndex");
        String gameName = (String) session.getAttribute("gameName");
        MoveUpdateVerifyer curMoveUpdateVerifyer = gamesManager.getGameByName(gameName).getMoveUpdateVerifyer();
        try (PrintWriter out = response.getWriter()) {
            if (curMoveUpdateVerifyer != null) {
                if (userIndex == 0) {
                    curMoveUpdateVerifyer.index0 = true;
                } else if (userIndex == 1) {
                    curMoveUpdateVerifyer.index1 = true;
                }

                Gson gson = new Gson();

//                String json = gson.toJson(curMoveUpdateVerifyer.lastMove);
                JsonElement jsonElement = gson.toJsonTree(curMoveUpdateVerifyer.lastMove);
                Game game = gamesManager.getGameByName(gameName);
                updateScoreToJsonElement(jsonElement, game); //tamir
                updateGameEndStatusToJsonElement(jsonElement, game);
                String json = gson.toJson(jsonElement);

                // Update drown ship info (if needed)
//                Ship drownShip = curMoveUpdateVerifyer.lastMove.drownShip;
//                if (drownShip != null) {
//                    updateDrownInfo(jsonElement, drownShip);
//                }

                out.println(json);
            }
        }
    }

    private void updateDrownInfo(JsonElement jsonElement, Ship drownShip) {
        Position drownShipStartingPosition = drownShip.getStartPoint();
        jsonElement.getAsJsonObject().addProperty("drownShipStartingLocationX", String.valueOf(drownShipStartingPosition.getX()));
        jsonElement.getAsJsonObject().addProperty("drownShipStartingLocationY", String.valueOf(drownShipStartingPosition.getY()));
        jsonElement.getAsJsonObject().addProperty("drownShipDirection", String.valueOf(drownShip.getDirection()));
        jsonElement.getAsJsonObject().addProperty("drownShipLength", String.valueOf(drownShip.getLength()));
    }

    private void updateScoreToJsonElement(JsonElement jsonElement, Game game) {
        jsonElement.getAsJsonObject().addProperty("index0Score", game.getGameEngine().getPlayerData(0).getScore());
        jsonElement.getAsJsonObject().addProperty("index1Score", game.getGameEngine().getPlayerData(1).getScore());
    }

    private void updateGameEndStatusToJsonElement(JsonElement jsonElement, Game game) {
        GameEngine gameEngine = game.getGameEngine();
        boolean isGameEnd = (gameEngine.getStatus() == GameStatus.END);

        jsonElement.getAsJsonObject().addProperty("isGameEnd", isGameEnd);

        if (isGameEnd) {
            int winningPlayerIndex = gameEngine.getWinPlayerIndex();
            jsonElement.getAsJsonObject().addProperty("winningPlayerIndex", winningPlayerIndex);
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
