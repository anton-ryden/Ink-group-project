package InkGroupProject.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateCreated;

    public User(int id, String firstName, String lastName, String email, String dateCreated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
