package InkGroupProject.view;

import InkGroupProject.model.CountryPath;
import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.controller.World;
import InkGroupProject.controller.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Class for the main scene where the interactive map, info-panel and donate panel is shown
 */
public class Map implements IScene, PropertyChangeListener {
    private World worldMap;
    private GridPane root;
    private VBox informationPanel;
    private VBox donationPanel;
    private Button donationButton;
    private CountryPath selectedCountryPath;
    Database db;

    /**
     * Constructor for that calls for initialization
     * @param worldMap the interactive map thats going to be shown
     */
    public Map(World worldMap) {
        this.worldMap = worldMap;
        init();
    }

    /**
     * Initializes all values.
     * Adds worldmap, info-panel and donate-panel
     */
    public void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");
        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #3f3f4f");


        //***********InfoPanel***********//
        informationPanel = new VBox();
        informationPanel.setStyle("-fx-background-color: #ffffff");
        informationPanel.getStylesheets().add("./InkGroupProject/view/infopanel.css");
        informationPanel.setPrefWidth(250);
        informationPanel.setMaxWidth(250);
        informationPanel.setVisible(false);

        //***********DonationPanel************//
        donationPanel = new VBox();
        donationPanel.setStyle("-fx-background-color: #ffffff");
        donationPanel.getStylesheets().add("./InkGroupProject/view/donationpanel.css");
        donationPanel.setPrefWidth(250);
        donationPanel.setMaxWidth(250);
        donationPanel.setVisible(true);

        TextField donationValue = new TextField();
        donationValue.setPromptText("Enter how much you want to donate");
        donationPanel.getChildren().add(donationValue);
        donationButton = new Button("Donate");
        donationButton.setDisable(true);
        donationPanel.getChildren().add(donationButton);
        donationButton.setOnAction( e -> {
            try {
                int value = Integer.parseInt(donationValue.getText());
                if (value > 0) {
                    db.createDonation(UserSession.getInstance().getId(), selectedCountryPath.getDisplayName(), value);
                    donationPanel.getChildren().clear();
                    donationPanel.getChildren().add(donationButton);
                    donationPanel.getChildren().add(donationValue);
                    donationPanel.getChildren().add(new Text("Thanks! "+ donationValue.getText() + "$ has been donated to\n" + selectedCountryPath.getDisplayName()));
                } else {
                    throw new NumberFormatException("Negative value");
                }
            }

            catch (NumberFormatException exception) {
                donationPanel.getChildren().clear();
                donationPanel.getChildren().add(donationButton);
                donationPanel.getChildren().add(donationValue);
                donationPanel.getChildren().add(new Text("An error occurred"));
            }
        });



