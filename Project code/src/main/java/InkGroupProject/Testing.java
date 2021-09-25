package InkGroupProject;

import InkGroupProject.model.Database;
import InkGroupProject.model.User;
import InkGroupProject.model.UserSession;

public class Testing {
    public static void main(String[] args) {
        Database db = new Database(":resource:InkGroupProject/db/database.db");

        new Test();
        UserSession.login(new User(1, "f", "f", "f", "2021-09-21 17:05:07"));
        UserSession user = UserSession.getInstance();
        Test lol = new Test();
        System.out.println(user == lol.yo);
    }
}

class Test {
    UserSession yo;
    Test() {
        this.yo = UserSession.getInstance();
    }
}