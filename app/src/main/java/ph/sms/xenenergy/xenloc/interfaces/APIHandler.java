package ph.sms.xenenergy.xenloc.interfaces;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import ph.sms.xenenergy.xenloc.apiHandler.RetClassGen;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by xenuser on 2/2/2017.
 */
public interface APIHandler {

    @GET("{urlPath}")
    Call<RetClassGen> getLocations(@Path("urlPath") String urlPath);

}
