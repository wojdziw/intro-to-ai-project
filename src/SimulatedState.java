public class SimulatedState {
    private int[][] field;
    private int[] top;
    private int rowsCleared;
    private boolean gameFinished;

    public SimulatedState(int[][] field, int[] top, int rowsCleared, boolean gameFinished) {
        this.field = field;
        this.top = top;
        this.rowsCleared = rowsCleared;
        this.gameFinished = gameFinished;
    }

    public int[][] getField() {
        return field;
    }

    public int[] getTop() {
        return top;
    }

    public int getRowsCleared() {
        return rowsCleared;
    }

    public boolean wouldGameFinish() {
        return gameFinished;
    }
}