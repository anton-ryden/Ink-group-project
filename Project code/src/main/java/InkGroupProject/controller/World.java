/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package InkGroupProject.controller;

import InkGroupProject.model.*;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static javafx.scene.input.MouseEvent.*;

/**
 * This class represents an interactive map
 */
@DefaultProperty("children")
public class World extends Region {
    public enum Resolution { HI_RES, LO_RES };
    private static final StyleablePropertyFactory<World> FACTORY          = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final String                          HIRES_PROPERTIES = "InkGroupProject/controller/hires.properties";
    private static final String                          LORES_PROPERTIES = "InkGroupProject/controller/lores.properties";
    private static final double                          PREFERRED_WIDTH  = 1009;
    private static final double                          PREFERRED_HEIGHT = 665;
    private static final double                          MINIMUM_WIDTH    = 100;
    private static final double                          MINIMUM_HEIGHT    = 66;
    private static final double                          MAXIMUM_WIDTH    = 2018;
    private static final double                          MAXIMUM_HEIGHT   = 1330;
    private static       double                          MAP_OFFSET_X     = -PREFERRED_WIDTH * 0.0285;
    private static       double                          MAP_OFFSET_Y     = PREFERRED_HEIGHT * 0.195;
    private static final double                          ASPECT_RATIO     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private static final CssMetaData<World, Color>       BACKGROUND_COLOR = FACTORY.createColorCssMetaData("-background-color", s -> s.backgroundColor, Color.web("#3f3f4f"), false);
    private        final StyleableProperty<Color>        backgroundColor;
    private static final CssMetaData<World, Color>       FILL_COLOR = FACTORY.createColorCssMetaData("-fill-color", s -> s.fillColor, Color.web("#d9d9dc"), false);
    private        final StyleableProperty<Color>        fillColor;
    private static final CssMetaData<World, Color>       STROKE_COLOR = FACTORY.createColorCssMetaData("-stroke-color", s -> s.strokeColor, Color.BLACK, false);
    private        final StyleableProperty<Color>        strokeColor;
    private static final CssMetaData<World, Color>       HOVER_COLOR = FACTORY.createColorCssMetaData("-hover-color", s -> s.hoverColor, Color.web("#456acf"), false);
    private        final StyleableProperty<Color>        hoverColor;
    private static final CssMetaData<World, Color>       PRESSED_COLOR = FACTORY.createColorCssMetaData("-pressed-color", s -> s.pressedColor, Color.web("#789dff"), false);
    private        final StyleableProperty<Color>        pressedColor;
    private static final CssMetaData<World, Color>       SELECTED_COLOR = FACTORY.createColorCssMetaData("-selected-color", s-> s.selectedColor, Color.web("#9dff78"), false);
    private        final StyleableProperty<Color>        selectedColor;
    private static final CssMetaData<World, Color>       LOCATION_COLOR = FACTORY.createColorCssMetaData("-location-color", s -> s.locationColor, Color.web("#ff0000"), false);
    private        final StyleableProperty<Color>        locationColor;
    private              BooleanProperty                 hoverEnabled;
    private              BooleanProperty                 selectionEnabled;
    private              ObjectProperty<Country>         selectedCountry;
    private              BooleanProperty                 zoomEnabled;
    private              DoubleProperty                  scaleFactor;
    private              Properties                      resolutionProperties;
    private              Country                         formerSelectedCountry;
    private              double                          zoomSceneX;
    private              double                          zoomSceneY;
    private              double                          width;
    private              double                          height;
    private              Pane                            pane;
    private              Group                           group;
    private              Map<String, List<CountryPath>>  countryPaths;
    private              double                          eventRadius;
    private              boolean                         fadeColors;

