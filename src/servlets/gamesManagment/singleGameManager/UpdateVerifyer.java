package servlets.gamesManagment.singleGameManager;

public class UpdateVerifyer {
    SquareStatusAfterMove lastMove;
    public boolean index0;
    public boolean index1;

    private UpdateVerifyer(SquareStatusAfterMove lastMove, boolean index0, boolean index1) {
        this.lastMove = lastMove;
        this.index0 = index0;
        this.index1 = index1;
    }

    public static UpdateVerifyer Playe1AttackedUpdateVerifyer(SquareStatusAfterMove lastMove)
    {
        return new UpdateVerifyer(lastMove,false,true);
    }

    public static UpdateVerifyer Playe0AttackedUpdateVerifyer(SquareStatusAfterMove lastMove)
    {
        return new UpdateVerifyer(lastMove,true,false);
    }
    public static UpdateVerifyer noUpdatedValues(SquareStatusAfterMove lastMove)
    {
        return new UpdateVerifyer(lastMove,false,false);
    }




    public boolean isBothPlayerRecivedUpdate()
    {
        return this.index0 && this.index1;
    }
}
