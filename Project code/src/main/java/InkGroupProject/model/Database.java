package InkGroupProject.model;

import java.sql.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class Database {
    private static Database database;
    private Connection connection;

    private Database(String path) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static Database getInstance(String path) {
        if (database == null)
            database = new Database(path);
        return database;
    }

    public boolean createAccount(String firstName, String lastName, String email, String password) {
        try {
            PreparedStatement insertInfo = connection.prepareStatement("INSERT INTO accounts (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
            insertInfo.setString(1, firstName);
            insertInfo.setString(2, lastName);
            insertInfo.setString(3, email);
            insertInfo.setString(4, encryptPassword(password));
            insertInfo.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    private String encryptPassword(String password) {
        String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        return encryptedPassword;
    }

    public User getAccount(String email) {
        User account = null;
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM accounts WHERE email = ?");
            query.setString(1, email);
            ResultSet result = query.executeQuery();

            int id = result.getInt("id");
            String firstName = result.getString("first_name");
            String lastName = result.getString("last_name");
            String _email = result.getString("email");
            String registerDate = result.getString("date_created");

            account = new User(id, firstName, lastName, _email, registerDate);

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return account;
    }

    public boolean login(String email, String password) {
        boolean isLoggedIn = false;
        try {
            PreparedStatement query = connection.prepareStatement("SELECT password FROM accounts WHERE email = ?");
            query.setString(1, email);
            ResultSet result = query.executeQuery();
            String encrypted_password = result.getString("password");

            BCrypt.Result bcrypt_result = BCrypt.verifyer().verify(password.toCharArray(), encrypted_password);
            if (bcrypt_result.verified)
                isLoggedIn = true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return isLoggedIn;
    }

    public CountryPath getPoveryInfo(CountryPath countryPath) {
        String countryName = countryPath.getDisplayName();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM poverty_stats WHERE country_name = ?");
            query.setString(1, countryName);
            ResultSet result = query.executeQuery();
            countryPath.setPopulation(result.getInt("population"));
            countryPath.setNumberOfPoor19Dollar(result.getDouble("num_of_poor_1_9"));
            countryPath.setNumberOfPoor32Dollar(result.getDouble("num_of_poor_3_2"));
            countryPath.setNumberOfPoor55Dollar(result.getDouble("num_of_poor_5_5"));
        } catch (SQLException ex) {
            return countryPath;
        }
        return countryPath;
    }
}
