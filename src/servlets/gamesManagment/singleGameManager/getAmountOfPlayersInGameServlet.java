package servlets.gamesManagment.singleGameManager;

import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.GAME_NAME;

@WebServlet(name = "getAmountOfPlayersInGameServlet", urlPatterns = "/getAmountOfPlayersInGame")
public class getAmountOfPlayersInGameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gameName = (String) req.getSession().getAttribute(GAME_NAME);
        int numOfPlayersInGame = ServletUtils.getGamesManager(getServletContext()).getGameByName(gameName).getAmountOfPlayers();
        PrintWriter writer = resp.getWriter();

        writer.print(numOfPlayersInGame);
    }
}
