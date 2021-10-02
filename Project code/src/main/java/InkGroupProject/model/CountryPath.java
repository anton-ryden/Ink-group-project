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

import javafx.scene.control.Tooltip;
import javafx.scene.shape.SVGPath;

import java.util.Locale;
import java.util.Observable;

public class CountryPath extends SVGPath{
    private final String NAME;
    private final Locale LOCALE;
    private final Tooltip TOOLTIP;
    private int population;
    private int numberOfPoor19Dollar;
    private int numberOfPoor32Dollar;
    private int numberOfPoor55Dollar;

    // ******************** Constructors **************************************
    public CountryPath(final String NAME, final String CONTENT) {
        super();
        this.NAME = NAME;
        Locale.setDefault(Locale.ENGLISH);
        this.LOCALE = new Locale("", NAME);
        this.TOOLTIP = new Tooltip(LOCALE.getDisplayCountry());
        Tooltip.install(this, TOOLTIP);
        if (null == CONTENT) return;
        setContent(CONTENT);
    }

    // ******************** Methods *******************************************
    public String getName() {
        return NAME;
    }

    public String getDisplayName() {
        return LOCALE.getDisplayCountry();
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setNumberOfPoor19Dollar(double numberOfPoor19Dollar) {
        this.numberOfPoor19Dollar = (int)(numberOfPoor19Dollar*1000000);
    }

    public void setNumberOfPoor32Dollar(double numberOfPoor32Dollar) {
        this.numberOfPoor32Dollar = (int)(numberOfPoor32Dollar*1000000);
    }

    public void setNumberOfPoor55Dollar(double numberOfPoor55Dollar) {
        this.numberOfPoor55Dollar = (int)(numberOfPoor55Dollar*1000000);
    }

    public int getPopulation() {
        return population;
    }

    public int getNumberOfPoor19Dollar() {
        return numberOfPoor19Dollar;
    }

    public int getNumberOfPoor32Dollar() {
        return numberOfPoor32Dollar;
    }

    public int getNumberOfPoor55Dollar() {
        return numberOfPoor55Dollar;
    }

    public int getPoverty(){
        return numberOfPoor55Dollar;
    }
}