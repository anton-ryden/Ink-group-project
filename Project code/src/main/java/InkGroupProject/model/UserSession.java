package InkGroupProject.model;

public final class UserSession {
    private static UserSession instance = null;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static void login(User user) {
        if (instance == null)
            instance = new UserSession(user);
    }

    public void logout() {
        user = null;
        instance = null;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public int getId() {
        return user.getId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getDateCreated() {
        return user.getDateCreated();
    }

    public boolean isLoggedIn() {
        return instance != null;
    }
}
