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

/**
 * Public class that represents the path of a country with all information about it.
 */
public class CountryPath extends SVGPath{
    private final String NAME;
    private final Locale LOCALE;
    private final Tooltip TOOLTIP;
    private int population;
    private Integer numberOfPoor19Dollar;
    private Integer numberOfPoor32Dollar;
    private Integer numberOfPoor55Dollar;
    private double healthyDietCost;

    // ******************** Constructors **************************************

    /**
     * Constructor that sets the name and content of a country.
     * @param NAME the name
     * @param CONTENT the content
     */
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

    /**
     * Returns the abbreviation of a country.
     * @return the abbreviation.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns the full name of a country.
     * @return the full name.
     */
    public String getDisplayName() {
        return LOCALE.getDisplayCountry();
    }

    /**
     * Sets the population of a country.
     * @param population the population.
     */
    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     * Sets the number of poor people that makes equal or less than 1.9 dollars per day.
     * @param numberOfPoor19Dollar number of poor people that makes equal or less than 1.9 dollars per day.
     */
    public void setNumberOfPoor19Dollar(double numberOfPoor19Dollar) {
        this.numberOfPoor19Dollar = (int)(numberOfPoor19Dollar*1000000);
    }

    /**
     * Sets the number of poor people that makes equal or less than 3.2 dollars per day.
     * @param numberOfPoor32Dollar number of poor people that makes equal or less than 3.2 dollars per day.
     */
    public void setNumberOfPoor32Dollar(double numberOfPoor32Dollar) {
        this.numberOfPoor32Dollar = (int)(numberOfPoor32Dollar*1000000);
    }

    /**
     * Sets the number of poor people that makes equal or less than 5.5 dollars per day.
     * @param numberOfPoor55Dollar number of poor people that makes equal or less than 5.5 dollars per day.
     */
    public void setNumberOfPoor55Dollar(double numberOfPoor55Dollar) {
        this.numberOfPoor55Dollar = (int)(numberOfPoor55Dollar*1000000);
    }


    /**
     * Returns the population of a country.
     * @return the population
     */

    public void setHealthyDietCost(double healthyDietCost) {
        this.healthyDietCost = healthyDietCost;
    }
    public int getPopulation() {
        return population;
    }

    /**
     * Returns the number of poor people that makes equal or less than 1.9 dollars per day.
     * @return the number of poor people that makes equal or less than 1.9 dollars per day.
     */
    public Integer getNumberOfPoor19Dollar() {
        return numberOfPoor19Dollar;
    }

    /**
     * Returns the number of poor people that makes equal or less than 3.2 dollars per day.
     * @return the number of poor people that makes equal or less than 3.2 dollars per day.
     */
    public Integer getNumberOfPoor32Dollar() {
        return numberOfPoor32Dollar;
    }

    /**
     * Returns the number of poor people that makes equal or less than 5.5 dollars per day.
     * @return the number of poor people that makes equal or less than 5.5 dollars per day.
     */
    public Integer getNumberOfPoor55Dollar() {
        return numberOfPoor55Dollar;
    }


    /**
     * Returns the total number of poor people.
     * @return the total number of poor people.
     */

    public double getHealthyDietCost() {
        return healthyDietCost;
    }


    public Integer getPoverty(){
        return numberOfPoor55Dollar;
    }
}