//this will represent a user created level
public class CustomLevel
{
    //name of whoever created the level or whatever was put into that text box
    public String creatorName;

    //size of the board
    public int n;

    //region map for level
    public int[][] regionMap;

    //index for the level in the file
    public int index;

    //constructor
    public CustomLevel(String creatorName, int n, int[][] regionMap)
    {
        this.creatorName = creatorName;
        this.n = n;
        this.regionMap = regionMap;
    }//end constructor
}
