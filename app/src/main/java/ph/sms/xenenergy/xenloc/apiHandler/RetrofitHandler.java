package ph.sms.xenenergy.xenloc.apiHandler;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import ph.sms.xenenergy.xenloc.interfaces.APIHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xenuser on 3/15/2017.
 */
public class RetrofitHandler {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private static final String TAG = "RETROFITHANDLER";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context context;
    private APIHandler handler;

    public RetrofitHandler(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.handler = ServiceGenerator.createService(APIHandler.class);
    }

    public RetClassGen getLocations(String urlPath) {
        RetClassGen retClassGen = new RetClassGen();
        Call<RetClassGen> call = handler.getLocations(urlPath);
        try {
            retClassGen = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retClassGen;
    }


}
