package ph.sms.xenenergy.xenloc.model;

/**
 * Created by xesi on 10/12/2019.
 */

public class Location {
    String sLong;
    String sLat;

    public Location(String sLong, String sLat) {
        this.sLong = sLong;
        this.sLat = sLat;
    }

    public String getsLong() {
        return sLong;
    }

    public void setsLong(String sLong) {
        this.sLong = sLong;
    }

    public String getsLat() {
        return sLat;
    }

    public void setsLat(String sLat) {
        this.sLat = sLat;
    }
}
