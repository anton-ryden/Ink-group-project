package InkGroupProject.model;

import java.sql.*;

import InkGroupProject.view.UserSettings;
import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 *
 *
 * Connecting the database and create queries
 */
public class Database {
    private static Database database;
    private Connection connection;

    /**
     *Connecting to the database
     * @param path the path to the database
     */
    private Database(String path) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     *Returns the instance of the database
     * @param path the path to the database
     * @return the database path
     */
    public static Database getInstance(String path) {
        if (database == null)
            database = new Database(path);
        return database;
    }

    /**
     *Creates an account
     * @param firstName the first name of the account
     * @param lastName the last name of the account
     * @param email the email of the account
     * @param password the password of the account
     * @return true or false depending if the account was created or not
     */
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

    public boolean createDonation(int user, String country, int value) {
        try {
            PreparedStatement insertInfo = connection.prepareStatement("INSERT INTO donations (user_id, country, value) VALUES (?, ?, ?)");
            insertInfo.setInt(1, user);
            insertInfo.setString(2, country);
            insertInfo.setInt(3, value);
            insertInfo.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public int getTotalDonatedMoney(String country) {
        int res = 0;
        try {
            PreparedStatement query = connection.prepareStatement("SELECT SUM(value) FROM donations WHERE country = ?");
            query.setString(1, country);
            ResultSet result = query.executeQuery();

            res = result.getInt("SUM(value)");

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }

    /**
     *Encrypts the password
     * @param password the password to encrypt
     * @return returns password but encrypted
     */
    private String encryptPassword(String password) {
        String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        return encryptedPassword;
    }


    /**
     *Get the account information
     * @param email the email of the account
     * @return returns account information
     */
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

    /**
     *Trying to log in
     * @param email the email of the account
     * @param password the password of the account
     * @return returns true or false depending on if the entered accounts is logged in or not
     */
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

    /**
     * It gets the data from the database for countryPath
     * @param countryPath the country you want information of
     * @return The updated countryPath with the new information
     */
    public CountryPath getPovertyInfo(CountryPath countryPath) {
        String countryName = countryPath.getDisplayName();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM poverty_stats WHERE country_name = ?");
            query.setString(1, countryName);
            ResultSet result = query.executeQuery();
            countryPath.setPopulation(result.getInt("population"));
            Double temp = result.getDouble("num_of_poor_1_9");
            if (!result.wasNull()){
                countryPath.setNumberOfPoor19Dollar(temp);
            }
            temp = result.getDouble("num_of_poor_3_2");
            if (!result.wasNull()){
                countryPath.setNumberOfPoor32Dollar(temp);
            }
            temp = result.getDouble("num_of_poor_5_5");
            if (!result.wasNull()){
                countryPath.setNumberOfPoor55Dollar(temp);
            }
            // Temporary until healthy_diet_cost has been merged into poverty_stats
            countryPath.setHealthyDietCost(result.getDouble("cost"));
        } catch (SQLException ex) {
            return countryPath;
        }
        return countryPath;
    }

    /**
     *
     * @param country the country that's showing
     * @return the cost of the meals for a healthy diet for a day in a certain country
     */
    public double getDietCost (String country) {
        double healthyDietCost;
        try {
            PreparedStatement query = connection.prepareStatement("SELECT cost FROM poverty_stats WHERE country = ?");
            query.setString(1, country);
            ResultSet result = query.executeQuery();
            healthyDietCost = result.getDouble("cost");
        } catch (SQLException ex) {
            healthyDietCost = -1;
        }

        return healthyDietCost;
    }

    public boolean updatePassword(String newPassword, int user_id) {
        try {
            PreparedStatement updatePassword = connection.prepareStatement("UPDATE accounts SET password =?  WHERE id =?");
            updatePassword.setString(1, encryptPassword(newPassword));
            updatePassword.setInt(2, user_id);
            updatePassword.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateUserInfo(String firstName, String lastName, String email, int user_id) {
        try {
            PreparedStatement updateUserInfo = connection.prepareStatement("UPDATE accounts SET first_name =?, last_name =?, email =? WHERE id =?");
            updateUserInfo.setString(1, firstName);
            updateUserInfo.setString(2, lastName);
            updateUserInfo.setString(3, email);
            updateUserInfo.setInt(4, user_id);
            updateUserInfo.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

}
