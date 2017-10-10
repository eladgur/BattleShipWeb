package servlets.signup;

import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "IsUserConnectedServlet ", urlPatterns = "/isUserConnected")
public class IsUserConnectedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isUserConnected = SessionUtils.isUserConnected(req);
        PrintWriter writer = resp.getWriter();

        writer.print(isUserConnected );
    }
}
