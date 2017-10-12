package utils;

import com.google.gson.JsonObject;
import logic.GameEngine;
import logic.exceptions.LogicallyInvalidXmlInputException;
import servlets.fileUpload.FileUploadServlet;
import servlets.gamesManagment.GamesManager;
import servlets.signup.UsersManager;
import xmlInputManager.InvalidXmFormatException;

import javax.jms.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static constants.Constants.INT_PARAMETER_ERROR;
import static constants.Constants.LOBY_PAGE_URL;
import static constants.Constants.UPLOAD_DIR;

public class ServletUtils {

    private static final String USERS_MANAGER_ATTRIBUTE_NAME = "usersManager";
    private static final String GAMES_MANAGER_ATTRIBUTE_NAME = "gamesManager";

    public static UsersManager getUsersManager(ServletContext servletContext) {
        if (servletContext.getAttribute(USERS_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(USERS_MANAGER_ATTRIBUTE_NAME, new UsersManager());
        }

        return (UsersManager) servletContext.getAttribute(USERS_MANAGER_ATTRIBUTE_NAME);
    }

    public static GamesManager getGamesManager(ServletContext servletContext) {
        if (servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(GAMES_MANAGER_ATTRIBUTE_NAME, new GamesManager());
        }

        return (GamesManager) servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }

        return INT_PARAMETER_ERROR;
    }

}