    // internal event handlers
    protected            EventHandler<MouseEvent>        _mouseEnterHandler;
    protected            EventHandler<MouseEvent>        _mousePressHandler;
    protected            EventHandler<MouseEvent>        _mouseReleaseHandler;
    protected            EventHandler<MouseEvent>        _mouseExitHandler;
    private              EventHandler<ScrollEvent>       _scrollEventHandler;
    // exposed event handlers
    private              EventHandler<MouseEvent>        mouseEnterHandler;
    private              EventHandler<MouseEvent>        mousePressHandler;
    private              EventHandler<MouseEvent>        mouseReleaseHandler;
    private              EventHandler<MouseEvent>        mouseExitHandler;

    private CountryPath selectedCountryPath;
    private Database db;
    private double startDragX;
    private double startDragY;
    private double lastDragX;
    private double lastDragY;
    private boolean beingDragged;

    private PropertyChangeSupport support;

    // ******************** Constructors **************************************

    /**
     * Constructor when no resolution is specified
     */
    public World() {
        this(Resolution.HI_RES, 5, false );
    }

    /**
     * Constructor when the resolution is specified
     * @param RESOLUTION the resolution that the map is going to be displayed in
     */
    public World(final Resolution RESOLUTION) {
        this(RESOLUTION,  5, false  );
    }

