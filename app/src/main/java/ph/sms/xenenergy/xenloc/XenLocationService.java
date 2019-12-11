package ph.sms.xenenergy.xenloc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;



public class XenLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "ph.sms.xenenergy.xenloc.UPDATE_LOCATION";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            System.out.println(action);
            if (ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    String location_string = new StringBuilder(""+location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();
                    try {
                        MainActivity.getInstance().updateLocationBg(location_string);

                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
