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

    /**
     *
     * @param NAME
     * @param CONTENT
     */
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

    /**
     * Return name
     * @return
     */
    // ******************** Methods *******************************************
    public String getName() {
        return NAME;
    }

    /**
     * Returns the countries name.
     * @return
     */
    public String getDisplayName() {
        return LOCALE.getDisplayCountry();
    }

    /**
     * Sets the population of a country from a database.
     * @param population
     */
    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     *Sets the percentage of people living under 1.9 dollars and multiplies it by 1000000 to get the population.
     * @param numberOfPoor19Dollar
     */
    public void setNumberOfPoor19Dollar(double numberOfPoor19Dollar) {
        this.numberOfPoor19Dollar = (int)(numberOfPoor19Dollar*1000000);
    }

    /**
     *Sets the percentage of people living under 3.2 dollars and multiplies it by 1000000 to get the population.
     * @param numberOfPoor32Dollar
     */
    public void setNumberOfPoor32Dollar(double numberOfPoor32Dollar) {
        this.numberOfPoor32Dollar = (int)(numberOfPoor32Dollar*1000000);
    }

    /**
     *Sets the percentage of people living under 5.5 dollars and multiplies it by 1000000 to get the population.
     * @param numberOfPoor55Dollar
     */
    public void setNumberOfPoor55Dollar(double numberOfPoor55Dollar) {
        this.numberOfPoor55Dollar = (int)(numberOfPoor55Dollar*1000000);
    }

    /**
     * Grabs the population of a country
     * @return
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Returns the population of people living under 1.9 dollars.
     * @return
     */
    public int getNumberOfPoor19Dollar() {
        return numberOfPoor19Dollar;
    }

    /**
     * Returns the population of people living under 3.2 dollars.
     * @return
     */
    public int getNumberOfPoor32Dollar() {
        return numberOfPoor32Dollar;
    }

    /**
     * Returns the population of people living under 5.5 dollars.
     * @return
     */
    public int getNumberOfPoor55Dollar() {
        return numberOfPoor55Dollar;
    }

    /**
     * Returns the population of people living under 5.5 dollars.
     * @return
     */
    public int getPoverty(){
        return numberOfPoor55Dollar;
    }
}