    /**
     * Countructor for when resolution, event_radius and fade_color is specified
     * @param RESOLUTION the resolution that the map is going to be displayed in
     * @param EVENT_RADIUS The radius of the event
     * @param FADE_COLORS The fade color
     */
    public World(final Resolution RESOLUTION, final double EVENT_RADIUS, final boolean FADE_COLORS) {
        db = Database.getInstance(":resource:InkGroupProject/db/database.db");
        support = new PropertyChangeSupport(this);
        lastDragX = 0;
        lastDragY = 0;
        beingDragged = false;

        resolutionProperties = readProperties(Resolution.HI_RES == RESOLUTION ? World.HIRES_PROPERTIES : World.LORES_PROPERTIES);
        backgroundColor      = new StyleableObjectProperty<Color>(BACKGROUND_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setBackground(new Background(new BackgroundFill(get(), CornerRadii.EMPTY, Insets.EMPTY))); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "backgroundColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return BACKGROUND_COLOR; }
        };
        fillColor            = new StyleableObjectProperty<Color>(FILL_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setFillAndStroke(); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "fillColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return FILL_COLOR; }
        };
        strokeColor          = new StyleableObjectProperty<Color>(STROKE_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setFillAndStroke(); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "strokeColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return STROKE_COLOR; }
        };
        hoverColor           = new StyleableObjectProperty<Color>(HOVER_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "hoverColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return HOVER_COLOR; }
        };
        pressedColor         = new StyleableObjectProperty<Color>(PRESSED_COLOR.getInitialValue(this)) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "pressedColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return PRESSED_COLOR; }
        };
        selectedColor        = new StyleableObjectProperty<Color>(SELECTED_COLOR.getInitialValue(this)) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectedColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return SELECTED_COLOR; }
        };
        locationColor        = new StyleableObjectProperty<Color>(LOCATION_COLOR.getInitialValue(this)) {
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "locationColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return LOCATION_COLOR; }
        };
        hoverEnabled         = new BooleanPropertyBase(true) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "hoverEnabled"; }
        };
        selectionEnabled     = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectionEnabled"; }
        };
        selectedCountry      = new ObjectPropertyBase<Country>() {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectedCountry"; }
        };
        zoomEnabled          = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                if (null == getScene()) return;
                if (get()) {
                    getScene().addEventFilter(ScrollEvent.ANY, _scrollEventHandler);
                } else {
                    getScene().removeEventFilter(ScrollEvent.ANY, _scrollEventHandler);
                }
            }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "zoomEnabled"; }
        };
        scaleFactor          = new DoublePropertyBase(1.0) {
            @Override protected void invalidated() {
                if (isZoomEnabled()) {
                    setScaleX(get());
                    setScaleY(get());
                }
            }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "scaleFactor"; }
        };
        countryPaths         = createCountryPaths();
        eventRadius          = EVENT_RADIUS;
        fadeColors           = FADE_COLORS;
        pane                 = new Pane();
        group                = new Group();

        setOnMousePressed(event -> {
            startDragX = event.getSceneX() - lastDragX ;
            startDragY = event.getSceneY() - lastDragY;
            event.consume();
        });
        setOnMouseDragged(event -> {
            beingDragged = true;
            setTranslateX(event.getSceneX() - startDragX);
            setTranslateY(event.getSceneY() - startDragY);
            event.consume();
        });
        setOnMouseReleased(event -> {
            lastDragX = event.getSceneX()-startDragX;
            lastDragY = event.getSceneY()-startDragY;
            event.consume();
        });
        _mouseEnterHandler   = evt -> handleMouseEvent(evt, mouseEnterHandler);
        _mousePressHandler   = evt -> handleMouseEvent(evt, mousePressHandler);
        _mouseReleaseHandler = evt -> handleMouseEvent(evt, mouseReleaseHandler);
        _mouseExitHandler    = evt -> handleMouseEvent(evt, mouseExitHandler);
        _scrollEventHandler  = evt -> {
            if (group.getTranslateX() != 0 || group.getTranslateY() != 0) { resetZoom(); }
            double delta    = 1.2;
            double scale    = getScaleFactor();
            double oldScale = scale;
            scale           = evt.getDeltaY() < 0 ? scale / delta : scale * delta;
            scale           = clamp( 1, 10, scale);
            double factor   = (scale / oldScale) - 1;
            zoomSceneX = evt.getSceneX();
            zoomSceneY = evt.getSceneY();
            if (Double.compare(1, getScaleFactor()) == 0) {
                resetZoom();
            }
            double deltaX = (zoomSceneX - (getBoundsInParent().getWidth() / 2 + getBoundsInParent().getMinX()));
            double deltaY = (zoomSceneY - (getBoundsInParent().getHeight() / 2 + getBoundsInParent().getMinY()));
            setScaleFactor(scale);
            setPivot(deltaX * factor, deltaY * factor);

            lastDragX = getTranslateX();
            lastDragY = getTranslateY();
            evt.consume();
        };


        initGraphics();
        registerListeners();
    }

    // ******************** Initialization ************************************

    /**
     * Initializes everything that has to do with the graphics
     */
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
                Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("world");
        Color fill   = getFillColor();
        Color stroke = getStrokeColor();

        countryPaths.forEach((name, pathList) -> {
            Country country = Country.valueOf(name);
            pathList.forEach(path -> {
                //********Set color of country********
                try {
                    setCountryColor(country, path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                path.setFill(null == country.getColor() ? fill : country.getColor());
                path.setStroke(stroke);
                path.setStrokeWidth(0.2);
                path.setOnMouseEntered(new WeakEventHandler<>(_mouseEnterHandler));
                path.setOnMousePressed(new WeakEventHandler<>(_mousePressHandler));
                path.setOnMouseReleased(new WeakEventHandler<>(_mouseReleaseHandler));
                path.setOnMouseExited(new WeakEventHandler<>(_mouseExitHandler));
            });
            pane.getChildren().addAll(pathList);
        });
        group.getChildren().add(pane);
        getChildren().setAll(group);
        setBackground(new Background(new BackgroundFill(getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Registers all listeners
     */
    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        sceneProperty().addListener(o -> {
            if (isZoomEnabled()) { getScene().addEventFilter( ScrollEvent.ANY, new WeakEventHandler<>(_scrollEventHandler)); }
        });
    }

    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Map<String, List<CountryPath>> getCountryPaths() { return countryPaths; }

    public void setMouseEnterHandler(final EventHandler<MouseEvent> HANDLER) { mouseEnterHandler = HANDLER; }
    public void setMousePressHandler(final EventHandler<MouseEvent> HANDLER) { mousePressHandler = HANDLER; }
    public void setMouseReleaseHandler(final EventHandler<MouseEvent> HANDLER) { mouseReleaseHandler = HANDLER;  }
    public void setMouseExitHandler(final EventHandler<MouseEvent> HANDLER) { mouseExitHandler = HANDLER; }

    public Color getBackgroundColor() { return backgroundColor.getValue(); }
    public void setBackgroundColor(final Color COLOR) { backgroundColor.setValue(COLOR); }
    public ObjectProperty<Color> backgroundColorProperty() { return (ObjectProperty<Color>) backgroundColor; }

    public Color getFillColor() { return fillColor.getValue(); }
    public void setFillColor(final Color COLOR) { fillColor.setValue(COLOR); }
    public ObjectProperty<Color> fillColorProperty() { return (ObjectProperty<Color>) fillColor; }

    public Color getStrokeColor() { return strokeColor.getValue(); }
    public void setStrokeColor(final Color COLOR) { strokeColor.setValue(COLOR); }
    public ObjectProperty<Color> strokeColorProperty() { return (ObjectProperty<Color>) strokeColor; }

    public Color getHoverColor() { return hoverColor.getValue(); }
    public void setHoverColor(final Color COLOR) { hoverColor.setValue(COLOR); }
    public ObjectProperty<Color> hoverColorProperty() { return (ObjectProperty<Color>) hoverColor; }

    public Color getPressedColor() { return pressedColor.getValue(); }
    public void setPressedColor(final Color COLOR) { pressedColor.setValue(COLOR); }
    public ObjectProperty<Color> pressedColorProperty() { return (ObjectProperty<Color>) pressedColor; }

    public Color getSelectedColor() { return selectedColor.getValue(); }
    public void setSelectedColor(final Color COLOR) { selectedColor.setValue(COLOR); }
    public ObjectProperty<Color> selectedColorProperty() { return (ObjectProperty<Color>) selectedColor; }

    public Color getLocationColor() { return locationColor.getValue(); }
    public void setLocationColor(final Color COLOR) { locationColor.setValue(COLOR); }
    public ObjectProperty<Color> locationColorProperty() { return (ObjectProperty<Color>) locationColor; }

    public boolean isHoverEnabled() { return hoverEnabled.get(); }
    public void setHoverEnabled(final boolean ENABLED) { hoverEnabled.set(ENABLED); }
    public BooleanProperty hoverEnabledProperty() { return hoverEnabled; }

    public boolean isSelectionEnabled() { return selectionEnabled.get(); }
    public void setSelectionEnabled(final boolean ENABLED) { selectionEnabled.set(ENABLED); }
    public BooleanProperty selectionEnabledProperty() { return selectionEnabled; }

    public Country getSelectedCountry() { return selectedCountry.get(); }
    public void setSelectedCountry(final Country COUNTRY) { selectedCountry.set(COUNTRY); }
    public ObjectProperty<Country> selectedCountryProperty() { return selectedCountry; }

    public boolean isZoomEnabled() { return zoomEnabled.get(); }
    public void setZoomEnabled(final boolean ENABLED) { zoomEnabled.set(ENABLED); }
    public BooleanProperty zoomEnabledProperty() { return zoomEnabled; }

    public double getScaleFactor() { return scaleFactor.get(); }
    public void setScaleFactor(final double FACTOR) { scaleFactor.set(FACTOR); }
    public DoubleProperty scaleFactorProperty() { return scaleFactor; }

    /**
     * Resets zoom scale and sets all translates to 0
     */
    public void resetZoom() {
        setScaleFactor(1.0);
        setTranslateX(0);
        setTranslateY(0);
        group.setTranslateX(0);
        group.setTranslateY(0);
        lastDragX = 0;
        lastDragY = 0;
    }

    /**
     * Zoom to a specified country
     * @param COUNTRY Zoom to a specified country
     */
    public void zoomToCountry(final Country COUNTRY) {
        if (!isZoomEnabled()) return;
        if (null != getSelectedCountry()) {
            setCountryFillAndStroke(getSelectedCountry(), getFillColor(), getStrokeColor());
        }
        zoomToArea(getBounds(COUNTRY));
    }

    /**
     * Zoom to a specified country
     * @param REGION Zoom to a specified country
     */
    public void zoomToRegion(final CRegion REGION) {
        if (!isZoomEnabled()) return;
        if (null != getSelectedCountry()) {
            setCountryFillAndStroke(getSelectedCountry(), getFillColor(), getStrokeColor());
        }
        zoomToArea(getBounds(REGION.getCountries()));
    }

    /**
     * Method for getting the bounds of multiple countries
     * @param COUNTRIES the countries that you want the bounds from
     * @return bounds from COUNTRIES
     */
    private double[] getBounds(final Country... COUNTRIES) { return getBounds(Arrays.asList(COUNTRIES)); }

    /**
     * Method for getting the bounds of multiple countries
     * @param COUNTRIES the countries that you want the bounds from in list form
     * @return bounds from COUNTRIES
     */
    private double[] getBounds(final List<Country> COUNTRIES) {
        double upperLeftX  = PREFERRED_WIDTH;
        double upperLeftY  = PREFERRED_HEIGHT;
        double lowerRightX = 0;
        double lowerRightY = 0;
        for (Country country : COUNTRIES) {
            List<CountryPath> paths = countryPaths.get(country.getName());
            for (int i = 0; i < paths.size(); i++) {
                CountryPath path   = paths.get(i);
                Bounds      bounds = path.getLayoutBounds();
                upperLeftX  = Math.min(bounds.getMinX(), upperLeftX);
                upperLeftY  = Math.min(bounds.getMinY(), upperLeftY);
                lowerRightX = Math.max(bounds.getMaxX(), lowerRightX);
                lowerRightY = Math.max(bounds.getMaxY(), lowerRightY);
            }
        }
        return new double[]{ upperLeftX, upperLeftY, lowerRightX, lowerRightY };
    }

    /**
     * Zooms to a specific area.
     * @param BOUNDS the bounds of the area that is to be zoomed into.
     */
    private void zoomToArea(final double[] BOUNDS) {
        resetZoom();
        group.setTranslateX(0);
        group.setTranslateY(0);
        double      offset      = width / PREFERRED_WIDTH;
        double      areaWidth   = (BOUNDS[2] - BOUNDS[0])*offset;
        double      areaHeight  = (BOUNDS[3] - BOUNDS[1])*offset;
        double      areaCenterX = BOUNDS[0]*offset + areaWidth * 0.5;
        double      areaCenterY = BOUNDS[1]*offset + areaHeight * 0.5;
        Orientation orientation = areaWidth < areaHeight ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        double sf = 1.0;
        switch(orientation) {
            case VERTICAL  : sf = clamp(1.0, 10.0, 1 / (areaHeight / height)); break;
            case HORIZONTAL: sf = clamp(1.0, 10.0, 1 / (areaWidth / width)); break;
        }

        setScaleFactor(sf);
        group.setTranslateX(width * 0.5 - (areaCenterX));
        group.setTranslateY(height * 0.5 - (areaCenterY));
        lastDragX = group.getTranslateX();
        lastDragY = group.getTranslateY();
    }

    /**
     * Sets a pivot. Mainly used when zooming.
     * @param X the pivot in the x-axis.
     * @param Y the pivot in the y-axis.
     */
    private void setPivot(final double X, final double Y) {
        setTranslateX(getTranslateX() - X);
        setTranslateY(getTranslateY() - Y);
    }

    /**
     * Manages the mouse events by doing specific things depending on the event.
     * @param EVENT the event registered from the users mouse.
     * @param HANDLER the handler that is to be used from the given event.
     */
    private void handleMouseEvent(final MouseEvent EVENT, final EventHandler<MouseEvent> HANDLER) {
        final CountryPath COUNTRY_PATH = (CountryPath) EVENT.getSource();
        final String            COUNTRY_NAME = COUNTRY_PATH.getName();
        final Country           COUNTRY      = Country.valueOf(COUNTRY_NAME);
        final List<CountryPath> PATHS        = countryPaths.get(COUNTRY_NAME);

        final EventType TYPE = EVENT.getEventType();
        if (MOUSE_RELEASED == TYPE) {
            if (!beingDragged){
                setCountryPath(COUNTRY_PATH);
                if (isSelectionEnabled()) {
                    zoomToCountry(COUNTRY);
                    Color color;
                    if (null == getSelectedCountry()) {
                        setSelectedCountry(COUNTRY);
                        color = getSelectedColor();
                    } else {
                        color = null == getSelectedCountry().getColor() ? getFillColor() : getSelectedCountry().getColor();
                    }
                    for (SVGPath path : countryPaths.get(getSelectedCountry().getName())) {
                        path.setFill(color);
                    }
                } else {
                    if (isHoverEnabled()) {
                        for (SVGPath path : PATHS) { path.setFill(getPressedColor()); }
                    }
                }
                Color color;
                if (isSelectionEnabled()) {
                    if (formerSelectedCountry == COUNTRY) {
                        setSelectedCountry(null);
                        color = null == COUNTRY.getColor() ? getFillColor() : COUNTRY.getColor();
                    } else {
                        setSelectedCountry(COUNTRY);
                        color = getSelectedColor();
                    }
                    formerSelectedCountry = getSelectedCountry();
                } else {
                    color = getHoverColor();
                }
                if (isHoverEnabled()) {
                    for (SVGPath path : PATHS) { path.setFill(color); }
                }
            }
            beingDragged = false;
        } else if (MOUSE_ENTERED == TYPE) {
            if (isHoverEnabled()) {
                Color color = isSelectionEnabled() && COUNTRY.equals(getSelectedCountry()) ? getSelectedColor() : getHoverColor();
                for (SVGPath path : PATHS) { path.setFill(color); }
            }

        } else if (MOUSE_EXITED == TYPE) {
            if (isHoverEnabled()) {
                Color color = isSelectionEnabled() && COUNTRY.equals(getSelectedCountry()) ? getSelectedColor() : getFillColor();
                for (SVGPath path : PATHS) {
                    path.setFill(null == COUNTRY.getColor() || COUNTRY == getSelectedCountry() ? color : COUNTRY.getColor());
                }
            }
        }
        if (null != HANDLER) HANDLER.handle(EVENT);
    }

    /**
     * Sets the color specifications of countries.
     */
    private void setFillAndStroke() {
        countryPaths.keySet().forEach(name -> {
            Country country = Country.valueOf(name);
            setCountryFillAndStroke(country, null == country.getColor() ? getFillColor() : country.getColor(), getStrokeColor());
        });
    }

    /**
     * Sets the color specifications of a country.
     * @param COUNTRY the country.
     * @param FILL the filling color.
     * @param STROKE the stroke color.
     */
    private void setCountryFillAndStroke(final Country COUNTRY, final Color FILL, final Color STROKE) {
        List<CountryPath> paths = countryPaths.get(COUNTRY.getName());
        for (CountryPath path : paths) {
            path.setFill(FILL);
            path.setStroke(STROKE);
        }
    }

    /**
     * Make sure no value can surpass min and max value
     * @param MIN minimum value
     * @param MAX maximum value
     * @param VALUE the value you want to check
     * @return value if it's in the region of min and max otherwise it returns min or max
     */
    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    /**
     * Reads properties from a file
     * @param FILE_NAME where the properties are stored
     * @return
     */
    private Properties readProperties(final String FILE_NAME) {
        final ClassLoader LOADER     = Thread.currentThread().getContextClassLoader();
        final Properties  PROPERTIES = new Properties();
        try(InputStream resourceStream = LOADER.getResourceAsStream(FILE_NAME)) {
            PROPERTIES.load(resourceStream);
        } catch (IOException exception) {
            System.out.println(exception);
        }
        return PROPERTIES;
    }

    /**
     * Creates the country paths.
     * @return the created country paths.
     */
    private Map<String, List<CountryPath>> createCountryPaths() {
        Map<String, List<CountryPath>> countryPaths = new HashMap<>();

        resolutionProperties.forEach((key, value) -> {
            String name = key.toString();
            List<CountryPath> pathList = new ArrayList<>();
            for (String path : value.toString().split(";")) {

                pathList.add(loadCountryData(new CountryPath(name, path)));
            }
            countryPaths.put(name, pathList);
        });
        return countryPaths;
    }

    // ******************** Style related *************************************

    /**
     * Gets the user agent style sheet.
     * @return the user agent style sheet.
     */
    @Override public String getUserAgentStylesheet() {
        return World.class.getResource("world.css").toExternalForm();
    }

    /**
     * gets the class css meta data
     * @return the class css meta data
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }

    /**
     * gets the css meta data
     * @return the css meta data
     */
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }


    // ******************** Resizing ******************************************

    /**
     * Resizes and relocate the window
     */
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            if (isZoomEnabled()) resetZoom();

            pane.setCache(true);
            pane.setCacheHint(CacheHint.SCALE);

            pane.setScaleX(width / PREFERRED_WIDTH);
            pane.setScaleY(height / PREFERRED_HEIGHT);
            group.resize(width, height);

            group.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
            pane.setCache(false);
        }
    }
    // ***************** Information Panel ****************************//

    /**
     * Sets the color of a country depending on how poor the country is
     * @param country the country you want to set the coolor for
     * @param path of the country. Contains all the information about poverty info.
     * @throws FileNotFoundException whenever a country is not found
     */
    private void setCountryColor(Country country, CountryPath path) throws FileNotFoundException {
        Integer population;
        Integer poverty;
        double povertyIndex;
        double h;
        double s;
        double b;
        Color myColor;
        try {
            population = path.getPopulation();
            poverty = path.getPoverty();
            if (population == 0){
                country.setColor(Color.GREY);
            } else if (poverty == null){
                country.setColor(Color.WHITE);
            } else if(country.getColor() == null) {

                povertyIndex = ((double) (poverty)) / ((double) population);
                s = povertyIndex * 2 + 0.3;
                b = 1;
                //h = 90 - (povertyIndex)*90;
                if(povertyIndex >= 0.2) {
                    h = 54 - povertyIndex * 54 * 0.85;
                }else{
                    h = 90 - (povertyIndex*2)*90;
                }
                if (s > 1){
                    s = 1;
                }
                myColor = Color.hsb(h, s, b);
                country.setColor(myColor);
                //country.setColor(new Color(Math.min(povertyIndex*1.5,1), Math.max(1 - povertyIndex*5, 0), 0, 1));
            }
        } catch (NumberFormatException e) {
            country.setColor(Color.GREY);
        }
    }

    // ***************** Observer pattern and database ****************************//
    /**
     * loads data into the instance countrypath from a database
     * @param countryPath the country you want to get data for
     * @return
     */
    public CountryPath loadCountryData(CountryPath countryPath){
        return db.getPovertyInfo(countryPath);
    }

    /**
     * Adds an observer to the support list
     * @param pcl the observer that wants to get notified when changes are done
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /**
     * Sends the selected country (countrypath) to the observer support
     * @param countryPath the country that was selected
     */
    public void setCountryPath(CountryPath countryPath) {
        support.firePropertyChange("selectedCountryPath", selectedCountryPath, countryPath);
        this.selectedCountryPath = countryPath;
    }

}
