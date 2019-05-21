package cn.rf.hz.web.cloudpark.moder;

public class Pb_productKey {
    private Integer productid;

    private String parkinglotno;

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

	@Override
	public String toString() {
		return "Pb_productKey [productid=" + productid + ", parkinglotno=" + parkinglotno + "]";
	}
    
    
}