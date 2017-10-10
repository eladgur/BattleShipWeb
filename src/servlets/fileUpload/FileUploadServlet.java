package servlets.fileUpload;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import constants.Constants;
import logic.GameEngine;
import logic.exceptions.LogicallyInvalidXmlInputException;
import servlets.gamesManagment.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import xmlInputManager.GameInfo;
import xmlInputManager.InvalidXmFormatException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import static constants.Constants.UPLOAD_DIR;
import static constants.Constants.XML_FILE_EXTENSTION;
import static xmlInputManager.XmlReader.getDataFromXml;

@WebServlet(name = "fileUploadServlet", urlPatterns = "/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
//This is mandatory for using the Parts() function for reciving the file!
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fileupload/form.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gets absolute path of the web application
        String uploadDirectoryPath = buildUploadDirectoryPath(getServletContext());

        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadDirectoryPath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }

        String gameName = request.getParameter("gameName");
        String uploaderName = SessionUtils.getUsername(request);
        response.setContentType("text/html");
        saveFileFromRequest(request, gameName, uploadDirectoryPath);
        // Create Complete file path for reading the file
        String filePath = buildGameFilePathFromGameName(gameName, getServletContext());
        PrintWriter writer = response.getWriter();

        try {
            if (!isGameNameExist(gameName)) {
                GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
                createNewGame(gameName, uploaderName, filePath, gamesManager);
            } else {
                writer.print("File Name allready exist");
                response.setStatus(500);
            }
        } catch (InvalidXmFormatException | LogicallyInvalidXmlInputException e) {
            writer.print(e.getMessage());
            response.setStatus(500);
        }
    }

    public static String buildGameFilePathFromGameName(String gameName, ServletContext servletContext) {
        return buildUploadDirectoryPath(servletContext) + File.separator + gameName + XML_FILE_EXTENSTION;
    }

    public static String buildUploadDirectoryPath(ServletContext servletContext) {
        String applicationPath = servletContext.getRealPath("");
        // constructs path of the directory to save uploaded file
        return applicationPath + File.separator + UPLOAD_DIR;
    }

    public static void createNewGame(String gameName, String uploaderName, String filePath, GamesManager gamesManager) throws
            InvalidXmFormatException, LogicallyInvalidXmlInputException {
        GameInfo gameInfo = getDataFromXml(filePath);
        GameEngine gameEngine = new GameEngine();
        gameEngine.loadAndValidateGameInfo(gameInfo);
        addGameToDataBase(gameName, gameEngine, uploaderName, gamesManager);
    }

    private String saveFileFromRequest(HttpServletRequest request, String gameName, String uploadFilePath) throws IOException, ServletException {
        Part xmlFile = request.getPart("xmlFile");
        String disposition = xmlFile.getHeader("Content-Disposition");
        String fileName = gameName + ".xml";
        //to write the content of the file to an actual file in the system
        xmlFile.write(uploadFilePath + File.separator + fileName);

        return fileName;
    }

    private void printPart(Part part, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>")
                .append("Parameter Name (From html form): ").append(part.getName())
                .append("<br>")
                .append("Content Type (of the file): ").append(part.getContentType())
                .append("<br>")
                .append("Size (of the file): ").append(part.getSize())
                .append("<br>");
        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("<br>");
        }
        sb.append("</p>");
        out.println(sb.toString());

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("<h2>File content:</h2>");
        out.println("<textarea style=\"width:100%;height:400px\">");
        out.println(content);
        out.println("</textarea>");
    }

    private static void addGameToDataBase(String gameName, GameEngine gameEngineToAdd, String uploaderName, GamesManager gamesManager) {
        gamesManager.addGame(gameName, gameEngineToAdd, uploaderName);
    }

    private boolean isGameNameExist(String gameName) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

        return gamesManager.isGameExist(gameName);
    }
}