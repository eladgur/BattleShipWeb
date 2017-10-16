package servlets.gamesManagment.singleGameManager;

import logic.GameEngine;
import logic.data.Ship;
import logic.data.enums.AttackResult;
import logic.exceptions.NoShipAtPoisitionException;
import xmlInputManager.Position;

import static logic.data.Constants.NUM_OF_PLAYERS;

public class SquareStatusAfterMove {
    int row;
    int column;
    String attackResult;
    int attackersIndex;
    Ship drownShip;

    public SquareStatusAfterMove(int row, int column, AttackResult attackResult, int attackerIndex, GameEngine gameEngine) {
        this.attackResult = attackResult.name();
        this.row = row;
        this.column = column;
        this.attackersIndex = attackerIndex;
        // Update only if needed
        Ship drownShip = attackResult.getShip();
        if (drownShip != null) {
            this.drownShip = drownShip;
        }
    }

    private void updateOnDrownShip(int row, int column, AttackResult attackResult, int attackerIndex, GameEngine gameEngine) {
        boolean needToUpdateDrownShip = false;
        int playerIndex = 0;

        if (attackResult == AttackResult.MINEDROWNSHIP) {
            needToUpdateDrownShip = true;
        } else if (attackResult == AttackResult.SHIPDROWNHIT) {
            needToUpdateDrownShip = true;
        }

        if (needToUpdateDrownShip) {
            Position position = new Position(row, column);
            this.drownShip = attackResult.getShip();
        } else {
            this.drownShip = null;
        }
    }

    public SquareStatusAfterMove(int row, int column, String attackResult, int userIndex) {
        this.attackResult = attackResult;
        this.row = row;
        this.column = column;
        this.attackersIndex = userIndex;
    }
}
