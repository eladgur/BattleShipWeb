package servlets.fileUpload;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import logic.GameEngine;
import logic.exceptions.LogicallyInvalidXmlInputException;
import xmlInputManager.GameInfo;
import xmlInputManager.InvalidXmFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import static xmlInputManager.XmlReader.getDataFromXml;

@WebServlet(name = "fileUploadServlet", urlPatterns = "/upload")
@MultipartConfig(location = "C:/uploads", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
//This is mandatory for using the Parts() function for reciving the file!
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fileupload/form.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String fileName = saveFileFromRequest(request, response);
        String filePath = "c:\\uploads\\" + fileName;
        try {
            GameInfo gameInfo = getDataFromXml(filePath);
            GameEngine gameEngine = new GameEngine();
            gameEngine.loadAndValidateGameInfo(gameInfo);
        } catch (InvalidXmFormatException | LogicallyInvalidXmlInputException e) {
            response.setStatus(500);
        }
    }

    private String saveFileFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        Part xmlFile = request.getPart("xmlFile");
        String disposition = xmlFile.getHeader("Content-Disposition");
        String fileName = disposition.replaceFirst("(?i)^.*filename=\"([^\"]+)\".*$", "$1");

        //to write the content of the file to an actual file in the system (will be created at c:\samplefile)
        xmlFile.write(fileName);

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
}