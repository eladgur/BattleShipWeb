package servlets.signup;

import com.google.gson.JsonObject;
import constants.Constants;

import utils.SessionUtils;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static constants.Constants.LOBY_PAGE_URL;
import static constants.Constants.USERNAME;

@WebServlet(name = "LoginServlet", urlPatterns = "/pages/signup/login")
public class LoginServlet extends HttpServlet {

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        boolean isSignUpSuccess = false;
        String usernameFromSession = SessionUtils.getUsername(request);
        UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());

        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null) {
                //This field is required at the browser, so it just in case that the browser didn't reqiored it
                isSignUpSuccess = false;
            } else {
                //Normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                if (usersManager.isUserExists(usernameFromParameter)) {
                    String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                    // Username already exists, mark the isSignUpSuccess boolean for notify the client
                    isSignUpSuccess = false;
                } else {
                    //Add the new user to the users list
                    usersManager.addUser(usernameFromParameter);
                    //Set the username in a session so it will be available on each request
                    //The true parameter means that if a session object does not exists yet
                    //Create a new one
                    request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                    //Redirect the request to the chat room - in order to actually change the URL
                    System.out.println("On login, request URI is: " + request.getRequestURI());
                    isSignUpSuccess = true;
                }
            }
        } else {
            // User is already logged in
            isSignUpSuccess = false;
        }

        // Create Json Object
        JsonObject json = new JsonObject();
        // Put some value pairs into the JSON object .
        json.addProperty("isSignUpSuccess", isSignUpSuccess);
        json.addProperty("url", LOBY_PAGE_URL);
        /* Return to javascript a boolean if Login success or not and finally output the json string*/
        writer.print(json.toString());
    }
}
