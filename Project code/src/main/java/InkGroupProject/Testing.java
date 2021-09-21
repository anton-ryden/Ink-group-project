package InkGroupProject;

import InkGroupProject.model.Database;

public class Testing {
    public static void main(String[] args) {
        Database db = new Database(":resource:InkGroupProject/db/database.db");
        db.createAccount("Lukas", "Gartman", "gusgartlu@student.gu.se", "hejhej123");
        db.createAccount("test", "test", "test@test.com", "holaespanola");
        db.getAccount("gusgartlu@student.gu.se");
        db.getAccount("test@test.com");
        System.out.println("\nLogin test:");
        System.out.println(db.login("gusgartlu@student.gu.se", "hejhej1223"));
    }
}
