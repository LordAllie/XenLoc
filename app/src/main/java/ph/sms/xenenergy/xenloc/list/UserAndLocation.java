package ph.sms.xenenergy.xenloc.list;

/**
 * Created by Daryll-POGI on 12/12/2019.
 */

public class UserAndLocation {

    private long user_id;
    private String user_name;
    private String user_marker;
    private String vatar;
    private double user_long;
    private double user_lat;
    private int state;

    public UserAndLocation(){}

    public UserAndLocation(long user_id, String user_name, String user_marker, String vatar, double user_long, double user_lat, int state) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_marker = user_marker;
        this.vatar = vatar;
        this.user_long = user_long;
        this.user_lat = user_lat;
        this.state = state;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_marker() {
        return user_marker;
    }

    public void setUser_marker(String user_marker) {
        this.user_marker = user_marker;
    }

    public String getVatar() {
        return vatar;
    }

    public void setVatar(String vatar) {
        this.vatar = vatar;
    }

    public double getUser_long() {
        return user_long;
    }

    public void setUser_long(double user_long) {
        this.user_long = user_long;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(double user_lat) {
        this.user_lat = user_lat;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
