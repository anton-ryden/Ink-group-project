package InkGroupProject;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BCryptTest {
    public BCryptTest() {
        String password = "1234";
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString);
    }
}
