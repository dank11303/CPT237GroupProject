package src;

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
        int[][] number = ui.getUserState();
        return false;
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
