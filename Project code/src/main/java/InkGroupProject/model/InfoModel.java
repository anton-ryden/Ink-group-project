package InkGroupProject.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.*;

//48 = data value on year 2018

public class InfoModel {
    private static File file = new File("./src/main/resources/InkGroupProject/model/PovStatsData.csv");
    private static String country;
    private static String population;
    private static String[] poverty = new String[3];

    public static String getInfo(String c, String by) throws FileNotFoundException {
        List<String> result = Arrays.asList(".ERROR".split("\","));;
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            List<String> data = Arrays.asList(line.split("\","));
            if(data.get(0).equals("\""+c) && data.get(2).equals("\""+by)) {
                int i = (data.size() - 1);
                while(i > 4) {
                    if (!data.get(i).substring(1).equals("")) {
                        return data.get(i).substring(1);
                    }
                    i--;
                };
            }

        }
        return "Error - No data found";

    }

    public static void updateInfo(String c) throws FileNotFoundException {
        country = c;
        population = getInfo(c,"Population, total");
        poverty[0] = getInfo(c, "Number of poor at $1.90 a day (2011 PPP) (millions)");
        poverty[1] = getInfo(c, "Number of poor at $3.20 a day (2011 PPP) (millions)");
        poverty[2] = getInfo(c, "Number of poor at $5.50 a day (2011 PPP) (millions)");
    }




    public static String getCountry() {
        return country;
    }

    public static String getPopulation() {
        return population;
    }

    public static String getPoverty(int i) {
        return poverty[i];
    }

    public static Map<String, List<CountryPath>> addData(Map<String, List<CountryPath>> countryPaths) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        Map<String, String> nameToAbbr = new HashMap<>();
        for (String abbr : countryPaths.keySet()) {
            nameToAbbr.put(countryPaths.get(abbr).get(0).getDisplayName(), abbr);
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            List<String> data = Arrays.asList(line.split("\","));
            String dataCountry = data.get(0).substring(1);
            String dataBy = data.get(2).substring(1);

            if(countryPaths.containsKey(nameToAbbr.get(dataCountry)) && dataBy.equals("Number of poor at $1.90 a day (2011 PPP) (millions)")) {
                int i = data.size()-1;
                while (i > 4) {
                    if (!data.get(i).substring(1).equals("")) {
                        countryPaths.get(nameToAbbr.get(dataCountry)).get(0).setPoverty(data.get(i).substring(1));
                        break;
                    }
                    i--;
                }
            } else if(countryPaths.containsKey(nameToAbbr.get(dataCountry)) && dataBy.equals("Population, total")) {
                int i = data.size()-1;
                while (i > 4) {
                    if (!data.get(i).substring(1).equals("")) {
                        countryPaths.get(nameToAbbr.get(dataCountry)).get(0).setPopulation(data.get(i).substring(1));
                        break;
                    }
                    i--;
                }
            }
        }
        return countryPaths;
    };

}
