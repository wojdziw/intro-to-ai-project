public class UtilityHelpers {

    public static int[][] freshField(State s) {
        int[][] field = s.getField();
        int[][] newField = new int[field.length][field[0].length];
        for (int i=0; i<field.length; i++)
            for (int j=0; j<field[0].length; j++)
                newField[i][j] = field[i][j];
        return newField;
    }

    public static int[] freshTop(State s) {
        int[] top = s.getTop();
        int[] newTop = new int[top.length];
        for (int i=0; i<top.length; i++)
            newTop[i] = top[i];
        return newTop;
    }

    public static int[][] calculateUpdatedField(int[][] field, int[] top, int[] move, int nextPiece) {

        int[][] pWidth = State.getpWidth();
        int[][] pHeight = State.getpHeight();
        int[][][] pBottom = State.getpBottom();
        int[][][] pTop = State.getpTop();

        int orient = move[State.ORIENT];
        int slot = move[State.SLOT];

        int turn = 1;

        //height if the first column makes contact
        int height = top[slot]-pBottom[nextPiece][orient][0];
        //for each column beyond the first in the piece
        for(int c = 1; c < pWidth[nextPiece][orient];c++) {
            height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
        }


        //for each column in the piece - fill in the appropriate blocks
        for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

            //from bottom to top of brick
            for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
                field[h][i+slot] = turn;
            }
        }

        //adjust top
        for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
            top[slot+c]=height+pTop[nextPiece][orient][c];
        }


        //check for full rows - starting at the top
        for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
            //check all columns in the row
            boolean full = true;
            for(int c = 0; c < State.COLS; c++) {
                if(field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            //if the row was full - remove it and slide above stuff down
            if(full) {
                //for each column
                for(int c = 0; c < State.COLS; c++) {

                    //slide down all bricks
                    for(int i = r; i < top[c]; i++) {
                        field[i][c] = field[i+1][c];
                    }
                    //lower the top
                    top[c]--;
                    while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
                }
            }
        }

        return field;
    }

    public static int[] calculateUpdatedTop(int[][] field, int[] top, int[] move, int nextPiece) {
        int[][] pWidth = State.getpWidth();
        int[][] pHeight = State.getpHeight();
        int[][][] pBottom = State.getpBottom();
        int[][][] pTop = State.getpTop();

        int orient = move[State.ORIENT];
        int slot = move[State.SLOT];

        int turn = 1;

        //height if the first column makes contact
        int height = top[slot]-pBottom[nextPiece][orient][0];
        //for each column beyond the first in the piece
        for(int c = 1; c < pWidth[nextPiece][orient];c++) {
            height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
        }


        //for each column in the piece - fill in the appropriate blocks
        for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

            //from bottom to top of brick
            for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
                field[h][i+slot] = turn;
            }
        }

        //adjust top
        for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
            top[slot+c]=height+pTop[nextPiece][orient][c];
        }


        //check for full rows - starting at the top
        for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
            //check all columns in the row
            boolean full = true;
            for(int c = 0; c < State.COLS; c++) {
                if(field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            //if the row was full - remove it and slide above stuff down
            if(full) {
                //for each column
                for(int c = 0; c < State.COLS; c++) {

                    //slide down all bricks
                    for(int i = r; i < top[c]; i++) {
                        field[i][c] = field[i+1][c];
                    }
                    //lower the top
                    top[c]--;
                    while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
                }
            }
        }

        return top;
    }


}
