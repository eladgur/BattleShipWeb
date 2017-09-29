package servlets.gamesManagment;

import logic.GameEngine;

import java.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GamesManager {

    public static final String GAME_ENGINE_DICTIONARY = "gameEngineDictionary";

    private Set<String> gamesNamesSet;

    private Map<String, GameEngine> gameEngineMap;

    public GamesManager() {
        gameEngineMap = new Hashtable<String, GameEngine>();
        gamesNamesSet = new HashSet<>();
    }

    public void addGame(String gameName, GameEngine gameEngine) {
        gamesNamesSet.add(gameName);
        gameEngineMap.put(gameName, gameEngine);
    }

    public void removeGame(String gameName) {
        gamesNamesSet.remove(gameName);
        gameEngineMap.remove(gameName);
    }

    public Set<String> getGamesList() {
        return Collections.unmodifiableSet(gamesNamesSet);
    }

    public Map<String, GameEngine> getGameEngineMap() {
        return gameEngineMap;
    }

    public boolean isGameExist(String gameName) {
        return gameEngineMap.containsKey(gameName);
    }

}
