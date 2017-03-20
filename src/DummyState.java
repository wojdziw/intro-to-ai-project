import java.awt.*;

public class DummyState {

    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int N_PIECES = 7;

    //number of next piece
    protected int nextPiece;

    //all legal moves - first index is piece type - then a list of 2-length arrays
    protected static int[][][] legalMoves = new int[N_PIECES][][];

    //indices for legalMoves
    public static final int ORIENT = 0;
    public static final int SLOT = 1;

    //possible orientations for a given piece type
    protected static int[] pOrients = {1,2,4,4,4,2,2};

    //the next several arrays define the piece vocabulary in detail
    //width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = {
            {2},
            {1,4},
            {2,3,2,3},
            {2,3,2,3},
            {2,3,2,3},
            {3,2},
            {3,2}
    };
    //height of the pieces [piece ID][orientation]
    private static int[][] pHeight = {
            {2},
            {4,1},
            {3,2,3,2},
            {3,2,3,2},
            {3,2,3,2},
            {2,3},
            {2,3}
    };
    private static int[][][] pBottom = {
            {{0,0}},
            {{0},{0,0,0,0}},
            {{0,0},{0,1,1},{2,0},{0,0,0}},
            {{0,0},{0,0,0},{0,2},{1,1,0}},
            {{0,1},{1,0,1},{1,0},{0,0,0}},
            {{0,0,1},{1,0}},
            {{1,0,0},{0,1}}
    };
    private static int[][][] pTop = {
            {{2,2}},
            {{4},{1,1,1,1}},
            {{3,1},{2,2,2},{3,3},{1,1,2}},
            {{1,3},{2,1,1},{3,3},{2,2,2}},
            {{3,2},{2,2,2},{2,3},{1,2,1}},
            {{1,2,2},{3,2}},
            {{2,2,1},{2,3}}
    };

    //initialize legalMoves
    {
        //for each piece type
        for(int i = 0; i < N_PIECES; i++) {
            //figure number of legal moves
            int n = 0;
            for(int j = 0; j < pOrients[i]; j++) {
                //number of locations in this orientation
                n += COLS+1-pWidth[i][j];
            }
            //allocate space
            legalMoves[i] = new int[n][2];
            //for each orientation
            n = 0;
            for(int j = 0; j < pOrients[i]; j++) {
                //for each slot
                for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
                    legalMoves[i][n][ORIENT] = j;
                    legalMoves[i][n][SLOT] = k;
                    n++;
                }
            }
        }

    }

    //constructor
    public DummyState() {
        nextPiece = randomPiece();
    }

    //random integer, returns 0-6
    private int randomPiece() {
        return (int)(Math.random()*N_PIECES);
    }

    // Gives legal moves for specified piece
    public int[][] getLegalMoves(int piece) { return legalMoves[piece];}

}


