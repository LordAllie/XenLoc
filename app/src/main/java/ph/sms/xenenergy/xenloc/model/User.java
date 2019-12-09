package ph.sms.xenenergy.xenloc.model;

/**
 * Created by xesi on 09/12/2019.
 */

public class User {
    String username;
    String email;
    String age;
    String password;
    String image;

    public User(String username, String email, String age, String password, String image) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.password = password;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
