package eu.hansolo.fx.worldheatmap;

import eu.hansolo.fx.world.World;
import eu.hansolo.fx.world.World.Resolution;
import eu.hansolo.fx.world.WorldBuilder;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;



public class Main extends Application {
    private StackPane     pane;
    private World         worldMap;
    private List<Point2D> cities;

    @Override public void init() {
        try { cities = readCitiesFromFile(); } catch (IOException e) { cities = new ArrayList<>(); }

        worldMap = WorldBuilder.create()
                               .resolution(Resolution.HI_RES)
                               .zoomEnabled(true)
                               .hoverEnabled(true)
                               .selectionEnabled(true)
                               .fadeColors(true)

                               .build();

        pane = new StackPane(worldMap);
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);

        stage.setTitle("Interactive map");
        stage.setScene(scene);
        stage.show();
    }

    private List<Point2D> readCitiesFromFile() throws IOException {
        List<Point2D>  cities     = new ArrayList<>(8092);
        String         citiesFile = (Main.class.getResource("cities.txt").toExternalForm()).replace("file:", "");
        citiesFile = citiesFile.replace(":", "");
        Stream<String> lines      = Files.lines(Paths.get(citiesFile));
        lines.forEach(line -> {
            String city[] = line.split(",");
            double[] xy = World.latLonToXY(Double.parseDouble(city[1]), Double.parseDouble(city[2]));
            cities.add(new Point2D(xy[0], xy[1]));
        });
        lines.close();
        return cities;
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
