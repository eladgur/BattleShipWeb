package servlets.gamesManagment;

import logic.GameEngine;
import logic.exceptions.LogicallyInvalidXmlInputException;
import servlets.fileUpload.FileUploadServlet;
import servlets.gamesManagment.singleGameManager.SquareStatusAfterMove;
import utils.ServletUtils;
import xmlInputManager.InvalidXmFormatException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static constants.Constants.FORWARD_SLASH;
import static constants.Constants.UPLOAD_DIR;
import static constants.Constants.XML_FILE_EXTENSTION;

public class GamesManager {

    public static final String GAME_ENGINE_DICTIONARY = "gameEngineDictionary";

    private Map<String, Game> gamesList;

    private Set<String> gamesNamesSet;

    public GamesManager() {
        this.gamesList = new Hashtable<>();
        this.gamesNamesSet = new HashSet<>();
    }

    public void addGame(String gameName, GameEngine gameEngine, String uploaderName) {
        Game game = new Game(gameName, gameEngine, 0, uploaderName);
        //Add name Game to names list for refresh on the client side every 2 seconds
        gamesNamesSet.add(gameName);
        //Add game Object to the game list for forther details about this game
        gamesList.put(gameName, game);
    }

    public void removeGame(String gameName) {
        gamesNamesSet.remove(gameName);
        gamesList.remove(gameName);
    }

    public Set<String> getGamesList() {
        return Collections.unmodifiableSet(gamesNamesSet);
    }

    public int getNumberOfPlayersInSpecificGame(String gameName) {
        if (isGameExist(gameName)) {
            return gamesList.get(gameName).getAmountOfPlayers();
        } else {
            return -1; //insteadOfexception
        }
    }

    public void addUserToGame(String gameName, String userName) throws Game.GameFullException {
        Game game = gamesList.get(gameName);

        if (game != null) {
            game.addUserToGame(userName);
        }
    }

    public void removeUserFromGame(String gameName, String userName) {

        if (isGameExist(gameName)) {
            Game game = gamesList.get(gameName);
            game.removeUserFromGame(userName);
        }
    }

    public boolean isGameExist(String gameName) {
        return gamesList.containsKey(gameName);
    }

    public GameEngine getGameEngineByGameName(String gameName) {
        GameEngine gameEngine = gamesList.get(gameName).getGameEngine();

        return gameEngine;
    }

    public Game getGameByName(String gameName) {
        return this.gamesList.get(gameName);
    }

    public void resetGame(String gameName, ServletContext servletContext) throws IOException {
        String gamePath = FileUploadServlet.buildGameFilePathFromGameName(gameName, servletContext);
        String gameUploaderName = getGameByName(gameName).getUploaderName();

        //Remove Game
        removeGame(gameName);
        //Re-Add the Game
        try {
            FileUploadServlet.createNewGame(gameName, gameUploaderName, gamePath, this);
        } catch (InvalidXmFormatException | LogicallyInvalidXmlInputException e) {
            e.printStackTrace();
        }
    }
}
