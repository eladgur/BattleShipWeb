package servlets.gamesManagment.singleGameManager;

import logic.data.enums.AttackResult;

public class SquareStatusAfterMove {
    int row;
    int column;
    String attackResult;

    public SquareStatusAfterMove(int row, int column, AttackResult attackResult) {
        this.attackResult = attackResult.name();
        this.row = row;
        this.column = column;
    }
}
