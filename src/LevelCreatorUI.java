import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.function.Consumer;

//the UI for the level creator portion of the app
public class LevelCreatorUI
{
    //root node for the screen
    private final VBox root = new VBox(10);

    //board size
    private final int n;

    //store region ID for each cell (-1 is an unpainted cell)
    private int[][] regionMap;

    //store the current color
    private int selectedColor = 0;

    //the grid to make levels in
    private final GridPane grid = new GridPane();

    //references each cell to repaint them
    private StackPane[][] cells;

    //test button stored as field to enable/disable it
    private Button testButton;


    //constructor that declares properties for the level creator
    public LevelCreatorUI(int n, //board size
                          Runnable onBack, //back button
                          Consumer<int[][]> onTest) //called when the region map is finished
    {
        //initialize a couple properties
        this.n = n;
        regionMap = new int[n][n];
        cells = new StackPane[n][n];

        //create a back button to go back to the levels menu
        Button back = new Button("Back");
        back.setOnAction(_ -> onBack.run());

        //title for the scene that also says the dimensions of the puzzle
        Label title = new Label("Level Creator - " + n + "x" + n);
        title.setFont(Font.font(20));

        //status message that shows when not all cells are filled
        Label status = new Label("");

        //create the top bar
        HBox topBar = new HBox(10, back, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        //this section will create a color pallette to choose colors to create the level from
        VBox palette = new VBox(5);
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPadding(new Insets(5));

        //create a toggle group for the color selector
        ToggleGroup colorRadios = new ToggleGroup();

        //create a button for each color in the pallette
        for (int i = 0; i < Levels.PALETTE.length; i++)
        {
            Color c = Levels.PALETTE[i];

            // colored square to show the color
            Rectangle colorBox = new Rectangle(20, 20, c);

            // radio button with the colored square as its graphic
            RadioButton colorBtn = new RadioButton();
            colorBtn.setGraphic(colorBox);
            colorBtn.setGraphicTextGap(10);
            colorBtn.setToggleGroup(colorRadios);

            // select first color by default
            if (i == 0) colorBtn.setSelected(true);

            int colorIndex = i;
            colorBtn.setOnAction(_ -> selectedColor = colorIndex);

            palette.getChildren().add(colorBtn);
        }//end for

        //fill the whole regionmap with -1
        for (int[] row : regionMap)
        {
            java.util.Arrays.fill(row, -1);
        }//end for

        //set up painting grid
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setAlignment(Pos.CENTER);

        //build the grid of paintable cells
        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                //create a stack pane for each cell
                StackPane cell = new StackPane();
                cell.setPrefSize(60, 60);
                cell.setBorder(new Border(new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(1)
                )));

                //start the cell to show it's unpainted
                cell.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

                //store the reference
                cells[r][c] = cell;

                //create local copies because lambdas are mean and don't like variables from the loops >:[
                int rr = r, cc = c;

                //when the cell is clicked, paint it with whatever color is selected
                cell.setOnMouseClicked(_ -> {
                    regionMap[rr][cc] = selectedColor;
                    cells[rr][cc].setBackground(new Background(new BackgroundFill(Levels.PALETTE[selectedColor], CornerRadii.EMPTY, Insets.EMPTY)));
                    testButton.setDisable(!checkAll()); //checks if all cells have been completed and enables the button once they are, and keeps it disabled if they aren't
                });

                //when the mouse is dragged over cells
                //start full drag when user drags from a cell
                cell.setOnDragDetected(e -> cell.startFullDrag());

                //when dragging enters this cell, apply the x state
                cell.setOnMouseDragEntered(e -> {
                    regionMap[rr][cc] = selectedColor;
                    cells[rr][cc].setBackground(new Background(new BackgroundFill(Levels.PALETTE[selectedColor], CornerRadii.EMPTY, Insets.EMPTY)));
                    testButton.setDisable(!checkAll());
                });

                //add the cell to the grid
                grid.add(cell, c, r);
            }
        }

        //the test button which will take the user to a game screen where they need to complete the level to be able to save it to the file
        //  the button is disabled until all of the cells have been painted
        testButton = new Button("Test Puzzle");
        testButton.setDisable(true);
        testButton.setOnAction(_ -> onTest.accept(regionMap));

        //set up the area where the pallete is on the right and the grid is on the left
        HBox middle = new HBox(10, grid, palette);
        middle.setAlignment(Pos.CENTER);
        middle.setPadding(new Insets(10));

        //assemble the root scene
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(topBar, middle, testButton, status);

    }//end levelCreatorUI(int, Runnable, Consumer<int[][]>)

    //checkAll helper method that will make sure all cells are painted before enabling the test button
    private boolean checkAll()
    {
        for (int[] row : regionMap)
        {
            for (int id : row)
            {
                if (id == -1) return false;
            }
        }
        return true;
    }//end checkAll()

    //getter method for the root scene so it can be placed by QueensUI.java
    public Parent getRoot()
    {
        return root;
    }
}
