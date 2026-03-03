import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class QueensUI extends Application {

    static final int N = 8;

    // levels[level][row][col] = region/color id
    static final int[][][] LEVELS = {
            { // Level 1 (example)
                    {0,0,0,1,1,2,2,3},
                    {0,4,4,1,5,5,2,3},
                    {0,4,4,1,5,5,6,3},
                    {7,7,4,1,1,5,6,6},
                    {7,7,8,8,1,5,6,6},
                    {7,7,8,8,8,5,6,6},
                    {7,7,8,8,8,8,6,6},
                    {7,7,8,8,8,8,6,6}
            },
            { // Level 2 (placeholder example)
                    {1,1,1,1,2,2,2,2},
                    {1,0,0,1,2,3,3,2},
                    {1,0,0,1,2,3,3,2},
                    {4,4,4,4,5,5,5,5},
                    {4,6,6,4,5,7,7,5},
                    {4,6,6,4,5,7,7,5},
                    {8,8,8,8,9,9,9,9},
                    {8,8,8,8,9,9,9,9}
            }
    };

    // solutions[level][row][col] = 1 means queen belongs here (optional for later)
    static final int[][][] SOLUTIONS = {
            new int[N][N], // Level 1 solution placeholder
            new int[N][N]  // Level 2 solution placeholder
    };


    static final Color[] PALETTE = {
            Color.web("#b9a6e8"), // 0 purple
            Color.web("#f6c28f"), // 1 orange
            Color.web("#8fb6ff"), // 2 blue
            Color.web("#a7df9f"), // 3 green
            Color.web("#d9b58e"), // 4 tan
            Color.web("#e0e0e0"), // 5 gray
            Color.web("#a8dfb0"), // 6 light green
            Color.web("#f26c5f"), // 7 red
            Color.web("#f2e58c"), // 8 yellow
            Color.web("#cfcfcf")  // 9 extra gray
    };

    private final int[][] userState = new int[N][N]; // 0 empty, 1 x, 2 queen
    private final Label[][] symbols = new Label[N][N];
    private final StackPane[][] cells = new StackPane[N][N];

    private int currentLevel = 0;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Queens");
        title.setFont(Font.font(24));

        ComboBox<String> levelPicker = new ComboBox<>();
        for (int i = 0; i < LEVELS.length; i++) levelPicker.getItems().add("Level " + (i + 1));
        levelPicker.getSelectionModel().select(0);
        levelPicker.setOnAction(e -> {
            currentLevel = levelPicker.getSelectionModel().getSelectedIndex();
            clearUserState();
            renderLevel(currentLevel);
        });

        Button clear = new Button("Clear Marks");
        clear.setOnAction(e -> {
            clearUserState();
            renderSymbols();
        });

        HBox topBar = new HBox(12, title, levelPicker, clear);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        GridPane board = buildBoard();
        renderLevel(currentLevel);

        VBox root = new VBox(10, topBar, board);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 720, 820));
        stage.setTitle("Queens UI (Levels via 3D Array)");
        stage.show();
    }

    private GridPane buildBoard() {
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(1))));

                Label txt = new Label("");
                txt.setFont(Font.font(26));

                symbols[r][c] = txt;
                cells[r][c] = cell;

                int rr = r, cc = c;
                cell.setOnMouseClicked(e -> {
                    userState[rr][cc] = (userState[rr][cc] + 1) % 3;
                    renderSymbols();
                });

                cell.getChildren().add(txt);
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    private void renderLevel(int levelIndex) {
        int[][] map = LEVELS[levelIndex];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                int id = map[r][c];
                Color fill = PALETTE[id % PALETTE.length];
                cells[r][c].setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
        renderSymbols();
    }

    private void renderSymbols() {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                symbols[r][c].setText(
                        userState[r][c] == 0 ? "" :
                        userState[r][c] == 1 ? "x" : "♛"
                );
            }
        }
    }

    private void clearUserState() {
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                userState[r][c] = 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}