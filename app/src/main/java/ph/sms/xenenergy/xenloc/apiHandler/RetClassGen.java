package ph.sms.xenenergy.xenloc.apiHandler;

import java.util.List;

/**
 * Created by Daryll Sabate on 7/31/2017.
 */
public class RetClassGen<T> {
    private int respCode;
    private String respMsg;
    private String tableName;
    private T resposeBody;
    private List<T> responseBodyList;

    public RetClassGen() {
    }

    public RetClassGen(int respCode, String respMsg, String tableName, T resposeBody, List<T> responseBodyList) {
        this.respCode = respCode;
        this.respMsg = respMsg;
        this.tableName = tableName;
        this.resposeBody = resposeBody;
        this.responseBodyList = responseBodyList;
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public T getResposeBody() {
        return resposeBody;
    }

    public void setResposeBody(T resposeBody) {
        this.resposeBody = resposeBody;
    }

    public List<T> getResponseBodyList() {
        return responseBodyList;
    }

    public void setResponseBodyList(List<T> responseBodyList) {
        this.responseBodyList = responseBodyList;
    }
}
