package ph.sms.xenenergy.xenloc.model;

/**
 * Created by xesi on 12/12/2019.
 */

public class ChatRecord {
    String message;
    long time;
    String email;

    public ChatRecord() {
    }

    public ChatRecord(String message, long time, String email) {
        this.message = message;
        this.time = time;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
