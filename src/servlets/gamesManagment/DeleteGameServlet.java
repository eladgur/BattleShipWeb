package servlets.gamesManagment;

import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DeleteGameServlet ", urlPatterns = "/deleteGame")
public class DeleteGameServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        boolean isDeleted = false;
        ServletContext servletContext = getServletContext();
        GamesManager gamesManager = ServletUtils.getGamesManager(servletContext);
        String gameName = req.getParameter("gameName");
        Game game = gamesManager.getGameByName(gameName);
        int amountOfPlayersInGame = game.getAmountOfPlayers();
        PrintWriter writer = resp.getWriter();

        //Delete the Game
        if (amountOfPlayersInGame == 0 && userIsTheGameUploader(game.getUploaderName(), req)) {
            gamesManager.removeGame(gameName);
            isDeleted = true;
        }

        writer.print(isDeleted);
    }

    private boolean userIsTheGameUploader(String uploaderName, HttpServletRequest req) {
        String userName = SessionUtils.getUsername(req);
        boolean userIsTheUploader = (userName == uploaderName);

        return userIsTheUploader;
    }
}
