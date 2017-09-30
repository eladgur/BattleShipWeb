package servlets.gamesManagment;


import com.google.gson.Gson;
import logic.GameEngine;
import logic.data.PlayerData;
import servlets.signup.UsersManager;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "GamesInfoServlet", urlPatterns = "/gamesInfo")
public class gameInfoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            GamesManager GamesManager = ServletUtils.getGamesManager(getServletContext());
            String gameName = request.getParameter("gameName");
            PlayerData playerData = GamesManager.getGameEngineMap().get(gameName).getPlayerData();
            out.println("<p>Game Name: " + gameName + "</p>");
            //out.println("Game Uploader User Name: ");//TODO:add to upload game
            out.println("<p>Game Board Size: " + playerData.getBoardSize() + "</p>");
            out.println("<p>Game Kind: " + playerData.getGameType() + "</p>");
            out.println("<p>Number OF Players In Game: " + GamesManager.getNumberOfPlayersInSpecificGame(gameName) + "</p>");
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

