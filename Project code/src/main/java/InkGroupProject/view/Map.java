package InkGroupProject.view;

import InkGroupProject.model.CountryPath;
import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import InkGroupProject.model.WorldBuilder;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import InkGroupProject.controller.World;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
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
    private GridPane informationPanel;
    private GridPane donationPanel;
    private Label donationPanelHeader;
    private Label infoPanelHeader;
    private Label moneyCheckText;
    private Label donationSoFar;
    private Text welcomeText;
    private Button myPageButton;
    private Button hideShowButton;
    private Button donationButton;
    private CountryPath selectedCountryPath;
    private TextField donationValue;
    private VBox gradientLine;
    private CountryPath countryToDraw;
    private StackPane stackPane;
    private UserSession currentUser;
    private Database db;

    /**
     * Constructor for that calls for initialization
     */
    public Map() {
        this.worldMap = createWorld();
        init();
        initDonationPanel();
        hideDonationPanel();
        initInformationPanel();
        initGradientLine();
    }

    private World createWorld() {
        World worldMap = WorldBuilder.create()
                .resolution(World.Resolution.HI_RES)
                .zoomEnabled(true)
                .hoverEnabled(true)
                .selectionEnabled(true)
                .fadeColors(true)
                .build();
        worldMap.addPropertyChangeListener(this);

        return worldMap;
    }

    /**
     * Initializes the root pane.
     */
    private void init() {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");
        currentUser = UserSession.getInstance();

        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #3f3f4f");

        root.add(worldMap, 1,0);
        GridPane.setHgrow(worldMap, Priority.ALWAYS);
        GridPane.setVgrow(worldMap, Priority.ALWAYS);
    }

    /**
     * Initializes the donation panel.
     */
    private void initDonationPanel() {
        donationPanel = new GridPane();
        donationPanel.getStylesheets().add("./InkGroupProject/view/donationpanel.css");
        donationPanel.getStyleClass().add("donation-panel");
        donationPanel.setMinWidth(250);
        donationPanel.setMaxWidth(250);
        donationPanel.setPrefWidth(250);
        donationPanel.setVisible(true);

        String userFullName = currentUser.getFirstName() + " " + currentUser.getLastName();
        welcomeText = new Text("Welcome, " + userFullName + "!");
        donationPanel.add(welcomeText, 0, 0);

        myPageButton = new Button("My page");
        donationPanel.add(myPageButton, 0, 1);
        myPageButton.setOnAction(e -> {
            UserPage userPage = new UserPage();
            userPage.start(Main.getStage());
        });

        hideShowButton = new Button("˄");
        donationPanel.add(hideShowButton, 1, 0);
        GridPane.setHalignment(hideShowButton, HPos.RIGHT);

        if (selectedCountryPath == null)
            hideShowButton.setDisable(true);

        hideShowButton.setOnAction(e -> {
            if (hideShowButton.getText().equals("˄")) {
                hideShowButton.setText("˅");
                updateDonationPanel(selectedCountryPath);
            } else {
                hideShowButton.setText("˄");
                hideDonationPanel();
            }
        });

        donationPanelHeader = new Label();
        donationPanelHeader.getStyleClass().add("donation-header");
        donationPanelHeader.setWrapText(true);
        donationPanel.add(donationPanelHeader, 0, 3, 2, 1);
        GridPane.setHalignment(donationPanelHeader, HPos.CENTER);

        stackPane = new StackPane();
        stackPane.setMinSize(donationPanel.getPrefWidth(), donationPanel.getPrefWidth());
        donationPanel.add(stackPane, 0, 4, 2, 1);
        GridPane.setHalignment(stackPane, HPos.CENTER);

        donationValue = new TextField();
        donationValue.setPromptText("Enter how much you want to donate");
        donationValue.setVisible(false);
        donationPanel.add(donationValue, 0, 5, 2, 1);

        donationButton = new Button("Donate");
        donationButton.setVisible(false);
        donationPanel.add(donationButton, 0, 6, 2, 1);
        GridPane.setHalignment(donationButton, HPos.CENTER);

        donationButton.setOnAction(e -> {
            try {
                int value = Integer.parseInt(donationValue.getText().replace(",", ""));
                if (value > 0) {
                    String countryName = selectedCountryPath.getDisplayName();
                    String donationAmountString = String.format("%,d", value);
                    db.createDonation(currentUser.getId(), countryName, value);

                    donationPanel.getChildren().clear();
                    donationPanel.setAlignment(Pos.CENTER);
                    Label confirmationText = new Label();
                    confirmationText.setTextFill(Color.GREEN);
                    confirmationText.setText("Thanks! $" + donationAmountString + " has been donated to\n" + countryName);
                    donationPanel.getChildren().add(confirmationText);
                } else {
                    throw new NumberFormatException("Negative value");
                }
            } catch (NumberFormatException exception) {
                System.err.println(exception.getMessage());
                moneyCheckText.setTextFill(Color.RED);
                moneyCheckText.setText("Invalid amount");
            }
        });

        moneyCheckText = new Label();
        donationPanel.add(moneyCheckText, 0, 7, 2, 1);

        donationSoFar = new Label();
        donationSoFar.setWrapText(true);
        donationPanel.add(donationSoFar, 0, 8, 2, 1);

        root.add(donationPanel, 0,0);
    }

    /**
     * Initializes the information panel.
     */
    private void initInformationPanel() {
        informationPanel = new GridPane();
        informationPanel.getStylesheets().add("./InkGroupProject/view/infopanel.css");
        informationPanel.setVisible(false);

        Button hideButton = new Button("Hide");
        hideButton.setOnAction(e -> {
            informationPanel.setVisible(false);
            // todo: also deselect the CountryPath (see mouse events in World)
        });
        informationPanel.add(hideButton, 1, 0);
        GridPane.setHalignment(hideButton, HPos.RIGHT);

        infoPanelHeader = new Label();
        infoPanelHeader.getStyleClass().add("info-header");
        infoPanelHeader.setWrapText(true);
        informationPanel.add(infoPanelHeader, 0, 1, 2, 1);
        GridPane.setHalignment(infoPanelHeader, HPos.CENTER);

        root.add(informationPanel, 2,0);
    }

    /**
     * Initializes the gradient line.
     */
    private void initGradientLine() {
        HBox gradientBar = new HBox();
        gradientBar.setPrefHeight(20);
        gradientBar.setMaxHeight(20);
        gradientBar.setPrefWidth(300);
        gradientBar.setMaxWidth(300);
        gradientBar.setSpacing(50);
        gradientBar.setStyle("-fx-background-color: linear-gradient(to right, #D9FFB3, #FF8C1A, #FF0000);");
        gradientBar.setPadding(new Insets(1, 5, 1, 5));

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
        gradientLine.setAlignment(Pos.CENTER);

        Text text = new Text("Percentage of population living in poverty");
        text.setStyle("-fx-font-size: 15px; -fx-stroke: white; -fx-stroke-width: 1px");
        gradientLine.getChildren().add(text);

        root.add(gradientLine, 1, 0);
        GridPane.setHalignment(gradientLine, HPos.CENTER);
        GridPane.setValignment(gradientLine, VPos.BOTTOM);
    }

    /**
     * Creates the graph in the info-panel
     * @param countryPath the country data that should be displayed
     */
    private void startGraph(CountryPath countryPath) {
        double percentage  = 0;
        double percentage2 = 0;
        double percentage3 = 0;
        int population = countryPath.getPopulation();
        if (countryPath.getPoverty() != null) {
            percentage  = (Math.round((double) countryPath.getNumberOfPoor19Dollar() * 100 / population));
            percentage2 = (Math.round((double) countryPath.getNumberOfPoor32Dollar() * 100 / population));
            percentage3 = (Math.round((double) countryPath.getNumberOfPoor55Dollar() * 100 / population));
        }

        selectedCountryPath = countryPath;

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setTitle("Percentage of population\nliving under a certain\namount of money");
        xAxis.setLabel("Living with $x money per day");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setAutoRanging(false);
        yAxis.getStyleClass().add("axis");
        yAxis.setTickLabelFill(Color.GREY);
        xAxis.setTickLabelFill(Color.GREY);
        XYChart.Series series1 = new XYChart.Series();

        series1.getData().add(new XYChart.Data("1.9", percentage));
        series1.getData().add(new XYChart.Data("3.2", percentage2));
        series1.getData().add(new XYChart.Data("5.5", percentage3));
        barChart.getData().addAll(series1);
        informationPanel.add(barChart, 0, 3, 2, 1);
        GridPane.setHalignment(barChart, HPos.CENTER);
    }

    private void hideDonationPanel() {
        root.getChildren().remove(donationPanel);
        donationPanel = new GridPane();
        donationPanel.getStylesheets().add("./InkGroupProject/view/donationpanel.css");
        donationPanel.getStyleClass().add("donation-panel");
        donationPanel.getStyleClass().add("donation-panel-hidden");
        donationPanel.add(welcomeText, 0, 0);
        GridPane.setHgrow(welcomeText, Priority.ALWAYS);
        donationPanel.add(myPageButton, 0, 1);
        donationPanel.add(hideShowButton, 1, 0);
        root.add(donationPanel, 0, 0);
        GridPane.setValignment(donationPanel, VPos.TOP);
    }

    /**
     * Updates the donation panel with information from the selected country.
     * @param countryPath the selected country.
     */
    private void updateDonationPanel(CountryPath countryPath) {
        hideDonationPanel();
        initDonationPanel();

        hideShowButton.setText("˅");
        hideShowButton.setDisable(false);

        String countryName = countryPath.getDisplayName();
        donationPanelHeader.setText(countryName);

        drawCountry(countryPath);

        donationValue.setVisible(true);
        donationValue.setTextFormatter(new TextFormatter<Integer>(c -> {
            try {
                if (!c.isContentChange()) {
                    return c;
                }
                String newText = c.getControlNewText();
                if (newText.isEmpty()) {
                    return c;
                }
                String modifiedText = String.format("%,d", Integer.parseInt(newText.replace(",", "")));
                c.setRange(0, c.getControlText().length());
                c.setText(modifiedText);
                c.setCaretPosition(modifiedText.length());
                c.setAnchor(modifiedText.length());

                return c;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        donationValue.textProperty().addListener((obs, oldValue, newValue) -> {
            int amount;
            try {
                amount = Integer.parseInt(newValue.replace(",", ""));
            } catch (NumberFormatException ex) {
                amount = 0;
            }

            double cost = countryPath.getHealthyDietCost();
            int numberOfMeals = (int)(amount / cost);
            String numberOfMealsString = String.format("%,d", numberOfMeals);
            if (cost > 0) {
                moneyCheckText.setTextFill(Color.BLACK);
                moneyCheckText.setText("Number of healthy meals: " + numberOfMealsString);
            }
        });

        donationButton.setText("Donate to " + countryName);
        donationButton.setVisible(true);

        if (countryPath.getHealthyDietCost() > 0) {
            moneyCheckText.setText("Number of healthy meals: 0");
        } else {
            donationButton.setDisable(true);
            moneyCheckText.setTextFill(Color.RED);
            moneyCheckText.setText("No price data for healthy meals was found for " + countryName);
            moneyCheckText.setWrapText(true);
        }
        String totalDonationString = String.format("%,d", db.getTotalDonatedMoney(countryName));
        donationSoFar.setText(countryName + " has been donated $" + totalDonationString + " so far");
    }

    /**
     * It updates the infopanel information with the new countrypath that should be displayed
     * @param countryPath the country data that should be displayed
     */
    private void updateInfoPanel(CountryPath countryPath) {
        root.getChildren().remove(informationPanel);
        initInformationPanel();
        informationPanel.setVisible(true);

        String countryName = countryPath.getDisplayName();
        infoPanelHeader.setText(countryName);

        int population = countryPath.getPopulation();
        String populationString = String.format("%,d", population);
        Label text = new Label();
        Integer poverty = countryPath.getPoverty();
        String povertyString = String.format("%,d", poverty);
        if (poverty == null) {
            text.setTextFill(Color.RED);
            text.setText("No poverty data was found for " + countryName + ".");
        } else if (poverty == 0) {
            text.setTextFill(Color.BLACK);
            text.setText("Population: " + populationString + "\nThere are no records of poverty for " + countryName + ".");
        } else {
            text.setTextFill(Color.BLACK);
            String percentage = String.valueOf(Math.round((double) poverty * 100 / population));
            text.setText("Population: " + populationString + "\nAround " + povertyString + " lives with a salary less than $5.5 a day. "
                    + "That is around " + percentage + "% of the population living in poverty.");
        }
        text.setMaxWidth(200);
        text.setWrapText(true);
        informationPanel.add(text, 0, 2, 2, 1);
        GridPane.setHalignment(text, HPos.CENTER);

        startGraph(countryPath);
    }

    /**
     * This method gets called whenever a new country is selected on the map and new information should be displayed
     * @param evt the event that contains the new value
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CountryPath countryPath = (CountryPath) evt.getNewValue();
        updateDonationPanel(countryPath);
        updateInfoPanel(countryPath);
    }

    /**
     * Draw the outline of the selected country to the donation panel.
     * @param countryPath the selected country.
     */
    private void drawCountry(CountryPath countryPath) {
        countryToDraw = new CountryPath("", countryPath.getContent());
        countryToDraw.setStroke(Color.BLACK);
        countryToDraw.setFill(Color.WHITE);

        double dpWidth = donationPanel.getPrefWidth();
        double countryHeight = countryToDraw.prefWidth(-1);
        Double scale;
        if (dpWidth / countryHeight < dpWidth / countryToDraw.prefHeight(countryHeight)) {
            scale = dpWidth / countryHeight;
        } else {
            scale = dpWidth / countryToDraw.prefHeight(countryHeight);
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

        if (stackPane != null)
            donationPanel.getChildren().remove(stackPane);

        stackPane = new StackPane(countryToDraw);
        stackPane.setMinSize(dpWidth, dpWidth);
        stackPane.setMaxSize(dpWidth, dpWidth);
        donationPanel.add(stackPane, 0, 4, 2, 1);
        GridPane.setHalignment(stackPane, HPos.CENTER);
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
}