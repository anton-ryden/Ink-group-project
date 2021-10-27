package InkGroupProject.model;

import javafx.scene.paint.Color;
import java.util.List;

/**
 * Interface that represents a region of countries. Mainly used for business regions.
 */
public interface CRegion {

    String name();

    List<Country> getCountries();

    void setColor(final Color COLOR);
}