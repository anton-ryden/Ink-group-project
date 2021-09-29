package InkGroupProject.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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

}
