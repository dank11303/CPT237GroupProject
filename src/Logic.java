//class that will handle all the logic checking for the application
public class Logic
{
    private final QueensUI ui; //hold specific running UI

    //constructor to pass the specific running UI to this class.
    public Logic(QueensUI ui)
    {
        this.ui = ui;
    }//end Logic(QueensUI)

    //dynamic win check (rules-based)
    public Boolean checkGameState()
    {
        int[][] submittedState = ui.getUserState();

        //must place exactly N queens total (one per row/col/region)
        int queens = 0;
        for (int r = 0; r < submittedState.length; r++)
        {
            for (int c = 0; c < submittedState[0].length; c++)
            {
                if (submittedState[r][c] == 2) queens++;
            }
        }

        if (queens != submittedState.length) return false;

        return checkColsAndRows() && checkAreas() && checkTouching();
    }//end checkGameState()

    //check the states of the columns and rows
    public Boolean checkColsAndRows()
    {
        int[][] submittedState = ui.getUserState();
        int n = submittedState.length;

        //check each row has exactly 1 queen
        for (int r = 0; r < n; r++)
        {
            int count = 0;
            for (int c = 0; c < n; c++)
            {
                if (submittedState[r][c] == 2) count++;
            }
            if (count != 1) return false;
        }

        //check each column has exactly 1 queen
        for (int c = 0; c < n; c++)
        {
            int count = 0;
            for (int r = 0; r < n; r++)
            {
                if (submittedState[r][c] == 2) count++;
            }
            if (count != 1) return false;
        }

        return true;
    }//end checkColsAndRows()

    //check that each area only has one queen
    public Boolean checkAreas()
    {
        int[][] submittedState = ui.getUserState();
        int[][] regionMap = ui.getCurrentLevelMap();
        int n = submittedState.length;

        int[] regionCount = new int[n];

        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                if (submittedState[r][c] == 2)
                {
                    int regionId = regionMap[r][c];
                    regionCount[regionId]++;
                }
            }
        }

        for (int i = 0; i < n; i++)
        {
            if (regionCount[i] != 1) return false;
        }

        return true;
    }//end checkAreas()

    //check if the queens in the game have any touching them.
    public Boolean checkTouching()
    {
        int[][] submittedState = ui.getUserState();
        int n = submittedState.length;

        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                if (submittedState[r][c] == 2)
                {
                    for (int dr = -1; dr <= 1; dr++)
                    {
                        for (int dc = -1; dc <= 1; dc++)
                        {
                            if (dr == 0 && dc == 0) continue;

                            int rr = r + dr;
                            int cc = c + dc;

                            if (rr >= 0 && rr < n && cc >= 0 && cc < n)
                            {
                                if (submittedState[rr][cc] == 2) return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }//end checkTouching()
}