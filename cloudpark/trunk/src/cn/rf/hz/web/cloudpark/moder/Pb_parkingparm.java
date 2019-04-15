package cn.rf.hz.web.cloudpark.moder;

public class Pb_parkingparm extends Pb_parkingparmKey {
    private String parmvalue;

    public String getParmvalue() {
        return parmvalue;
    }

    public void setParmvalue(String parmvalue) {
        this.parmvalue = parmvalue == null ? null : parmvalue.trim();
    }
}