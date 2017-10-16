package servlets.gamesManagment;

import logic.exceptions.LogicallyInvalidXmlInputException;
import servlets.fileUpload.FileUploadServlet;
import utils.ServletUtils;
import utils.SessionUtils;
import xmlInputManager.InvalidXmFormatException;

import static constants.Constants.UPLOAD_DIR;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "resetGameServlet", urlPatterns = "/resetGame")
public class resetGameServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gameName = req.getParameter("gameName");
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

        gamesManager.resetGame(gameName, getServletContext());
    }

}