        //Add BarGraph
        root.add(worldMap, 1,0);
        root.add(donationPanel, 0,0);
        root.add(informationPanel, 2,0);
        GridPane.setHgrow(worldMap, Priority.ALWAYS);
        GridPane.setVgrow(worldMap, Priority.ALWAYS);
    }

    /**
     *  Sets the scene ready for display
     * @param stage The window that contains all the javafx applications
     */
    public void start(Stage stage) {
        Scene mapScene = new Scene(root);
        stage.setTitle("Interactive Map");
        stage.setResizable(true);
        stage.setScene(mapScene);
        stage.show();
    }

    /**
     *
     * @return the root of the map class.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Creates the graph in the info-panel
     * @param countryPath the country data that should be displayed
     */
    public void startGraph(CountryPath countryPath) {
        selectedCountryPath = countryPath;
        informationPanel.getStyleClass();
        int population = countryPath.getPopulation();
        double percentage = (Math.round((double) countryPath.getNumberOfPoor19Dollar() * 100 / population));
        double percentage2 = (Math.round((double) countryPath.getNumberOfPoor32Dollar() * 100 / population));
        double percentage3 = (Math.round((double) countryPath.getNumberOfPoor55Dollar() * 100 / population));
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Percentage of population\nliving under a certain\namount of money");
        xAxis.setLabel("Living with $x money per day");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setAutoRanging(false);
        yAxis.getStyleClass().add("axis");
        yAxis.setTickLabelFill(Color.GREY);
        xAxis.setTickLabelFill(Color.GREY);
        XYChart.Series series1 = new XYChart.Series();

        series1.getData().add(new XYChart.Data("1.9",percentage));
        series1.getData().add(new XYChart.Data("3.2", percentage2));
        series1.getData().add(new XYChart.Data("5.5",percentage3));

        bc.getData().addAll(series1);
        informationPanel.getChildren().add(bc);
        informationPanel.getChildren().add(new Label("Check what your donation could purchase"));

        HBox moneyCheckField = new HBox();
        final TextField search = new TextField();
        search.setPromptText("Enter amount of USD");
        search.setFocusTraversable(false);
        moneyCheckField.getChildren().add(search);
        Button check = new Button("Check");
        moneyCheckField.getChildren().add(check);
        informationPanel.getChildren().add(moneyCheckField);

        Label moneyCheckText = new Label();
        informationPanel.getChildren().add(moneyCheckText);

        Button hide = new Button("Hide");
        informationPanel.getChildren().add(hide);
        hide.setTranslateX(90); //0 is middle
        hide.setTranslateY(-600);
        hide.setOnAction(e -> {
            informationPanel.setVisible(false);
        });

        search.focusedProperty().addListener((obs, isUnfocused, isFocused) -> {
            if (isFocused) {
                check.setDefaultButton(true);
            } else {
                check.setDefaultButton(false);
            }
        });

        check.setOnAction(e -> {
            double amount;
            try {
                // Strip input of whitespace
                String searchText = search.getText().replaceAll("\\s+","");
                amount = Double.parseDouble(searchText);
            } catch (NumberFormatException ex) {
                amount = -1;
            }
            double cost = countryPath.getHealthyDietCost();
            int numberOfMeals = (int)(amount / cost);

            if (amount < 0 || cost < 0) {
                moneyCheckText.setTextFill(Color.RED);
                moneyCheckText.setText("Invalid number");
            } else {
                moneyCheckText.setTextFill(Color.BLACK);
                moneyCheckText.setText("Healthy meals: " + numberOfMeals);
            }
        });
    }

    /**
     * It updates the infopanel information with the new countrypath that should be displayed
     * @param countryPath the country data that should be displayed
     */
    public void updateInfoPanel(CountryPath countryPath) {
        informationPanel.setVisible(true);
        informationPanel.getChildren().clear();
        Text country = new Text(countryPath.getDisplayName());
        country.getStyleClass().add("country");

        informationPanel.getChildren().add(country);
        int population = countryPath.getPopulation();
        if (population > 0) {
            Label text = null;
            Integer poverty = countryPath.getPoverty();
            if(poverty == null){
                text = new Label("Population: " + population + ". There is no poverty data about this country");
            }else {
                String percentage = String.valueOf(Math.round((double) poverty * 100 / population));
                text = new Label("Population: " + population + " Around " + poverty + " lives with a salary less than $5.5 a day. " + "That is around " + percentage + "% of the population living in poverty");
            }


            text.setMaxWidth(160);
            text.setWrapText(true);
            informationPanel.getChildren().add(text);
            if (poverty != null){
                startGraph(countryPath);
            }
            donationButton.setText("Donate to " + countryPath.getDisplayName());
            donationButton.setDisable(false);
        } else {
            informationPanel.getChildren().add(new Label("There is no data about this country"));
            donationButton.setText("Donate");
            donationButton.setDisable(true);
        }
    }

    /**
     * This method gets called whenever a new country is selected on the map and new information should be displayed
     * @param evt the event that contains the new value
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CountryPath countryPath = (CountryPath) evt.getNewValue();
        updateInfoPanel(countryPath);
    }
}