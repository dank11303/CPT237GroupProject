//class that will handle all the logic checking for the application
public class Logic
{
    //stores the running QueensUI so we can read the board and the level map
    private final QueensUI ui;

    //constructor to pass the specific running UI to this class.
    public Logic(QueensUI ui)
    {
        //save the UI reference so we can call ui.getUserState() and ui.getCurrentLevelMap()
        this.ui = ui;
    }//end Logic(QueensUI)

    //dynamic win check (rules-based)
    public Boolean checkGameState()
    {
        //get the current player board from the UI
        int[][] submittedState = ui.getUserState();

        //must place exactly N queens total (one per row/col/region)
        int queens = 0;

        //count how many queens are on the board
        //queen is represented by the number 2 in userState
        for (int[] row : submittedState)
        {
            for (int c = 0; c < submittedState[0].length; c++)
            {
                if (row[c] == 2) queens++;
            }
        }

        //if the player does not have exactly N queens, the puzzle is not solved
        //submittedState.length is N (8 for a 8x8 board)
        if (queens != submittedState.length) return false;

        //the puzzle is solved only if ALL three checks return true
        return checkColsAndRows() && checkAreas() && checkTouching();
    }//end checkGameState()

    //check the states of the columns and rows
    public Boolean checkColsAndRows()
    {
        //get the current player board from the UI
        int[][] submittedState = ui.getUserState();

        //board size (8 for a 8x8 board)
        int n = submittedState.length;

        //check each row has exactly 1 queen
        for (int[] row : submittedState)
        {
            int count = 0;

            //count queens in this row
            for (int c = 0; c < n; c++)
            {
                if (row[c] == 2) count++;
            }

            //if row does not have exactly 1 queen, fail
            if (count != 1) return false;
        }

        //check each column has exactly 1 queen
        for (int c = 0; c < n; c++)
        {
            int count = 0;

            //count queens in this column
            for (int[] row : submittedState)
            {
                if (row[c] == 2) count++;
            }

            //if column does not have exactly 1 queen, fail
            if (count != 1) return false;
        }

        //passed row and column checks
        return true;
    }//end checkColsAndRows()

    //check that each area only has one queen
    public Boolean checkAreas()
    {
        //get the current player board from the UI
        int[][] submittedState = ui.getUserState();

        //get the region map for the current level (same size as the board)
        int[][] regionMap = ui.getCurrentLevelMap();

        //board size (8 for a 8x8 board)
        int n = submittedState.length;

        //this counts how many queens are in each region
        //IMPORTANT: this assumes your region ids go from 0 to 7 (for a 8x8 board)
        int[] regionCount = new int[n];

        //loop through every cell
        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                //only care about cells that have a queen
                if (submittedState[r][c] == 2)
                {
                    //get the region id for this cell
                    int regionId = regionMap[r][c];

                    //count a queen in this region
                    regionCount[regionId]++;
                }
            }
        }

        //each region must have exactly 1 queen
        for (int i = 0; i < n; i++)
        {
            if (regionCount[i] != 1) return false;
        }

        //passed region check
        return true;
    }//end checkAreas()

    //check if the queens in the game have any touching them.
    public Boolean checkTouching()
    {
        //get the current player board from the UI
        int[][] submittedState = ui.getUserState();

        //board size (8 for a 8x8 board)
        int n = submittedState.length;

        //loop through every cell on the board
        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                //if this cell has a queen
                if (submittedState[r][c] == 2)
                {
                    //check all 8 surrounding positions using dr/dc
                    for (int dr = -1; dr <= 1; dr++)
                    {
                        for (int dc = -1; dc <= 1; dc++)
                        {
                            //skip the center (the queen itself)
                            if (dr == 0 && dc == 0) continue;

                            //neighbor row/col
                            int rr = r + dr;
                            int cc = c + dc;

                            //make sure the neighbor is inside the board
                            if (rr >= 0 && rr < n && cc >= 0 && cc < n)
                            {
                                //if any neighbor is also a queen, queens are touching => fail
                                if (submittedState[rr][cc] == 2) return false;
                            }
                        }
                    }
                }
            }
        }

        //no touching queens found
        return true;
    }//end checkTouching()
}