import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BoardUI
{
    //stores what the player placed:
    //0 = empty, 1 = x mark, 2 = queen
    private final int[][] userState = new int[Levels.N][Levels.N];

    //each cell has a Label that shows either "", "x", or "♛"
    private final Label[][] symbols = new Label[Levels.N][Levels.N];

    //each cell is a clickable StackPane (background + label)
    private final StackPane[][] cells = new StackPane[Levels.N][Levels.N];

    //the main grid that holds all 64 cells (8x8)
    private final GridPane grid = new GridPane();

    //this is a callback QueensUI can set (used to start timer on first click)
    private Runnable onFirstAction;

    //tracks if we already fired the first-action callback
    private boolean firstActionFired = false;

    //constructor builds the entire board (grid + cells + click events)
    public BoardUI()
    {
        //small spacing between cells
        grid.setHgap(1);
        grid.setVgap(1);

        //center the board on the screen
        grid.setAlignment(Pos.CENTER);

        //build the 8x8 board
        for (int r = 0; r < Levels.N; r++)
        {
            for (int c = 0; c < Levels.N; c++)
            {
                //this is the clickable square
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);

                //simple black border around each cell
                cell.setBorder(new Border(new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(1)
                )));

                //this label shows x / queen
                Label txt = new Label("");
                txt.setFont(Font.font(26));

                //store references so we can update later
                symbols[r][c] = txt;
                cells[r][c] = cell;

                //local copies for the click handler
                int rr = r, cc = c;

                //when a cell is clicked
                cell.setOnMouseClicked(_ -> {

                    //fire the first-action callback only once (starts timer)
                    if (!firstActionFired)
                    {
                        firstActionFired = true;
                        if (onFirstAction != null) onFirstAction.run();
                    }

                    //cycle this cell’s state:
                    //0 -> 1 -> 2 -> 0 ...
                    userState[rr][cc] = (userState[rr][cc] + 1) % 3;

                    //update the display after state changes
                    renderSymbols();
                });

                //put the label inside the cell
                cell.getChildren().add(txt);

                //add the cell to the grid (column first, then row)
                grid.add(cell, c, r);
            }
        }
    }

    //returns the board so QueensUI can add it to the game screen
    public Parent getNode()
    {
        return grid;
    }

    //returns the player's board state (Logic uses this)
    public int[][] getUserState()
    {
        return userState;
    }

    //QueensUI sets a callback that runs once when the player first clicks the board
    public void setOnFirstAction(Runnable action)
    {
        onFirstAction = action;
    }

    //resets the "first click" so the timer can start again (new level / clear)
    public void resetFirstAction()
    {
        firstActionFired = false;
    }

    //sets the colors of each cell based on the region map for the current level
    public void renderLevel(int[][] regionMap)
    {
        //loop through the board and paint each cell
        for (int r = 0; r < Levels.N; r++)
        {
            for (int c = 0; c < Levels.N; c++)
            {
                //region id comes from LEVELS array
                int id = regionMap[r][c];

                //choose a color from the palette
                Color fill = Levels.PALETTE[id % Levels.PALETTE.length];

                //set the background color for the cell
                cells[r][c].setBackground(new Background(
                        new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)
                ));
            }
        }

        //draw symbols after coloring
        renderSymbols();
    }

    //updates the x/queen text based on userState
    public void renderSymbols()
    {
        for (int r = 0; r < Levels.N; r++)
        {
            for (int c = 0; c < Levels.N; c++)
            {
                //convert userState number into a symbol
                symbols[r][c].setText(
                        userState[r][c] == 0 ? "" :
                                userState[r][c] == 1 ? "x" : "♛"
                );
            }
        }
    }

    //clears the whole board back to empty
    public void clear()
    {
        //set every cell back to 0 (empty)
        for (int r = 0; r < Levels.N; r++)
            for (int c = 0; c < Levels.N; c++)
                userState[r][c] = 0;

        //redraw symbols after clearing
        renderSymbols();

        //allow first click to start timer again
        resetFirstAction();
    }

    //loads a saved board into the grid
    public void setUserState(int[][] loaded)
    {
        //copy the saved 8x8 array into the current array
        for (int r = 0; r < Levels.N; r++)
            System.arraycopy(loaded[r], 0, userState[r], 0, Levels.N);

        //redraw symbols after loading
        renderSymbols();

        //allow first click to start timer again
        resetFirstAction();
    }
}