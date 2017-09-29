package servlets.gamesManagment;

import logic.GameEngine;
import servlets.gamesManagment.singleGameManager.SquareStatusAfterMove;

import java.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GamesManager {

    public static final String GAME_ENGINE_DICTIONARY = "gameEngineDictionary";

    private Set<String> gamesNamesSet;

    private Map<String, GameEngine> gameEngineMap;

    private Map<String, Integer> amountOfplayersInGameMap;

    private Map<String, SquareStatusAfterMove> lastMoveInGameMap;

    public GamesManager() {
        this.gameEngineMap = new Hashtable<>();
        this.gamesNamesSet = new HashSet<>();
        this.amountOfplayersInGameMap = new Hashtable<>();
        this.lastMoveInGameMap = new Hashtable<>();
    }

    public void addGame(String gameName, GameEngine gameEngine) {
        gamesNamesSet.add(gameName);
        gameEngineMap.put(gameName, gameEngine);
        amountOfplayersInGameMap.put(gameName, new Integer(0));
    }

    public void removeGame(String gameName) {
        gamesNamesSet.remove(gameName);
        gameEngineMap.remove(gameName);
        amountOfplayersInGameMap.remove(gameName);
    }

    public Set<String> getGamesList() {
        return Collections.unmodifiableSet(gamesNamesSet);
    }

    public Map<String, GameEngine> getGameEngineMap() {
        return gameEngineMap;
    }

    public Map<String, Integer> getAmountOfplayersInGameMap() {
        return amountOfplayersInGameMap;
    }

    public int getNumberOfPlayersInSpecificGame(String gameName) {
        if (isGameExist(gameName)) {
            return amountOfplayersInGameMap.get(gameName).intValue();
        } else {
            return -1; //insteadOfexception
        }
    }

    public void addUserToRoom(String gameName) {

        if (isGameExist(gameName)) {
            int newAmountOfpeople = amountOfplayersInGameMap.get(gameName).intValue() + 1;
            amountOfplayersInGameMap.replace(gameName, new Integer(newAmountOfpeople));
        }
    }

    public void decUserFromRoom(String gameName) {

        if (isGameExist(gameName)) {
            int newAmountOfpeople = amountOfplayersInGameMap.get(gameName).intValue() - 1;
            amountOfplayersInGameMap.replace(gameName, new Integer(newAmountOfpeople));
        }
    }

    public boolean isGameExist(String gameName) {
        return gameEngineMap.containsKey(gameName);
    }

    public GameEngine getGameEngineByGameName(String gameName) {
        GameEngine gameEngine = gameEngineMap.get(gameName);

        return gameEngine;
    }

    public void updateLastMoveInLastMoveInGameMap(String gameName, SquareStatusAfterMove squareStatusAfterMove) {
        lastMoveInGameMap.put(gameName, squareStatusAfterMove);
    }
}
