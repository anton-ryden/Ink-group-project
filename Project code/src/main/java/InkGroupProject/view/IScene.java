package InkGroupProject.view;

import javafx.scene.Parent;
import javafx.stage.Stage;

public interface IScene {
    void start(Stage stage);
    Parent getRoot();
}
