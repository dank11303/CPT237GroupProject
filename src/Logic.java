

//class that will handle all of the logic checking for the application
public class Logic
{
    private QueensUI ui; //hold specific running UI

    //constructor to pass the specific running UI to this class.
    public Logic(QueensUI ui)
    {
        this.ui = ui;
    }//end Logic(QueensUI)

    //temporary for prototyping
    public Boolean checkGameState()
    {
        //get the current user answers for the puzzle by using getter in QueensUI.java
        int[][] submittedState = ui.getUserState();

        //get answer for current puzzle
        int[][] puzzleAnswer = ui.getSolution();

        //answered numbers
        int requireCorrect = puzzleAnswer.length;
        int correctEntries = 0;

        //loop through submitted answers and the key
        for (int r = 0; r < submittedState.length; r++)
        {
            for (int c = 0; c < submittedState[0].length; c++)
            {
                if (submittedState[r][c] == 2 && puzzleAnswer[r][c] == 1)
                {
                    correctEntries ++;
                }
            }
        }

        //if entered answers are correct
        if (correctEntries == requireCorrect)
        {
            return true;
        }
        else
        {
            return false;
        }
    }//end checkGameState()

    //check the states of the columns and rows
    public Boolean checkColsAndRows()
    {
        return false;
    }//end checkColsAndRows()

    //check that each area only has one queen
    public Boolean checkAreas()
    {
        return false;
    }//end checkAreas()

    //check if the queens in the game have any touching them.
    public Boolean checkTouching()
    {
        return false;
    }//end checkTouching()
}
