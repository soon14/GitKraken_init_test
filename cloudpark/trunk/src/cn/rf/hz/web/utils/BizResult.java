package cn.rf.hz.web.utils;

public class BizResult {

    public static final BizResult SUCCESS_RESULT = new BizResult();

    private boolean               success;
    private String                code;
    private String                msg;
    private Object                data;

    public BizResult() {
        this(true);
    }

    public BizResult(boolean success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public BizResult(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public BizResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
