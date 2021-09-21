package InkGroupProject;

import java.sql.*;

public class DatabaseTest {
    public static void main(String[] args) throws SQLException {
        // Reads from src/resources but writes to build/resources
        Connection connection = DriverManager.getConnection("jdbc:sqlite::resource:InkGroupProject/db/database.db");
        Statement query = connection.createStatement();

        query.execute("CREATE TABLE accounts (id int, username varchar(255));");
        query.execute("INSERT INTO accounts VALUES (1, 'test')");
        ResultSet result = query.executeQuery("SELECT * FROM accounts");
        while (result.next()) {
            System.out.println(result.getString("username"));
        }
    }
}
