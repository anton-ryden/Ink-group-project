package InkGroupProject.model;

import java.sql.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class Database {
    private Connection connection;

    public Database(String path) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void createAccount(String firstName, String lastName, String email, String password) {
        System.out.println("Creating account...");
        try {
            PreparedStatement insertInfo = connection.prepareStatement("INSERT INTO accounts (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
            insertInfo.setString(1, firstName);
            insertInfo.setString(2, lastName);
            insertInfo.setString(3, email);
            insertInfo.setString(4, encryptPassword(password));
            insertInfo.execute();
        } catch (SQLException ex) {
            System.out.println("something went wrong...");
            System.out.println(ex.getMessage());
        }
    }

    private String encryptPassword(String password) {
        String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        return encryptedPassword;
    }

    public void getAccount(String email) {
        try {
            Statement query = connection.createStatement();
            ResultSet result = query.executeQuery("SELECT * FROM accounts WHERE email = '" + email + "'");
            while (result.next()) {
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String _email = result.getString("email");
                String registerDate = result.getString("date_created");
                System.out.println("Name: " + firstName + " " + lastName + "\nEmail: " + _email);
                System.out.println("Signed up on " + registerDate);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean login(String email, String password) {
        boolean result = false;
        try {
            Statement query = connection.createStatement();
            ResultSet encrypted_password = query.executeQuery("SELECT password FROM accounts WHERE email = '" + email + "'");
            BCrypt.Result bcrypt_result = BCrypt.verifyer().verify(password.toCharArray(), encrypted_password.getString("password"));
            if (bcrypt_result.verified)
                result = true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return result;
    }
}
