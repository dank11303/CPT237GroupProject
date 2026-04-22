import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.function.IntConsumer;

//this will create the small in-between screen that is between the main menu and the level creator. It's used to set the level size
public class SizePickerUI
{
    //create a root node for the window
    private final VBox root = new VBox(15);

    //constructor
    public SizePickerUI(Runnable onBack, IntConsumer onSizeSelected)
    {
        //create a title
        Label title = new Label("Choose board size:");

        //have a combo box to allow you to select the level size
        ComboBox<String> sizePicker = new ComboBox<>();
        for (int i = 5; i <= 15; i++)
        {
            sizePicker.getItems().add(i + "x" + i); //add 5 to the index to pass the size value
        }
        sizePicker.getSelectionModel().select(0); //set the first option as default

        //button that will take you to the LevelCreatorUI and pass the selected size
        Button create = new Button("Create Level");
        create.setOnAction(_ -> {
            int size = 5 + sizePicker.getSelectionModel().getSelectedIndex();
            onSizeSelected.accept(size);
        });

        //cancel button to bring you back home
        Button back = new Button("Cancel");
        back.setOnAction(_ -> onBack.run());

        //add everyting into the root node
        root.getChildren().addAll(title, sizePicker, create, back);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
    }

    //getter method for the root so QueensUI can access it.
    public Parent getRoot()
    {
        return root;
    }
}