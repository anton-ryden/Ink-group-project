package InkGroupProject.view;

import InkGroupProject.model.InfoModel;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.controller.World;
import InkGroupProject.controller.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.application.Application;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.xml.stream.events.StartDocument;
import java.awt.*;


public class Map implements IScene {
    private World worldMap;
    private GridPane root;
    private VBox informationPanel;
    private InfoModel model;

    public Map() {
        init();
    }


    final static String one = "1.9";
    final static String two = "3.2";
    final static String three = "5.5";



    public void init() {
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
        informationPanel.getStylesheets().add("./InkGroupProject/view/infopanel.css");
        model = new InfoModel();
        worldMap.linkInformationPanel(model, this);
        informationPanel.setPrefWidth(250);

        //Add BarGraph
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


    public void startGraph() {
        informationPanel.getStyleClass();
        int population = Integer.parseInt(InfoModel.getPopulation());
        int poverty = ((int) (Double.parseDouble(InfoModel.getPoverty(0)) * 1000000));
        int poverty2 = ((int) (Double.parseDouble(InfoModel.getPoverty(1)) * 1000000));
        int poverty3 = ((int) (Double.parseDouble(InfoModel.getPoverty(2)) * 1000000));
        double percentage = (Math.round((double) poverty * 100 / population));
        double percentage2 = (Math.round((double) poverty2 * 100 / population));
        double percentage3 = (Math.round((double) poverty3 * 100 / population));
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Percentage of population\n living under a certain\n amount of money");
        xAxis.setLabel("Living with x$ money per day");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setAutoRanging(false);
        yAxis.getStyleClass().add("axis");
        yAxis.setTickLabelFill(Color.WHITE);
        xAxis.setTickLabelFill(Color.WHITE);

        XYChart.Series series1 = new XYChart.Series();
        //series1.getData().add(new XYChart.Data(,100));
        series1.getData().add(new XYChart.Data(one,percentage));
        series1.getData().add(new XYChart.Data(two, percentage2));
        series1.getData().add(new XYChart.Data(three,percentage3));

        bc.getData().addAll(series1);
        informationPanel.getChildren().add(bc);
        informationPanel.getChildren().add(new Label("Check what your donation could purchase"));

        final TextField search = new TextField();
        search.setPromptText("Enter amount of $");
        search.setFocusTraversable(false);
        informationPanel.getChildren().add(search);
        Button check = new Button("Check");
        informationPanel.getChildren().add(check);



    }

    public void updateInfoPanel() {

        informationPanel.getChildren().clear();
        Text country = new Text(InfoModel.getCountry());
        country.getStyleClass().add("country");

        informationPanel.getChildren().add(country);
        try {
            int population = Integer.parseInt(InfoModel.getPopulation());
            informationPanel.getChildren().add(new Label("Population: " + population));

            //informationPanel.getChildren().add(new Text("Around " + String.valueOf((int) (Double.parseDouble(InfoModel.getPoverty(0))*1000000)) +" lives with a salary less than 1.9$ a day"));
            int poverty = ((int) (Double.parseDouble(InfoModel.getPoverty(0)) * 1000000));
            String percentage = String.valueOf(Math.round((double) poverty * 100 / population));
            informationPanel.getChildren().add(new Label("Around " + poverty + " lives with a salary \n less than 1.9$ a day"));
            //informationPanel.getChildren().add(new Text("Around " + String.valueOf((int) (Double.parseDouble(InfoModel.getPoverty(2))*1000000)) +" lives with a salary less than 5.5$ a day"));
            informationPanel.getChildren().add(new Label("That is around " + percentage + "% of the population\n living in poverty"));
            startGraph();
        } catch (NumberFormatException e) {
            informationPanel.getChildren().add(new Label("There was an error fetching data"));
        }





    }

}