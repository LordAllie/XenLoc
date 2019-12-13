package ph.sms.xenenergy.xenloc.interfaces;


import android.os.Message;

/**
 * Created by xenuser on 3/20/2017.
 */
public interface MasterIdCallBack {
    void onSuccess(int id);
    void onSuccess(Message message);
    void onError(String message);
}
