package servlets.gamesManagment.singleGameManager;

import logic.data.enums.AttackResult;

public class SquareStatusAfterMove {
    int row;
    int column;
    String attackResult;
    int attackersIndex;

    public SquareStatusAfterMove(int row, int column, AttackResult attackResult, int userIndex) {
        this.attackResult = attackResult.name();
        this.row = row;
        this.column = column;
        this.attackersIndex = userIndex;
    }
}
