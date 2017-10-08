package servlets.gamesManagment.singleGameManager;

import com.google.gson.Gson;
import logic.GameEngine;
import logic.data.Constants;
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

@WebServlet(name = "PlayerMoveServlet", urlPatterns = "/playerMove")
public class PlayerMoveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        playMove(req, resp);
    }

    private void playMove(HttpServletRequest req ,HttpServletResponse resp) throws IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String check = req.getParameter("row");
        int row = Integer.parseInt(req.getParameter("row"));
        int col = Integer.parseInt(req.getParameter("col"));
        HttpSession session = req.getSession();
        int userIndex = (int) session.getAttribute("userIndex");
        String gameName = (String) session.getAttribute("gameName");
        GameEngine gameEngine = gamesManager.getGameEngineByGameName(gameName);
        Position positionToAttack = new Position(row, col);

        try {
            AttackResult attackResult = gameEngine.attackPosition(positionToAttack, false);
            SquareStatusAfterMove squareStatus = new SquareStatusAfterMove(row, col, attackResult,userIndex);
            UpdateVerifyer curMoveUpdateVerifyer= null;
            curMoveUpdateVerifyer =  UpdateVerifyer.noUpdatedValues(squareStatus);
            gamesManager.getGameByName(gameName).setUpdateVerifyer(curMoveUpdateVerifyer);
            //---for debugging
           Gson gson = new Gson();
            String json = gson.toJson(curMoveUpdateVerifyer.lastMove);
            PrintWriter writer = resp.getWriter();
            writer.println(json);
            //------------
        } catch (NoShipAtPoisitionException | CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
