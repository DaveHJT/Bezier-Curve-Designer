import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


// modified from CS349 sample code: user_prompt
public class DialogBox {
    static boolean answer;

    public static boolean Display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label(message);
        label.setWrapText(true);
        Button yesButton= new Button("Yes");
        yesButton.setOnAction(event -> {
            answer = true;
            window.close();
        });
        Button noButton= new Button("No");
        noButton.setOnAction(event -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox();
        HBox choices = new HBox();
        choices.getChildren().addAll(yesButton, noButton);
        choices.setSpacing(50);
        choices.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, choices);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(20);

        Scene scene = new Scene(layout, 300, 150);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
