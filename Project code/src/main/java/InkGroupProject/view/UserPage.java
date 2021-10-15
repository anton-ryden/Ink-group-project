package InkGroupProject.view;

import InkGroupProject.model.Database;
import InkGroupProject.model.UserSession;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.HashMap;

public class UserPage extends GridPane {
    private Database db = Database.getInstance(":resource:InkGroupProject/db/database.db");

    public void update() {
        int user_id = UserSession.getInstance().getId();
        this.getChildren().subList(2, this.getChildren().size()).clear();
        Text totalDonatedMoney = new Text("Total amount of money donated:\n$" + db.getTotalDonatedMoneyByUser(user_id));
        this.add(totalDonatedMoney,0,1);
        HashMap<String, Integer> donations = db.getDonatedMoneyByUser(user_id);
        int counter = 0;
        for(String country : donations.keySet()) {
            Text donationText = new Text(country + " : $" + String.valueOf(donations.get(country)) + " which equals "+ String.valueOf((int)(donations.get(country)/db.getDietCost(country))) +" healthy meals");
            this.add(donationText,0,2+counter);
            counter++;
        }
    }
}
