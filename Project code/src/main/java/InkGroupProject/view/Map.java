package InkGroupProject.view;

import InkGroupProject.model.InfoModel;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.controller.World;
import InkGroupProject.controller.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

public class Map implements IScene {
    private World worldMap;
    private GridPane root;
    private VBox informationPanel;
    private InfoModel model;

    public Map() {
        init();
    }

    private void init() {
        worldMap = WorldBuilder.create()
                .resolution(Resolution.HI_RES)
                .zoomEnabled(true)
                .hoverEnabled(true)
                .selectionEnabled(true)
                .fadeColors(true)
                .build();

        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #3f3f4f");
        Button prevBtn = new Button("Previous");

        //***********InfoPanel***********//
        informationPanel = new VBox();
        model = new InfoModel();
        worldMap.linkInformationPanel(model, this);
        Text population = new Text( "Population: ");
        informationPanel.setPrefWidth(200);
        informationPanel.getChildren().add(population);

        root.add(worldMap, 1,0);
        root.add(prevBtn, 0,0);
        root.add(informationPanel, 2,0);
        GridPane.setHgrow(worldMap, Priority.ALWAYS);
        GridPane.setVgrow(worldMap, Priority.ALWAYS);

    }

    public void start(Stage stage) {
        Scene mapScene = new Scene(root);
        stage.setTitle("Interactive Map");
        stage.setResizable(true);
        stage.setScene(mapScene);
        stage.show();
    }

    public Parent getRoot() {
        return root;
    }

    public void updateInfoPanel() {
        informationPanel.getChildren().clear();
        informationPanel.getChildren().add(new Text(InfoModel.getCountry()));
        try {
            int population = Integer.parseInt(InfoModel.getPopulation());
            informationPanel.getChildren().add(new Text("Population: " + population));
            //informationPanel.getChildren().add(new Text("Around " + String.valueOf((int) (Double.parseDouble(InfoModel.getPoverty(0))*1000000)) +" lives with a salary less than 1.9$ a day"));
            int poverty = ((int) (Double.parseDouble(InfoModel.getPoverty(0)) * 1000000));
            String percentage = String.valueOf(Math.round((double) poverty * 100 / population));
            informationPanel.getChildren().add(new Text("Around " + poverty + " lives with a salary less than 1.9$ a day"));
            //informationPanel.getChildren().add(new Text("Around " + String.valueOf((int) (Double.parseDouble(InfoModel.getPoverty(2))*1000000)) +" lives with a salary less than 5.5$ a day"));
            informationPanel.getChildren().add(new Text("That is around " + percentage + "% of the population living in poverty"));
        } catch (NumberFormatException e) {
            informationPanel.getChildren().add(new Text("There was an error fetching data"));
        }

    }

}
