package InkGroupProject.view;

import InkGroupProject.model.CountryPath;
import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import InkGroupProject.model.WorldBuilder;
import InkGroupProject.controller.World;
import InkGroupProject.controller.World.Resolution;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.*;
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
    private TextField donationValue;
    private VBox gradientLine;
    private CountryPath countryToDraw;
    private StackPane s;
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

        //***********GradientLine***********
        HBox gradientBar = new HBox();
        gradientBar.setPrefHeight(20);
        gradientBar.setMaxHeight(20);
        gradientBar.setPrefWidth(300);
        gradientBar.setMaxWidth(300);
        gradientBar.setStyle("-fx-background-color: linear-gradient(to right, #D9FFB3, #FF8C1A, #FF0000);");


        gradientBar.setSpacing(50);
        gradientBar.getChildren().add(new Text("0%"));
        gradientBar.getChildren().add(new Text("25%"));
        gradientBar.getChildren().add(new Text("50%"));
        gradientBar.getChildren().add(new Text("75%"));
        gradientBar.getChildren().add(new Text("100%"));
        gradientBar.setVisible(true);


        gradientLine = new VBox();
        gradientLine.setPrefHeight(40);
        gradientLine.setMaxHeight(40);
        gradientLine.setPrefWidth(300);
        gradientLine.setMaxWidth(300);
        gradientLine.getChildren().add(gradientBar);
        Text text = new Text("Percentage of population being poor");
        text.setStyle("-fx-font-size: 15px; -fx-stroke: white; -fx-stroke-width: 1px");
        gradientLine.setAlignment(Pos.CENTER);
        gradientLine.getChildren().add(text);

        //***********DonationPanel************//
        donationPanel = new VBox();
        donationPanel.setStyle("-fx-background-color: #ffffff");
        donationPanel.getStylesheets().add("./InkGroupProject/view/donationpanel.css");
        donationPanel.setPrefWidth(250);
        donationPanel.setMaxWidth(250);
        donationPanel.setVisible(true);


        donationValue = new TextField();
        donationValue.setPromptText("Enter how much you want to donate");

        donationButton = new Button("Donate");
        donationButton.setDisable(true);

        donationButton.setOnAction( e -> {
            try {
                int value = Integer.parseInt(donationValue.getText());
                if (value > 0) {
                    db.createDonation(UserSession.getInstance().getId(), selectedCountryPath.getDisplayName(), value);
                    donationPanel.getChildren().clear();

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
        root.add(gradientLine, 1, 0);
        GridPane.setHalignment(gradientLine, HPos.CENTER);
        GridPane.setValignment(gradientLine, VPos.BOTTOM);
        GridPane.setHgrow(worldMap, Priority.ALWAYS);
        GridPane.setVgrow(worldMap, Priority.ALWAYS);
    }

    /**
     *  Sets the scene ready for display
     * @param stage The window that contains all the javafx applications
     */
    public void start(Stage stage) {
        Scene mapScene = new Scene(root);
        stage.setX(Screen.getPrimary().getBounds().getWidth()/5);
        stage.setY(Screen.getPrimary().getBounds().getHeight()/5);
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
        int userX = -90;
        int userY = -420;

        Text username = new Text("Username:" + UserSession.getInstance().getFirstName());
        donationPanel.getChildren().add(username);
        username.setTranslateX(userX);
        username.setTranslateY(userY);

        Button mypage = new Button("MyPage");
        donationPanel.getChildren().add(mypage);
        mypage.setTranslateX(userX); //0 is middle
        mypage.setTranslateY(userY);
        mypage.setOnAction(e -> {
            //
        });

        Text country = new Text(countryPath.getDisplayName());
        donationPanel.getChildren().add(country);
        country.setTranslateY(-400);
        country.setFont(Font.font("arial", 30));

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
        donationPanel.getChildren().add(moneyCheckField);

        Label moneyCheckText = new Label();
        donationPanel.getChildren().add(moneyCheckText);
        moneyCheckText.setText("" + "\n" + "\n");

        Label donationsoFar = new Label(countryPath.getDisplayName() + " has been donated " + Database.getInstance(countryPath.getDisplayName()).getTotalDonatedMoney(countryPath.getDisplayName()) + "$ so far");
        donationsoFar.setTranslateY(0);
        donationsoFar.setWrapText(true);
        donationPanel.getChildren().add(donationsoFar);

        Button hide = new Button("Hide");
        informationPanel.getChildren().add(hide);
        hide.setTranslateX(90); //0 is middle
        hide.setTranslateY(-570);
        hide.setOnAction(e -> {
            informationPanel.setVisible(false);
        });


        donationValue.textProperty().addListener((obs, isUnfocused, isFocused) -> {
            double amount;

                try {
                    // Strip input of whitespace
                    String searchText = donationValue.getText().replaceAll("\\s+","");
                    amount = Double.parseDouble(searchText);
                } catch (NumberFormatException ex) {
                    amount = -1;
                }
                double cost = countryPath.getHealthyDietCost();
                int numberOfMeals = (int)(amount / cost);
                moneyCheckText.setTextFill(Color.BLACK);
                moneyCheckText.setText("Amount of Healthy meals:" + "\n" + numberOfMeals);
                moneyCheckText.setWrapText(true);

        });

    }

    /**
     * It updates the infopanel information with the new countrypath that should be displayed
     * @param countryPath the country data that should be displayed
     */
    public void updateInfoPanel(CountryPath countryPath) {
        int a = 90;
        informationPanel.setVisible(true);
        informationPanel.getChildren().clear();
        donationPanel.getChildren().clear();
        Text country = new Text(countryPath.getDisplayName());
        country.getStyleClass().add("country");
        donationPanel.getChildren().add(country);
        informationPanel.getChildren().add(country);

        int population = countryPath.getPopulation();
        if (population > 0) {
            drawCountry(countryPath);
            donationPanel.getChildren().add(donationValue);
            donationPanel.getChildren().add(donationButton);
            donationButton.setTranslateY(a);
            donationValue.setTranslateY(a);
            Label text = null;
            Integer poverty = countryPath.getPoverty();
            if(poverty == null){
                text = new Label("Population: " + population + ". There is no poverty data about this country");
            }else {
                String percentage = String.valueOf(Math.round((double) poverty * 100 / population));
                text = new Label("Population: " + population + " Around " + poverty + " lives with a salary less than $5.5 a day. " + "That is around " + percentage + "% of the population living in poverty");
            }text.setMaxWidth(160);
            text.setWrapText(true);
            informationPanel.getChildren().add(text);
            startGraph(countryPath);
            donationButton.setText("Donate to " + countryPath.getDisplayName());
            donationButton.setDisable(false);
        } else {
            informationPanel.getChildren().add(new Label("No data found"));
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
    private void drawCountry(CountryPath countryPath){
        countryToDraw = new CountryPath("", countryPath.getContent());
        countryToDraw.setStroke(Color.BLACK);
        countryToDraw.setFill(Color.WHITE);
        double countryHeight = countryToDraw.prefWidth(-1);
        Double scale = null;
        if (donationPanel.getWidth()/countryHeight < donationPanel.getWidth()/countryToDraw.prefHeight(countryHeight)){
            scale = donationPanel.getWidth()/countryHeight;
        }else {
            scale = donationPanel.getWidth()/countryToDraw.prefHeight(countryHeight);
        }
        countryToDraw.setScaleX(scale);
        countryToDraw.setScaleY(scale);

        double REQUIRED_WIDTH = 100.0;
        double REQUIRED_HEIGHT = 100.0;

        final Region svgShape = new Region();
        svgShape.setShape(countryToDraw);
        svgShape.setMinSize(REQUIRED_WIDTH, REQUIRED_HEIGHT);
        svgShape.setPrefSize(REQUIRED_WIDTH, REQUIRED_HEIGHT);
        svgShape.setMaxSize(REQUIRED_WIDTH, REQUIRED_HEIGHT);
        svgShape.setStyle("-fx-background-color: black;");
        if (s != null){
            donationPanel.getChildren().remove(s);
        }
        s = new StackPane(countryToDraw);
        s.setMinSize(donationPanel.getWidth(), donationPanel.getWidth());
        donationPanel.getChildren().add(s);
    }
}