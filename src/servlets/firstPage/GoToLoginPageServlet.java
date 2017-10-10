package servlets.firstPage;

import constants.Constants;

import static constants.Constants.*;

import servlets.signup.UsersManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static constants.Constants.USERNAME;

@WebServlet(name = "GoToLoginPageServlet", urlPatterns = "/goToLoginPage")
public class GoToLoginPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        boolean isUserConnected = SessionUtils.isUserConnected(request);

        if (isUserConnected) {
            response.sendRedirect(LOBY_PAGE_URL);
        } else {
            response.sendRedirect(SIGN_UP_URL);
        }
    }
}
