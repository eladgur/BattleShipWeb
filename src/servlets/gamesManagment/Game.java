package servlets.gamesManagment;

import logic.GameEngine;
import servlets.gamesManagment.singleGameManager.SquareStatusAfterMove;

import java.util.LinkedList;
import java.util.List;

public class Game {

    public static final int MAX_AMOUNT_OF_PLAYERS = 2;

    private String name;
    private GameEngine gameEngine;
    private int amountOfPlayers;
    private String uploader;
    private SquareStatusAfterMove lastMove;
    private List<String> playersNames;

    public Game(String gameName, GameEngine gameEngine, int amountOfPlayers, String uploaderName) {
        this.name = gameName;
        this.gameEngine = gameEngine;
        this.amountOfPlayers = amountOfPlayers;
        this.playersNames = new LinkedList<>();
        this.uploader = uploaderName;
    }

    public int getAmountOfPlayers() {
        return this.amountOfPlayers;
    }

    public GameEngine getGameEngine() {
        return this.gameEngine;
    }

    public void updateLastMove(SquareStatusAfterMove squareStatusAfterMove) {
        this.lastMove = squareStatusAfterMove;
    }

    public void addUserToGame(String userName) throws GameFullException {
        if (amountOfPlayers + 1 <= MAX_AMOUNT_OF_PLAYERS) {
            this.playersNames.add(userName);
            this.amountOfPlayers++;
        } else {
            throw new GameFullException();
        }
    }

    public void removeUserFromGame(String userName) {
        this.playersNames.remove(userName);
        this.amountOfPlayers--;
    }

    public String getUploaderName() {
        return this.uploader;
    }

    public class GameFullException extends Exception {

        private static final String msg = "Game is allready full";

        public GameFullException() {
            super(msg);
        }
    }
}
