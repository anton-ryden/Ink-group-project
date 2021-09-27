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

package InkGroupProject.model;

import InkGroupProject.controller.World;
import InkGroupProject.controller.World.Resolution;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class WorldBuilder<B extends WorldBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();
    private Resolution                resolution = Resolution.HI_RES;

    // ******************** Constructors **************************************
    protected WorldBuilder() {}

    // ******************** Methods *******************************************
    public static final WorldBuilder create() { return new WorldBuilder(); }

    public final B resolution(final Resolution RESOLUTION) {
        resolution = RESOLUTION;
        return (B)this;
    }

    public final B hoverEnabled(final boolean ENABLED) {
        properties.put("hoverEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B selectionEnabled(final boolean ENABLED) {
        properties.put("selectionEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B zoomEnabled(final boolean ENABLED) {
        properties.put("zoomEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B fadeColors(final boolean FADE_COLORS) {
        properties.put("fadeColors", new SimpleBooleanProperty(FADE_COLORS));
        return (B)this;
    }

    public final World build() {
        double              eventRadius         = properties.containsKey("eventRadius") ? ((DoubleProperty) properties.get("eventRadius")).get() : 5;
        boolean             fadeColors          = properties.containsKey("fadeColors") ? ((BooleanProperty) properties.get("fadeColors")).get() : false;
        final World CONTROL = new World(resolution, eventRadius, fadeColors);

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMinSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMaxSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
                CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if ("padding".equals(key)) {
                CONTROL.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
            } else if ("backgroundColor".equals(key)) {
                CONTROL.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("fillColor".equals(key)) {
                CONTROL.setFillColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("strokeColor".equals(key)) {
                CONTROL.setStrokeColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("hoverColor".equals(key)) {
                CONTROL.setHoverColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("pressedColor".equals(key)) {
                CONTROL.setPressedColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("selectedColor".equals(key)) {
                CONTROL.setSelectedColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("locationColor".equals(key)) {
                CONTROL.setLocationColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("hoverEnabled".equals(key)) {
                CONTROL.setHoverEnabled(((BooleanProperty) properties.get(key)).get());
            } else if ("selectionEnabled".equals(key)) {
                CONTROL.setSelectionEnabled(((BooleanProperty) properties.get(key)).get());
            } else if ("zoomEnabled".equals(key)) {
                CONTROL.setZoomEnabled(((BooleanProperty) properties.get(key)).get());
            } else if ("mouseEnterHandler".equals(key)) {
                CONTROL.setMouseEnterHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("mousePressHandler".equals(key)) {
                CONTROL.setMousePressHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("mouseReleaseHandler".equals(key)) {
                CONTROL.setMouseReleaseHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("mouseExitHandler".equals(key)) {
                CONTROL.setMouseExitHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
