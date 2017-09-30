package utils;

import logic.GameEngine;
import servlets.gamesManagment.GamesManager;
import servlets.signup.UsersManager;

import javax.jms.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static constants.Constants.INT_PARAMETER_ERROR;

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