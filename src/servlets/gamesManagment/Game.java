package servlets.gamesManagment;

import logic.GameEngine;
import logic.data.ShipBoard;
import logic.data.enums.AttackResult;
import servlets.gameServlet.GameServlet;
import servlets.gamesManagment.singleGameManager.MoveUpdateVerifyer;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Game {

    public static final int MAX_AMOUNT_OF_PLAYERS = 2;

    private String name;
    private GameEngine gameEngine;
    private int amountOfPlayers;
    private String uploader;
    private List<String> playersNames;
    private MoveUpdateVerifyer moveUpdateVerifyer;
    private boolean gameEnd;
    private int winnerIndex;
    private String user0ShipBoard[][];
    private String user1ShipBoard[][];
    private String endTime;

    public Game(String gameName, GameEngine gameEngine, int amountOfPlayers, String uploaderName) {
        int boardSize = gameEngine.getGameInfo().getBoardSize();

        this.name = gameName;
        this.gameEngine = gameEngine;
        this.amountOfPlayers = amountOfPlayers;
        this.playersNames = new LinkedList<>();
        this.uploader = uploaderName;
        this.moveUpdateVerifyer = null;
        this.endTime = null;
        createShipBoards(boardSize, gameEngine);
    }

    public MoveUpdateVerifyer getMoveUpdateVerifyer() {
        return moveUpdateVerifyer;
    }

    public void setMoveUpdateVerifyer(MoveUpdateVerifyer moveUpdateVerifyer) {
        this.moveUpdateVerifyer = moveUpdateVerifyer;
    }

    private void createShipBoards(int boardSize, GameEngine gameEngine) {
        this.user0ShipBoard = new String[boardSize][boardSize];
        this.user1ShipBoard = new String[boardSize][boardSize];

        setShipBoard(user0ShipBoard, gameEngine.getPlayerData(0).getShipBoard(), boardSize);
        setShipBoard(user1ShipBoard, gameEngine.getPlayerData(1).getShipBoard(), boardSize);
    }

    private void setShipBoard(String[][] userShipBoardToSet, ShipBoard shipBoard, int boardSize) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                userShipBoardToSet[row][col] = GameServlet.generateShipBoardClassNameFromShipBoard(shipBoard, row, col);
            }
        }
    }

    public int getAmountOfPlayers() {
        return this.amountOfPlayers;
    }

    public GameEngine getGameEngine() {
        return this.gameEngine;
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

    public void storeAttackResult(AttackResult attackResult, int row, int col, int userIndex) {
        String[][] attackerBoardToUpdate;
        String[][] defenderBoardToUpdate;

        if (userIndex == 0) {
            attackerBoardToUpdate = this.user0ShipBoard;
            defenderBoardToUpdate = this.user1ShipBoard;
        } else {
            attackerBoardToUpdate = this.user1ShipBoard;
            defenderBoardToUpdate = this.user0ShipBoard;
        }


        updateAttackerBoard(attackResult, attackerBoardToUpdate, row, col);
        updateDefenderBoard(attackResult, defenderBoardToUpdate, row, col);
    }

    private void updateAttackerBoard(AttackResult attackResult, String[][] attackerBoardToUpdate, int row, int col) {
        String classNameToPut = "";
        boolean needToUpdateSquare = false;

        switch (attackResult) {
            case MINESHIP:
                classNameToPut = "hit";
                needToUpdateSquare = true;
                break;
            case MINEDROWNSHIP:
                classNameToPut = "drownShip";
                needToUpdateSquare = true;
                break;
            case MINEWATER:
                classNameToPut = "miss";
                needToUpdateSquare = true;
                break;
            case MINEMINE:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            case INSERTMINE:
                classNameToPut = "mine";
                needToUpdateSquare = true;
                break;
            default:
                needToUpdateSquare = false;
                break;
        }

        if (needToUpdateSquare) {
            attackerBoardToUpdate[row][col] = classNameToPut;
        }
    }

    private void updateDefenderBoard(AttackResult attackResult, String[][] defenderBoardToUpdate, int row, int col) {
        String classNameToPut = "";
        boolean needToUpdateSquare = false;

        switch (attackResult) {

            case SHIPHIT:
                classNameToPut = "hit";
                needToUpdateSquare = true;
                break;
            case SHIPDROWNHIT:
                classNameToPut = "drownShip";
                needToUpdateSquare = true;
                break;
            case MISSHIT:
                classNameToPut = "miss";
                needToUpdateSquare = true;
                break;
            case MINESHIP:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            case MINEDROWNSHIP:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            case MINEWATER:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            case MINEMINE:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            case MINEREAPETEDHIT:
                classNameToPut = "mineExplosion";
                needToUpdateSquare = true;
                break;
            default:
                needToUpdateSquare = false;
                break;
        }

        if (needToUpdateSquare) {
            defenderBoardToUpdate[row][col] = classNameToPut;
        }
    }

    public String[][] getUser0ShipBoard() {
        return this.user0ShipBoard;
    }

    public String[][] getUser1ShipBoard() {
        return this.user1ShipBoard;
    }

    /*This function make sure that both players get the same end time*/
    public String getEndTime() {
        if (this.endTime == null) {
            LocalTime startTime = gameEngine.getStartTime();
            LocalTime currentTime = LocalTime.now();
            this.endTime = String.valueOf(MINUTES.between(startTime, currentTime)) +
                    ":" + String.valueOf(SECONDS.between(startTime, currentTime));
        }

        return this.endTime;
    }

    public class GameFullException extends Exception {

        private static final String msg = "Game is allready full";

        public GameFullException() {
            super(msg);
        }
    }

    public String GetPlayerNameByIndex(int index) {
        return playersNames.get(index);
    }
}
