package servlets.gamesManagment.singleGameManager;

public class MoveUpdateVerifyer {

    SquareStatusAfterMove lastMove;
    public boolean index0;
    public boolean index1;

    //Index0 - User0 is updated at the moment !

    private MoveUpdateVerifyer(SquareStatusAfterMove lastMove, boolean index0, boolean index1) {
        this.lastMove = lastMove;
        this.index0 = index0;
        this.index1 = index1;
    }

    public static MoveUpdateVerifyer Playe1AttackedUpdateVerifyer(SquareStatusAfterMove lastMove) {
        return new MoveUpdateVerifyer(lastMove, false, true);
    }

    public static MoveUpdateVerifyer Playe0AttackedUpdateVerifyer(SquareStatusAfterMove lastMove) {
        return new MoveUpdateVerifyer(lastMove, true, false);
    }

    public static MoveUpdateVerifyer noUpdatedValues(SquareStatusAfterMove lastMove) {
        return new MoveUpdateVerifyer(lastMove, false, false);
    }

    public boolean isBothPlayerRecivedUpdate() {
        return this.index0 && this.index1;
    }
}
