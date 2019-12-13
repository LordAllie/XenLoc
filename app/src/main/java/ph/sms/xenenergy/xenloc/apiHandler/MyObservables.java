package ph.sms.xenenergy.xenloc.apiHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import ph.sms.xenenergy.xenloc.interfaces.APIHandler;

/**
 * Created by Daryll Sabate on 12/27/2017.
 */

public class MyObservables {
    private Context context;
    private APIHandler handler;
    private ByteBuffer buffer;

    public MyObservables(APIHandler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

}